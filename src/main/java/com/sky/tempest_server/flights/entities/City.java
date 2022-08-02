package com.sky.tempest_server.flights.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
public class City extends Location {
    private String cityCode;

    public City() {
        super();
    }

    public City(String name, String country, String cityCode) {
        super(name, country, LocationType.CITY);
        this.cityCode = cityCode;
    }

}
