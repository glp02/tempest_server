package com.sky.tempest_server.flights.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Entity
@Table(name="airport")
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Airport extends Location implements Serializable {

    @Id
    @NotNull
    @Column(nullable = false,unique = true,name = "iata_code",updatable = false)
    private String iataCode;

    @NotNull
    @Column(nullable = false,name = "city")
    private String city;

    @NotNull
    @Column(nullable = false, name = "latitude")
    private double latitude;

    @NotNull
    @Column(nullable = false, name = "longitude")
    private double longitude;

    @Column(nullable = false,name = "location_type")
    LocationType locationType;

    @Column(nullable = false, name = "airport_name")
    String name;

    @Column(nullable = false, name = "country")
    String country;

    public Airport() {
        super();
    }

    public Airport(String name, String country,String city, String iataCode, double latitude, double longitude) {
        this.name = name;
        this.country = country;
        this.city = city;
        this.iataCode =iataCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationType = LocationType.AIRPORT;
    }

}
