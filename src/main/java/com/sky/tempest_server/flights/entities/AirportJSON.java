package com.sky.tempest_server.flights.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class AirportJSON {

    @JsonProperty("name")
    private String name;

    @JsonProperty("code")
    private String iataCode;

}
