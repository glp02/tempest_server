package com.sky.tempest_server.flights.entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
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

    @Override
    public boolean equals(Object o) {
        if(o instanceof Airport) {
            Airport airport = ((Airport) o);
            return super.equals(airport)
                    && (this.getIataCode()).equals(airport.getIataCode())
                    && (this.getCity()).equals(airport.getCity());
        } else return false;
    }

}
