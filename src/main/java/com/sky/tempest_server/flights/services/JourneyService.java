package com.sky.tempest_server.flights.services;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.tempest_server.flights.entities.Airport;
import com.sky.tempest_server.flights.entities.Flight;
import com.sky.tempest_server.flights.entities.Journey;
import com.sky.tempest_server.flights.entities.JourneyDTO;
import com.sky.tempest_server.flights.exceptions.JourneyNotFoundException;
import com.sky.tempest_server.flights.exceptions.MyJsonProcessingException;
import com.sky.tempest_server.flights.repositories.AirportRepository;
import com.sky.tempest_server.flights.repositories.FlightsRepository;
import com.sky.tempest_server.flights.repositories.JourneyRepository;
import com.sky.tempest_server.user.entities.User;
import com.sky.tempest_server.user.filters.LoginFilter;
import com.sky.tempest_server.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class JourneyService {
    @Autowired
    private JourneyRepository journeyRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private FlightsService flightsService;

    @Autowired
    private FlightsRepository flightsRepository;

    @Autowired
    AirportRepository airportRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    public List<JourneyDTO> getSavedJourneys(String token) {
        User user = userService.findUserByToken(token);

        Iterator<Journey> journeys = journeyRepository.findJourneysByUser(user).iterator();
        List<JourneyDTO> journeyDTOS = new ArrayList<>();
        while (journeys.hasNext()){
            journeyDTOS.add(journeys.next().dto());
        }
        return journeyDTOS;
    }

    public void saveJourney(String token, String body){
        User user = userService.findUserByToken(token);
        String journeyName;
        Flight outboundFlight;
        Flight returnFlight = null;
        try {
            JsonNode responseJSON = mapper.readValue(body, JsonNode.class);
            journeyName = responseJSON.get("journeyName").textValue();
            outboundFlight = flightFromJson(responseJSON.get("outboundFlight"));
            if(responseJSON.has("returnFlight")){
                returnFlight = flightFromJson(responseJSON.get("returnFlight"));
            }
        } catch (Throwable e) {
            throw new MyJsonProcessingException();
        }




        if(!flightsRepository.existsById(outboundFlight.getId())){
            saveAirportsFromFlight(outboundFlight);
            flightsRepository.save(outboundFlight);
        }
        if(returnFlight!=null && !flightsRepository.existsById(returnFlight.getId())){
            saveAirportsFromFlight(returnFlight);
            flightsRepository.save(returnFlight);
        }
        Journey journey = new Journey(user,journeyName, outboundFlight, returnFlight);
        journeyRepository.save(journey);
    }

    public JourneyDTO deleteJourney(String token, String body){
        User user = userService.findUserByToken(token);
        Long journeyId = null;
        try {
            JsonNode responseJSON = mapper.readValue(body, JsonNode.class);
            if(responseJSON.has("journeyId")){
                journeyId = (long) responseJSON.get("journeyId").intValue();
            }
        } catch (Throwable e) {
            throw new MyJsonProcessingException();
        }
        for (Journey currentJourney : journeyRepository.findJourneysByUser(user)) {
            if (currentJourney.getId().equals(journeyId)){
                journeyRepository.delete(currentJourney);
                return currentJourney.dto();
            }
        }
        throw new JourneyNotFoundException(journeyId);
    }

    private Flight flightFromJson(JsonNode flightJsonNode) {
        try {
            String flightId = flightJsonNode.get("id").textValue();
            int flightNumber = flightJsonNode.get("flightNumber").intValue();
            String departureDate = flightJsonNode.get("departureDate").textValue();
            String arrivalDate = flightJsonNode.get("departureDate").textValue();
            int duration = flightJsonNode.get("duration").intValue();
            Airport departureAirport = getAirportFromJson(flightJsonNode.get("departureAirport"));
            Airport arrivalAirport = getAirportFromJson(flightJsonNode.get("arrivalAirport"));
            String airlineCode = flightJsonNode.get("airlineCode").textValue();

            return new Flight(flightId,flightNumber,departureDate,arrivalDate,duration,departureAirport,arrivalAirport,airlineCode);

        } catch (Exception e) {
            throw new MyJsonProcessingException();
        }
    }

    private Airport getAirportFromJson(JsonNode airportJsonNode){
        return new Airport(
                airportJsonNode.get("name").textValue(),
                airportJsonNode.get("country").textValue(),
                airportJsonNode.get("city").textValue(),
                airportJsonNode.get("iataCode").textValue(),
                airportJsonNode.get("latitude").doubleValue(),
                airportJsonNode.get("longitude").doubleValue()
        );
    }

    private void saveAirportsFromFlight(Flight flight){
        Airport arrivalAirport = flight.getArrivalAirport();
        Airport departureAirport = flight.getDepartureAirport();
        if(!airportRepository.existsById(arrivalAirport.getIataCode())){
            airportRepository.save(arrivalAirport);
        }
        if(!airportRepository.existsById(departureAirport.getIataCode())){
            airportRepository.save(departureAirport);
        }
    }
}