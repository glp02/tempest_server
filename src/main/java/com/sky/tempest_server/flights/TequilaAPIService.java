package com.sky.tempest_server.flights;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
class TequilaAPIService {
    static final String TEQUILA_URL = "https://tequila-api.kiwi.com/locations/query";
    private static final String TEQUILA_API_KEY = "API_KEY_HERE";
    
    private final RestTemplate restTemplate;

    @Autowired
    TequilaAPIService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }


    HttpEntity<String> getRequestResponse(String queryUrlParams, Map<String,String> uriVariables){
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
//        headers.set("Accept-Encoding", "gzip");
        headers.set("apikey",TEQUILA_API_KEY);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        //GET JSON RESPONSE AS STRING
        return restTemplate.exchange(
                TEQUILA_URL +  queryUrlParams,
                HttpMethod.GET,
                entity,
                String.class,
                uriVariables);
    }
    
}
