package com.sky.tempest_server.flights.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class City extends Location {
    private String cityCode;

    private String name;

    private String country;

    private LocationType locationType;

    public City(String name, String country, String cityCode) {
        this.name=name;
        this.country=country;
        this.locationType = LocationType.CITY;
        this.cityCode = cityCode;
    }

}
