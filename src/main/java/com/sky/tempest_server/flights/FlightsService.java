package com.sky.tempest_server.flights;

import com.sky.tempest_server.flights.entities.Airport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FlightsService {
    private final RestTemplate restTemplate;

    @Autowired
    public FlightsService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public HttpEntity<String> searchAirports(String searchText) {
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
        return response;


}
//    public String searchAirports(String searchText) {
//        String url = "https://jsonplaceholder.typicode.com/posts";
//        return this.restTemplate.getForObject(url, String.class);
//
//    }


}
