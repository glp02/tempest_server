package com.sky.tempest_server.flights;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.sky.tempest_server.flights.entities.Airport;

import com.sky.tempest_server.flights.entities.Flight;
import com.sky.tempest_server.weather.entities.WeatherDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
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
public class WeatherService {
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectReader readJsonArrayToJsonNodeList = mapper.readerFor(new TypeReference<List<JsonNode>>() {});

    @Value("${openweather.apikey}")
    String openWeatherApiKey;

    @Autowired
    public WeatherService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }


    public WeatherDTO getWeatherByLatLong(double latitude, double longitude, String datetime) throws IOException {
        //SET API KEY IN HEADER
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
//        headers.set("Accept-Encoding", "gzip");
        HttpEntity<?> entity = new HttpEntity<>(headers);

        //BUILD URL WITH QUERY PARAMETERS
        String url = "https://tequila-api.kiwi.com/locations/query";
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
                .queryParam("appid", openWeatherApiKey)
                .encode()
                .toUriString();

        //GENERATE MAP OF PARAMETERS
        Map<String, Object> params = new HashMap<>();
        params.put("lat", latitude);
        params.put("lon", longitude);
        params.put("appid", openWeatherApiKey);

        //GET JSON RESPONSE AS STRING
        HttpEntity<String> response = restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                entity,
                String.class,
                params);


        //MANIPULATE JSON RESPONSE
        JsonNode responseJSON = mapper.readValue(response.getBody(), JsonNode.class);
        JsonNode weatherJSON = responseJSON.get("list");
        List<JsonNode> weatherList = readJsonArrayToJsonNodeList.readValue(weatherJSON);
        return null;
    }
}

