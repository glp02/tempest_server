package com.sky.tempest_server.flights.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Airport {
    String fullName;
    String IATACode;
}
