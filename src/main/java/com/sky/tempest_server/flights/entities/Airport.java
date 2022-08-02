package com.sky.tempest_server.flights.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Airport extends Location{

    private String iataCode;
    private String city;

    public Airport() {
        super();
    }

    public Airport(String name, String country,String city, String iataCode) {
        super(name, country, LocationType.AIRPORT);
        this.city = city;
        this.iataCode =iataCode;
    }

}
