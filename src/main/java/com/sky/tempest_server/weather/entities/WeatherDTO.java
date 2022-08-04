package com.sky.tempest_server.weather.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherDTO {

    String dayOfYearIso;
    Temperature temperatureValues;
    WeatherSymbol weatherSymbol;
}
