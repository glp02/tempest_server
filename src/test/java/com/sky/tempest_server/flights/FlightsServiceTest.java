package com.sky.tempest_server.flights;

import com.sky.tempest_server.flights.entities.Airport;
import com.sky.tempest_server.flights.entities.City;
import com.sky.tempest_server.flights.entities.Flight;
import com.sky.tempest_server.flights.entities.Location;
import com.sky.tempest_server.flights.exceptions.InvalidLocationTypeException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class FlightsServiceTest {

    private final String testAirportSearchJsonResponse = "{\"locations\":[{" +
            "\"name\":\"testAirportName\"," +
            "\"code\":\"testAirportCode\"," +
            "\"city\":{" +
                "\"name\":\"testCityName\"," +
                "\"country\":{" +
                    "\"name\": \"testCountryName\"" +
                "}" +
            "}}]}";
    private final String testCitySearchJsonResponse = "{\"locations\":[{" +
            "\"name\":\"testCityName\"," +
            "\"code\":\"testCityCode\"," +
            "\"country\":{" +
                "\"name\":\"testCountryName\"" +
            "}}]}";
    private final String testFlightSearchJsonResponse = "{\"data\":[{" +
            "\"id\":\"testId\"," +
            "\"duration\":{" +
            "\"total\":1000" +
            "}," +
            "\"route\":[{" +
            "\"flight_no\":0," +
            "\"local_departure\":\"testDepartureDate\"," +
            "\"local_arrival\":\"testArrivalDate\"," +
            "\"airline\":\"testAirline\"" +
            "}]," +
            "\"flyFrom\":\"testDepartureAirportCode\"," +
            "\"flyTo\":\"testArrivalAirportCode\"," +
            "\"cityFrom\":\"testDepartureCity\"," +
            "\"cityTo\":\"testArrivalCity\"" +
            "}]}";


    @Mock
    private TequilaAPIService mockTequilaAPIService;

    @InjectMocks
    private FlightsService flightsService;

    @org.junit.Test
    public void tests_searchAirports_correctlyManipulatesJSONResponse() throws Exception {
        TequilaAPIService mockTequilaAPIService = mock(TequilaAPIService.class);
        String searchText = "text";
        FlightsService flightsService = new FlightsService(mockTequilaAPIService);
        when(mockTequilaAPIService.getRequestResponse("https://tequila-api.kiwi.com/locations/query","?term="+searchText+"&location_types=airport"))
                .thenReturn(new HttpEntity<>(testAirportSearchJsonResponse));

        List<Location> resultAirports = flightsService.searchLocations("airport", searchText);

        assertEquals(new Airport("testAirportName","testCountryName","testCityName","testAirportCode"),resultAirports.get(0));
    }

    @org.junit.Test
    public void tests_searchCities_correctlyManipulatesJSONResponse() throws Exception {
        TequilaAPIService mockTequilaAPIService = mock(TequilaAPIService.class);
        String searchText = "text";
        FlightsService flightsService = new FlightsService(mockTequilaAPIService);
        when(mockTequilaAPIService.getRequestResponse("https://tequila-api.kiwi.com/locations/query","?term="+searchText+"&location_types=city"))
                .thenReturn(new HttpEntity<>(testCitySearchJsonResponse));

        List<Location> resultCities = flightsService.searchLocations("city", searchText);

        assertEquals(new City("testCityName","testCountryName","testCityCode"),resultCities.get(0));
    }

    @org.junit.Test
    public void tests_searchFlights_correctlyManipulatesJSONResponse() throws Exception {

        TequilaAPIService mockTequilaAPIService = mock(TequilaAPIService.class);
        String locationType = "airport";
        String testDate = "testDate";
        String flyFrom = "testFrom";
        String flyTo = "testTo";
        int maxStopovers = 0;
        String testQueryParams = "?date_from="+testDate+"&date_to="+testDate+"&fly_from="+locationType+":"+flyFrom+"&fly_to="+locationType+":"+flyTo+"&max_stopovers="+maxStopovers;
        FlightsService flightsService = new FlightsService(mockTequilaAPIService);
        when(mockTequilaAPIService.getRequestResponse("https://tequila-api.kiwi.com/v2/search", testQueryParams))
                .thenReturn(new HttpEntity<>(testFlightSearchJsonResponse));

        List<Flight> resultFlights = flightsService.searchFlights(testDate, locationType, flyFrom, locationType, flyTo);

        assertEquals(new Flight("testId", 0, "testDepartureDate", "testArrivalDate", 1000, "testDepartureAirportCode", "testArrivalAirportCode", "testDepartureCity", "testArrivalCity", "testAirline" ),resultFlights.get(0));
    }

    @org.junit.Test
    public void tests_searchFlights_correctlyThrowsException_whenIncorrectLocationTypeIsProvided() throws Exception {

        TequilaAPIService mockTequilaAPIService = mock(TequilaAPIService.class);
        String invalidLocationType = "notCityOrAirport";
        String testDate = "testDate";
        String flyFrom = "testFrom";
        String flyTo = "testTo";
        int maxStopovers = 0;
        String testQueryParams = "?date_from="+testDate+"&date_to="+testDate+"&fly_from="+invalidLocationType+":"+flyFrom+"&fly_to="+invalidLocationType+":"+flyTo+"&max_stopovers="+maxStopovers;
        FlightsService flightsService = new FlightsService(mockTequilaAPIService);
        when(mockTequilaAPIService.getRequestResponse("https://tequila-api.kiwi.com/v2/search", testQueryParams))
                .thenReturn(new HttpEntity<>(testFlightSearchJsonResponse));

        assertThrows(InvalidLocationTypeException.class, ()->{
            flightsService.searchFlights(testDate, invalidLocationType, flyFrom, invalidLocationType, flyTo);
        });
    }









}
