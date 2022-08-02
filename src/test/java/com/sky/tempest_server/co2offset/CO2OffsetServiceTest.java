package com.sky.tempest_server.co2offset;

import com.sky.tempest_server.co2offset.entities.CO2Offset;

import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class CO2OffsetServiceTest {
    static final String GO_CLIMATE_ENDPOINT = "https://api.goclimate.com/v1/flight_footprint";

    private final String testCO2OffsetByAirportCodeJsonResponse = "{\n" +
            "    \"footprint\": 500,\n" +
            "    \"offset_prices\": [\n" +
            "        {\n" +
            "            \"amount\": 750,\n" +
            "            \"currency\": \"EUR\",\n" +
            "            \"offset_url\": \"https://www.goclimate.com/flight_offsets/new?offset_params=economy%2CARN%2CBCN\",\n" +
            "            \"locale\": \"en\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"amount\": 750,\n" +
            "            \"currency\": \"EUR\",\n" +
            "            \"offset_url\": \"https://www.goclimate.com/de/flight_offsets/new?offset_params=economy%2CARN%2CBCN\",\n" +
            "            \"locale\": \"de-DE\"\n" +
            "        }\n" +
            "    ],\n" +
            "    \"details_url\": \"https://www.goclimate.com/se/flight_offsets/new?offset_params=economy%2CARN%2CBCN\"\n" +
            "}";

    @Mock
    private GoClimateAPIService mockGoClimateAPIService;

    @InjectMocks
    private CO2OffsetService co2OffsetService;

    @org.junit.Test
    public void test_getCO2OffsetByAirportCodes_correctlyManipulatesJSONResponse() throws IOException {
        GoClimateAPIService mockGoClimateAPIService = mock(GoClimateAPIService.class);
        CO2OffsetService co2OffsetService = new CO2OffsetService(mockGoClimateAPIService);

        String airportCodeFrom = "ARN";
        String airportCodeTo = "BCN";
        String cabinClass = "economy";
        String testQueryParams = "?segments[0][origin]=" + airportCodeFrom + "&segments[0][destination]=" + airportCodeTo + "&cabin_class=" + cabinClass + "&currencies[]=EUR";

        when(mockGoClimateAPIService.getRequestResponse(GO_CLIMATE_ENDPOINT, testQueryParams))
                .thenReturn(new HttpEntity<>(testCO2OffsetByAirportCodeJsonResponse));

        CO2Offset resultCO2Offset = co2OffsetService.getCO2OffsetByAirportCodes(airportCodeFrom, airportCodeTo, cabinClass);

        assertEquals(new CO2Offset(500,750,"https://www.goclimate.com/flight_offsets/new?offset_params=economy%2CARN%2CBCN"),resultCO2Offset);
    }
}
