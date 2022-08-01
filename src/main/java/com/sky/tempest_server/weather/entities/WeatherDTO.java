package com.sky.tempest_server.weather.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherDTO {
    public enum ProbOfPrecipitation {
        LOW,
        MEDIUM,
        HIGH
    };

    String dayOfYearIso;
    Temperature temperatureValues;
    ProbOfPrecipitation probOfPrecipitation;
}
