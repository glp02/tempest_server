package com.sky.tempest_server.weather.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherSymbol {
    int weatherSymbolCode;
    String weatherSymbolDescription;

}
