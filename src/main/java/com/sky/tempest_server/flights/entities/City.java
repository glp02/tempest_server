package com.sky.tempest_server.flights.entities;

import lombok.*;


@Data
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

    @Override
    public boolean equals(Object o) {
        if(o instanceof City) {
            City city = ((City) o);
            return super.equals(city) && (this.getCityCode()).equals(city.getCityCode());
        } else return false;
    }

}
