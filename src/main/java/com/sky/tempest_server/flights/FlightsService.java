package com.sky.tempest_server.flights;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.sky.tempest_server.flights.entities.Airport;

import com.sky.tempest_server.flights.entities.Flight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlightsService {
    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectReader readJsonArrayToJsonNodeList = mapper.readerFor(new TypeReference<List<JsonNode>>() {});

    static final String TEQUILA_LOCATIONS_ENDPOINT = "https://tequila-api.kiwi.com/locations/query";
    static final String TEQUILA_FLIGHTS_ENDPOINT = "https://tequila-api.kiwi.com/v2/search";

    @Autowired
    private final TequilaAPIService tequilaAPIService;

    public FlightsService(TequilaAPIService tequilaAPIService) {
        this.tequilaAPIService = tequilaAPIService;
    }

    public List<Airport> searchAirports(String searchText) throws IOException {
        //BUILD URL WITH QUERY PARAMETERS
        String queryUrlParams = UriComponentsBuilder.fromPath("")
                .queryParam("term", searchText)
                .queryParam("location_types", "airport")
                .encode()
                .toUriString();

        HttpEntity<String> tequilaResponse = tequilaAPIService.getRequestResponse(TEQUILA_LOCATIONS_ENDPOINT, queryUrlParams);

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


    public List<Flight> searchFlights(String flightDate, String departureAirportCode, String arrivalAirportCode) throws IOException {
        //BUILD URL WITH QUERY PARAMETERS
        String queryUrlParams = UriComponentsBuilder.fromPath("")
                .queryParam("date_from", flightDate)
                .queryParam("date_to", flightDate)
                .queryParam("fly_from", departureAirportCode)
                .queryParam("fly_to", arrivalAirportCode)
                .queryParam("max_stopovers", 0)
                .encode()
                .toUriString();

        HttpEntity<String> tequilaResponse = tequilaAPIService.getRequestResponse(TEQUILA_FLIGHTS_ENDPOINT, queryUrlParams);

        //MANIPULATE JSON RESPONSE
        JsonNode responseJSON = mapper.readValue(tequilaResponse.getBody(), JsonNode.class);
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

