package com.sky.tempest_server.co2offset;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.tempest_server.co2offset.entities.CO2Offset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import java.io.IOException;

@Service
public class CO2OffsetService {
    private final ObjectMapper mapper = new ObjectMapper();
    static final String GO_CLIMATE_ENDPOINT = "https://api.goclimate.com/v1/flight_footprint";
    @Autowired
    private final GoClimateAPIService goClimateAPIService;

    @Autowired
    public CO2OffsetService(GoClimateAPIService goClimateAPIService) {
        this.goClimateAPIService = goClimateAPIService;
    }

    public CO2Offset getCO2OffsetByAirportCodes(String airportCodeFrom, String airportCodeTo, String cabinClass) throws IOException {
        //BUILD URL WITH QUERY PARAMETERS
        String queryUrlParams = UriComponentsBuilder.fromPath("")
                .queryParam("segments[0][origin]", airportCodeFrom)
                .queryParam("segments[0][destination]", airportCodeTo)
                .queryParam("cabin_class", cabinClass)
                .queryParam("currencies[]", "EUR")
                .build().toUri().toString();

        HttpEntity<String> goClimateResponse = goClimateAPIService.getRequestResponse(GO_CLIMATE_ENDPOINT, queryUrlParams);

        //MANIPULATE JSON RESPONSE
        JsonNode responseJSON = mapper.readValue(goClimateResponse.getBody(), JsonNode.class);
        return new CO2Offset(responseJSON.get("footprint").intValue(),
                responseJSON.get("offset_prices").get(0).get("amount").intValue(),
                responseJSON.get("offset_prices").get(0).get("offset_url").textValue());
    }
}