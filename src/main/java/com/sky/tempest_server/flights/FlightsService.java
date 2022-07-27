package com.sky.tempest_server.flights;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.sky.tempest_server.flights.entities.Airport;
import com.sky.tempest_server.flights.entities.AirportJSON;
import io.swagger.v3.core.util.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.SQLOutput;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FlightsService {
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public FlightsService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public List<Airport> searchAirports(String searchText) throws Exception {
        String url = "https://tequila-api.kiwi.com/locations/query?term={term}&location_types={locationTypes}";
        HttpHeaders headers = new HttpHeaders();
        System.out.println(System.getenv("TEQUILA_API_KEY"));
        headers.set("apikey", System.getenv("TEQUILA_API_KEY"));
        HttpEntity<?> entity = new HttpEntity<>(headers);

        //get response as a string
        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, String.class, searchText, "airport");

        JsonNode responseJSON = mapper.readValue(response.getBody(), JsonNode.class);
        JsonNode locationsJSON = responseJSON.get("locations");
        ObjectReader reader = mapper.readerFor(new TypeReference<List<JsonNode>>() {
        });
        List<JsonNode> locationsList = reader.readValue(locationsJSON);
        List<Airport> airportList = locationsList.stream().map((locationNode) -> {
            return new Airport(locationNode.get("name").textValue(), locationNode.get("code").textValue());
        }).collect(Collectors.toList());

        return airportList;
    }

}
