package com.sky.tempest_server.co2offset;
import com.sky.tempest_server.co2offset.entities.CO2Offset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class CO2OffsetController {
    private CO2OffsetService service;

    @Autowired
    public CO2OffsetController(CO2OffsetService service) {
        this.service = service;
    }

    @GetMapping("/co2-offset")
    public CO2Offset getCO2OffsetByAirportCodes(@RequestParam(name="airportCodeFrom") String airportCodeFrom,
                                               @RequestParam(name="airportCodeTo") String airportCodeTo,
                                               @RequestParam(name="cabinClass") String cabinClass) throws IOException {
        return this.service.getCO2OffsetByAirportCodes(airportCodeFrom, airportCodeTo, cabinClass);
    }
}
