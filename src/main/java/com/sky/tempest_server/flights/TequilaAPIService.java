package com.sky.tempest_server.flights;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
class TequilaAPIService {
    @Value("${tequila.apikey}")
    String TEQUILA_API_KEY;
    
    private final RestTemplate restTemplate;

    @Autowired
    TequilaAPIService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    HttpEntity<String> getRequestResponse(String url, String queryUrlParams){
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set("apikey",TEQUILA_API_KEY);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        //GET JSON RESPONSE AS STRING
        return restTemplate.exchange(
                url +  queryUrlParams,
                HttpMethod.GET,
                entity,
                String.class);
    }
    
}
