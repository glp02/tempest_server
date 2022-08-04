package com.sky.tempest_server.flights.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JourneyDTO {
    private Long journeyID;
    private String userEmail;
    private String journeyName;
    private Flight outboundFlight;
    private Flight returnFlight;
}

