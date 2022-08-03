package com.sky.tempest_server.flights.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "flights")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flight {

    //maybe use flight api unique id?
    @Id
    @NotNull
    @Column(unique = true,nullable = false,name = "flight_id")
    //@val not sure what this is?
    private String id;

    @NotNull
    @Column(nullable = false,name = "flight_number")
    private int flightNumber;

    //maybe will have to store as string?
    @NotNull
    @Column(nullable = false,name = "departure_date")
    private String departureDate;

    @NotNull
    @Column(nullable = false,name = "arrival_date")
    private String arrivalDate;

    @NotNull
    @Column(nullable = false,name = "duration")
    private int duration;

    @NotNull
    @Column(nullable = false,name = "departure_airport_code")
    private String departureAirportCode;

    @NotNull
    @Column(nullable = false,name = "arrival_airport_code")
    private String arrivalAirportCode;

    @NotNull
    @Column(nullable = false,name = "departure_city")
    private String departureCity;

    @NotNull
    @Column(nullable = false,name = "arrival_city")
    private String arrivalCity;

    @NotNull
    @Column(nullable = false,name = "airline_code")
    private String airlineCode;

}
