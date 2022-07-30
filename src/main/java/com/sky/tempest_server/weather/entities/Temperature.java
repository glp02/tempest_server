package com.sky.tempest_server.weather.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Temperature {
    String dayOfYearISO;
    double minTemperature;
    double maxTemperature;
    double avgTemperature;
}
