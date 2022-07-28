package com.sky.tempest_server.weather;

import com.sky.tempest_server.flights.FlightsService;
import com.sky.tempest_server.flights.WeatherService;
import com.sky.tempest_server.flights.entities.Airport;
import com.sky.tempest_server.flights.entities.Flight;
import com.sky.tempest_server.weather.entities.WeatherDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class WeatherController {
    private WeatherService service;

    @Autowired
    public WeatherController(WeatherService service) {
        this.service = service;
    }

    @GetMapping("/weather")
    public WeatherDTO getWeatherByLatLong(@RequestParam(name="latitude") double latitude,
                                          @RequestParam(name="longitude") double longitude,
                                          @RequestParam(name="datetime") String datetime) {
        return this.service.getWeatherByLatLong(latitude, longitude, datetime);
    }

}






