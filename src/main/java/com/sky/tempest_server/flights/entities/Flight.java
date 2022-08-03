package com.sky.tempest_server.flights.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "flight")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flight implements Serializable {

    //maybe use flight api unique id?
    @Id
    @NotNull
    @Column(unique = true,nullable = false,name = "flight_id", updatable = false)
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

    @ManyToOne
    @JoinColumn(nullable = false, name="departure_airport",referencedColumnName = "iata_code" )
    private Airport departureAirport;

    @ManyToOne
    @JoinColumn(nullable = false, name="arrival_airport",referencedColumnName = "iata_code" )
    private Airport arrivalAirport;

    @NotNull
    @Column(nullable = false,name = "airline_code")
    private String airlineCode;

}
