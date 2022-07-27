package com.sky.tempest_server.flights;

import com.sky.tempest_server.flights.entities.Airport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class FlightsController {
    private FlightsService service;

    @Autowired
    public FlightsController(FlightsService service) {
        this.service = service;
    }

    @GetMapping("/flights/search-airports")
    public List<Airport> searchAirport(@RequestParam(name="searchText") String searchText) throws IOException {
        return this.service.searchAirports(searchText);
    }






}
