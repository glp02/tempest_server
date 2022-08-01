package com.sky.tempest_server.flights;

import com.sky.tempest_server.flights.entities.Flight;
import com.sky.tempest_server.flights.entities.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class FlightsController {
    private final FlightsService service;

    @Autowired
    public FlightsController(FlightsService service) {
        this.service = service;
    }

    @GetMapping("/flights/search-cities")
    public List<Location> searchCities(@RequestParam(name="searchText") String searchText) throws Exception {
        return this.service.searchLocations("city", searchText);
    }

    @GetMapping("/flights/search-airports")
    public List<Location> searchAirports(@RequestParam(name="searchText") String searchText) throws Exception {
        return this.service.searchLocations("airport", searchText);
    }

    @GetMapping("/flights/search-locations")
    public List<Location> searchLocations(@RequestParam(name="searchText") String searchText) throws Exception {
        List<Location> cityList =  this.searchCities(searchText);
        List<Location> airportList =  this.searchAirports(searchText);
        return Stream.concat(cityList.stream(), airportList.stream()).collect(Collectors.toList());
    }

    @GetMapping("/flights/search-flights")
    public List<Flight> searchFlights(@RequestParam(name="flightDate") String flightDate,
                                               @RequestParam(name = "departureLocationType") String departureLocationType,
                                               @RequestParam(name = "arrivalLocationType") String arrivalLocationType,
                                               @RequestParam(name="departureAirportCode") String departureAirportCode,
                                               @RequestParam(name="arrivalAirportCode") String arrivalAirportCode) throws Exception {
        return this.service.searchFlights(flightDate, departureLocationType, departureAirportCode, arrivalLocationType, arrivalAirportCode);
    }

}
