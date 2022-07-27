package com.sky.tempest_server.flights;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.sky.tempest_server.flights.entities.Airport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FlightsService {
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectReader readJsonArrayToJsonNodeList = mapper.readerFor(new TypeReference<List<JsonNode>>() {
    });

    @Autowired
    public FlightsService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public List<Airport> searchAirports(String searchText) throws IOException {
        String url = "https://tequila-api.kiwi.com/locations/query";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
//        headers.set("Accept-Encoding", "gzip");
        headers.set("apikey","API_KEY_GOES_HERE");
        HttpEntity<?> entity = new HttpEntity<>(headers);

        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("term", searchText)
                .queryParam("location_types", "airport")
                .encode()
                .toUriString();

        Map<String, String> params = new HashMap<>();
        params.put("term", searchText);
        params.put("location_types", "airport");

        HttpEntity<String> response = restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                entity,
                String.class,
                params);

        JsonNode responseJSON = mapper.readValue(response.getBody(), JsonNode.class);
        JsonNode locationsJSON = responseJSON.get("locations");
        List<JsonNode> locationsList = readJsonArrayToJsonNodeList.readValue(locationsJSON);
        List<Airport> airportList = locationsList.stream().map((locationNode) -> new Airport(
                locationNode.get("name").textValue(),
                locationNode.get("code").textValue(),
                locationNode.get("city").get("name").textValue(),
                locationNode.get("city").get("country").get("name").textValue()
            )).collect(Collectors.toList());

        return airportList;
        }

    }
