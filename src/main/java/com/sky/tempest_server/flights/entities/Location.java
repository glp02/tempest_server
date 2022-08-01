package com.sky.tempest_server.flights.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Location {

    enum LocationType {
        CITY,
        AIRPORT
    }

    private String name;
    private String country;
    private LocationType locationType;
}
