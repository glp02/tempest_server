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

    public Airport() {
        super();
    }

    public Airport(String name, String country,String city, String iataCode, double latitude, double longitude) {
        super(name, country, LocationType.AIRPORT);
        this.city = city;
        this.iataCode =iataCode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
