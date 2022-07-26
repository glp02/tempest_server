package com.sky.tempest_server.flights;

import com.sky.tempest_server.flights.entities.Airport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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

    public ResponseEntity<String> searchAirports(String searchText) {
        String url = "https://tequila-api.kiwi.com/locations/query";
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey","keygoeshere");
        HttpEntity<?> entity = new HttpEntity<>(headers);

        Map<String, String> params = new HashMap<>();
        params.put("term", searchText);
        params.put("location_types", "airport");

       ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, String.class, params);

       return response;

}
//    public String searchAirports(String searchText) {
//        String url = "https://jsonplaceholder.typicode.com/posts";
//        return this.restTemplate.getForObject(url, String.class);
//
//    }


}
