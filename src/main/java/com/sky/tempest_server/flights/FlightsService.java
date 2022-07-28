package com.sky.tempest_server.flights;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.sky.tempest_server.flights.entities.Airport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FlightsService {
    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectReader readJsonArrayToJsonNodeList = mapper.readerFor(new TypeReference<List<JsonNode>>() {});

    @Autowired
    private TequilaAPIService tequilaAPIService;

    public List<Airport> searchAirports(String searchText) throws IOException {

        //BUILD URL WITH QUERY PARAMETERS
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(TequilaAPIService.TEQUILA_URL)
                .queryParam("term", searchText)
                .queryParam("location_types", "airport")
                .encode()
                .toUriString();

        //GENERATE MAP OF PARAMETERS
        Map<String, String> params = new HashMap<>();
        params.put("term", searchText);
        params.put("location_types", "airport");


        HttpEntity<String> tequilaResponse = tequilaAPIService.getRequestResponse(urlTemplate,params);

        //MANIPULATE JSON RESPONSE
        JsonNode responseJSON = mapper.readValue(tequilaResponse.getBody(), JsonNode.class);
        JsonNode locationsJSON = responseJSON.get("locations");
        List<JsonNode> locationsList = readJsonArrayToJsonNodeList.readValue(locationsJSON);

        return locationsList.stream().map((locationNode) -> new Airport(
                locationNode.get("name").textValue(),
                locationNode.get("code").textValue(),
                locationNode.get("city").get("name").textValue(),
                locationNode.get("city").get("country").get("name").textValue()
            )).collect(Collectors.toList());
    }
}

