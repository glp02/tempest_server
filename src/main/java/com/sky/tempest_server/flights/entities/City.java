package com.sky.tempest_server.flights.entities;

import lombok.*;

@Getter @Setter
public class City extends Location {
    private String cityCode;

    public City() {
        super();
    }

    public City(String name, String country, String cityCode) {
        super(name, country, LocationType.CITY);
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
