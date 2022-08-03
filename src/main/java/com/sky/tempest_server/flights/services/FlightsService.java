package com.sky.tempest_server.flights.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.sky.tempest_server.flights.entities.Airport;

import com.sky.tempest_server.flights.entities.City;
import com.sky.tempest_server.flights.entities.Flight;
import com.sky.tempest_server.flights.entities.Location;
import com.sky.tempest_server.flights.exceptions.InvalidLocationTypeException;
import com.sky.tempest_server.flights.repositories.AirportRepository;
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

    @Autowired
    private AirportRepository airportRepository;

    public FlightsService(TequilaAPIService tequilaAPIService) {
        this.tequilaAPIService = tequilaAPIService;
    }

    public List<Location> searchLocations(String locationType, String searchText) throws IOException {
        //BUILD URL WITH QUERY PARAMETERS
        String queryUrlParams = UriComponentsBuilder.fromPath("")
                .queryParam("term", searchText)
                .queryParam("location_types", locationType)
                .encode()
                .toUriString();

        HttpEntity<String> tequilaResponse = tequilaAPIService.getRequestResponse(TEQUILA_LOCATIONS_ENDPOINT, queryUrlParams);

        //MANIPULATE JSON RESPONSE
        JsonNode responseJSON = mapper.readValue(tequilaResponse.getBody(), JsonNode.class);
        JsonNode locationsJSON = responseJSON.get("locations");
        List<JsonNode> locationsList = readJsonArrayToJsonNodeList.readValue(locationsJSON);

        if(locationType.equals("city")) {
            return locationsList.stream().map((locationNode) -> new City(
                    locationNode.get("name").textValue(),
                    locationNode.get("country").get("name").textValue(),
                    locationNode.get("code").textValue()
            )).filter(city -> (city.getCityCode() != null)).collect(Collectors.toList());
        }
        if(locationType.equals("airport")) {
            return locationsList.stream().map((locationNode) -> new Airport(
                    locationNode.get("name").textValue(),
                    locationNode.get("city").get("country").get("name").textValue(),
                    locationNode.get("city").get("name").textValue(),
                    locationNode.get("code").textValue(),
                    locationNode.get("location").get("lat").doubleValue(),
                    locationNode.get("location").get("lon").doubleValue()
            )).collect(Collectors.toList());
        }
        else throw new InvalidLocationTypeException();
    }

    public List<Flight> searchFlights(String flightDate,
                                      String departureLocationType,
                                      String departureAirportCode,
                                      String arrivalLocationType,
                                      String arrivalAirportCode) throws Exception {

        //CHECK LOCATION TYPES ARE VALID
        boolean departureLocationTypeInvalid = !departureLocationType.equals("city") && !departureLocationType.equals("airport");
        boolean arrivalLocationTypeInvalid = !arrivalLocationType.equals("city") && !arrivalLocationType.equals("airport");
        if (departureLocationTypeInvalid || arrivalLocationTypeInvalid) {
            throw new InvalidLocationTypeException();
        }


        //BUILD URL WITH QUERY PARAMETERS
        String queryUrlParams = UriComponentsBuilder.fromPath("")
                .queryParam("date_from", flightDate)
                .queryParam("date_to", flightDate)
                .queryParam("fly_from", departureLocationType+":"+departureAirportCode)
                .queryParam("fly_to", arrivalLocationType+":"+arrivalAirportCode)
                .queryParam("max_stopovers", 0)
                .encode()
                .toUriString();

        HttpEntity<String> tequilaResponse = tequilaAPIService.getRequestResponse(TEQUILA_FLIGHTS_ENDPOINT, queryUrlParams);

        //MANIPULATE JSON RESPONSE
        JsonNode responseJSON = mapper.readValue(tequilaResponse.getBody(), JsonNode.class);
        JsonNode dataJSON = responseJSON.get("data");
        List<JsonNode> dataList = readJsonArrayToJsonNodeList.readValue(dataJSON);


        return dataList.stream().map((flightNode) -> {
            try {
                return new Flight(
                        flightNode.get("id").textValue(),
                        flightNode.get("route").get(0).get("flight_no").intValue(),
                        flightNode.get("route").get(0).get("local_departure").textValue(),
                        flightNode.get("route").get(0).get("local_arrival").textValue(),
                        flightNode.get("duration").get("total").intValue(),
                        findAirport(flightNode.get("flyFrom").textValue()),
                        findAirport(flightNode.get("flyTo").textValue()),
                        flightNode.get("route").get(0).get("airline").textValue()
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

    }

    private Airport findAirport(String iataCode) throws IOException {
        Airport airport;
        if(airportRepository.existsById(iataCode)){
            airport = airportRepository.findById(iataCode).get();
        } else{
            airport = (Airport) searchLocations("airport",iataCode).get(0);
            airportRepository.save(airport);
        }

        return airport;
    }




}

