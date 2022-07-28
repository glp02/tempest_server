package com.sky.tempest_server.flights;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.sky.tempest_server.flights.entities.Airport;

import com.sky.tempest_server.flights.entities.Flight;
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
public class FlightsService {
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectReader readJsonArrayToJsonNodeList = mapper.readerFor(new TypeReference<List<JsonNode>>() {});

    @Value("${tequila.apikey}")
    String tequilaApiKey;

    @Autowired
    public FlightsService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public List<Airport> searchAirports(String searchText) throws IOException {

        //SET API KEY IN HEADER
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
//        headers.set("Accept-Encoding", "gzip");
        headers.set("apikey",tequilaApiKey);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        //BUILD URL WITH QUERY PARAMETERS
        String url = "https://tequila-api.kiwi.com/locations/query";
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("term", searchText)
                .queryParam("location_types", "airport")
                .encode()
                .toUriString();

        //GENERATE MAP OF PARAMETERS
        Map<String, String> params = new HashMap<>();
        params.put("term", searchText);
        params.put("location_types", "airport");

        //GET JSON RESPONSE AS STRING
        HttpEntity<String> response = restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                entity,
                String.class,
                params);

        //MANIPULATE JSON RESPONSE
        JsonNode responseJSON = mapper.readValue(response.getBody(), JsonNode.class);
        JsonNode locationsJSON = responseJSON.get("locations");
        List<JsonNode> locationsList = readJsonArrayToJsonNodeList.readValue(locationsJSON);

        return locationsList.stream().map((locationNode) -> new Airport(
                locationNode.get("name").textValue(),
                locationNode.get("code").textValue(),
                locationNode.get("city").get("name").textValue(),
                locationNode.get("city").get("country").get("name").textValue()
            )).collect(Collectors.toList());
    }
    public List<Flight> searchFlights(String flightDate, String departureAirportCode, String arrivalAirportCode) throws IOException {

        //SET API KEY IN HEADER
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
//        headers.set("Accept-Encoding", "gzip");
        headers.set("apikey",tequilaApiKey);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        //BUILD URL WITH QUERY PARAMETERS
        String url = "https://tequila-api.kiwi.com/v2/search";
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("date_from", flightDate)
                .queryParam("date_to", flightDate)
                .queryParam("fly_from", departureAirportCode)
                .queryParam("fly_to", arrivalAirportCode)
                .queryParam("max_stopovers", 0)
                .encode()
                .toUriString();

        //GENERATE MAP OF PARAMETERS
        Map<String, Object> params = new HashMap<>();
        params.put("date_from", flightDate);
        params.put("date_to", flightDate);
        params.put("fly_from", departureAirportCode);
        params.put("fly_to", arrivalAirportCode);
        params.put("max_stopovers", 0);

        //GET JSON RESPONSE AS STRING
        HttpEntity<String> response = restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                entity,
                String.class,
                params);

        //MANIPULATE JSON RESPONSE
        JsonNode responseJSON = mapper.readValue(response.getBody(), JsonNode.class);
        JsonNode dataJSON = responseJSON.get("data");
        List<JsonNode> dataList = readJsonArrayToJsonNodeList.readValue(dataJSON);

        return dataList.stream().map((flightNode) -> new Flight(
                flightNode.get("id").textValue(),
                flightNode.get("route").get(0).get("flight_no").intValue(),
                flightNode.get("route").get(0).get("local_departure").textValue(),
                flightNode.get("route").get(0).get("local_arrival").textValue(),
                flightNode.get("duration").get("total").intValue(),
                flightNode.get("flyFrom").textValue(),
                flightNode.get("flyTo").textValue(),
                flightNode.get("cityFrom").textValue(),
                flightNode.get("cityTo").textValue(),
                flightNode.get("route").get(0).get("airline").textValue()
        )).collect(Collectors.toList());

    }
}

