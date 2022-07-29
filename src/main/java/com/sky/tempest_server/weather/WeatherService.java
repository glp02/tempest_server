package com.sky.tempest_server.weather;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import com.sky.tempest_server.weather.MeteomaticsAPIService;
import com.sky.tempest_server.weather.entities.WeatherDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WeatherService {
    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectReader readJsonArrayToJsonNodeList = mapper.readerFor(new TypeReference<List<JsonNode>>() {});

    static final String METEOMATICS_ENDPOINT = "api.meteomatics.com";

    @Autowired
    private final MeteomaticsAPIService meteomaticsAPIService;

    public WeatherService(MeteomaticsAPIService meteomaticsAPIService) {
        this.meteomaticsAPIService = meteomaticsAPIService;
    }


    public JsonNode getWeatherByLatLong(double latitude, double longitude, String datetime) throws IOException {
//    BUILD URL WITH QUERY PARAMETERS
            String queryUrlParams = UriComponentsBuilder.fromPath("")
                    .queryParam("term", "asdas")
                    .queryParam("location_types", "airport")
                    .encode()
                    .toUriString();
        String url = "https://api.meteomatics.com/" + datetime + "--2022-08-01T00:00:00Z:PT1H/t_2m:C,wind_speed_10m:ms/" + latitude + "," + longitude + "/json?model=mix";
        HttpEntity<String> meteomaticsResponse = meteomaticsAPIService.getRequestResponse(url, queryUrlParams);


        //MANIPULATE JSON RESPONSE
        JsonNode responseJSON = mapper.readValue(meteomaticsResponse.getBody(), JsonNode.class);
        JsonNode weatherJSON = responseJSON.get("data");
//        List<JsonNode> weatherList = readJsonArrayToJsonNodeList.readValue(weatherJSON);
        return weatherJSON;
    }
}

