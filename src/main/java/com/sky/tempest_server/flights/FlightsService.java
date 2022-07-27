package com.sky.tempest_server.flights;

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
import java.util.HashMap;
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

    public List<Airport> searchAirports(String searchText) {
        String url = "https://tequila-api.kiwi.com/locations/query";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
//        headers.set("Accept-Encoding", "gzip");
        headers.set("apikey","APIKEYHERE");
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
        ObjectReader reader = mapper.readerFor(new TypeReference<List<JsonNode>>() {
        });
        List<JsonNode> locationsList = reader.readValue(locationsJSON);
        List<Airport> airportList = locationsList.stream().map((locationNode) -> {
            return new Airport(locationNode.get("name").textValue(), locationNode.get("code").textValue());
        }).collect(Collectors.toList());

        return airportList;


        //        String url = "https://tequila-api.kiwi.com/locations/query?term={term}&location_types={locationTypes}";
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("apikey", System.getenv("VxTTadDfT5FeAq8LIvddlLs6LrwNk-aH"));
//        HttpEntity<?> entity = new HttpEntity<>(headers);
//
//        //get response as a string
//        ResponseEntity<String> response = restTemplate.exchange(
//                url, HttpMethod.GET, entity, String.class, searchText, "airport");


        }
//    public String searchAirports(String searchText) {
//        String url = "https://jsonplaceholder.typicode.com/posts";
//        return this.restTemplate.getForObject(url, String.class);
//
    }
