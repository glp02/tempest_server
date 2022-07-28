package com.sky.tempest_server.co2offset.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CO2Offset {
    int co2Footprint;
    int co2OffsetAmount;
    String co2OffsetURL;
}
