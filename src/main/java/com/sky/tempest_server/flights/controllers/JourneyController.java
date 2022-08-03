package com.sky.tempest_server.flights.controllers;

import com.sky.tempest_server.flights.entities.JourneyDTO;
import com.sky.tempest_server.flights.services.JourneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class JourneyController {
    @Autowired
    private JourneyService service;

    @PostMapping("/journeys/savejourney")
    public void saveJourney(
            @RequestHeader("Authorization") String token,
            @RequestBody String body
    ){
        service.saveJourney(token,body);
    }

    @GetMapping("/journeys/getjourneys")
    public List<JourneyDTO> getJourneys(@RequestHeader("Authorization") String token){
        return service.getSavedJourneys(token);
    }

    @DeleteMapping("/journeys/deletejourney")
    public JourneyDTO deleteJourney(@RequestHeader("Authorization") String token, @RequestBody String body){
        return service.deleteJourney(token,body);
    }

}
