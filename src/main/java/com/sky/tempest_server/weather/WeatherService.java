package com.sky.tempest_server.weather;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.sky.tempest_server.weather.entities.DateValue;
import com.sky.tempest_server.weather.entities.Temperature;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import org.joda.time.DateTime;

@Service
public class WeatherService {
    private final ObjectMapper mapper = new ObjectMapper();

    private final ObjectReader readJsonArrayToJsonNodeList = mapper.readerFor(new TypeReference<List<JsonNode>>() {});

    static final String METEOMATICS_ENDPOINT = "https://api.meteomatics.com";
    DecimalFormat oneDecimalPlace = new DecimalFormat("#.#");
    Map<DateTime, List<Double>> daysMapList = new LinkedHashMap<DateTime, List<Double>>();
    List<Temperature> tempList = new ArrayList();
    @Autowired
    private final MeteomaticsAPIService meteomaticsAPIService;

    public WeatherService(MeteomaticsAPIService meteomaticsAPIService) {
        this.meteomaticsAPIService = meteomaticsAPIService;
    }

    public double getAverage(List<Double> tempValues) {
        double sum = 0;
        double average = 0;
        int lengthOfList = tempValues.size();
        for (Double tempValue : tempValues) {
            sum += tempValue;
        }
        average = sum / (double) lengthOfList;
        return Double.parseDouble(oneDecimalPlace.format(average));
    }

    public List<Temperature>  getWeatherByLatLong(double latitude, double longitude, String dateTimeFrom, String dateTimeTo) throws IOException {
        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("dateTimeTo", dateTimeTo);
        uriVariables.put("dateTimeFrom", dateTimeFrom);
        uriVariables.put("latitude", latitude);
        uriVariables.put("longitude", longitude);

        // BUILD URL + ENCODE
        String queryUrlParams = UriComponentsBuilder.fromPath("")
                .pathSegment("{dateTimeFrom}--{dateTimeTo}:PT1H")
                .pathSegment("t_2m:C,wind_speed_10m:ms,weather_symbol_1h:idx")
                .pathSegment("{latitude},{longitude}")
                .pathSegment("json")
                .queryParam("model",  "mix")
                .buildAndExpand(uriVariables)
                .encode()
                .toUriString();

        HttpEntity<String> meteomaticsResponse = meteomaticsAPIService.getRequestResponse(METEOMATICS_ENDPOINT, queryUrlParams);


        //MANIPULATE JSON RESPONSE
        JsonNode responseJSON = mapper.readValue(meteomaticsResponse.getBody(), JsonNode.class);
        JsonNode weatherJSON = responseJSON.get("data");
        JsonNode temperatureJSON = weatherJSON.get(0).get("coordinates").get(0).get("dates");

        List<JsonNode> temperatureList = readJsonArrayToJsonNodeList.readValue(temperatureJSON);
        List<DateValue> dateValueList = temperatureList.stream().map((dateValueNode) -> new DateValue(
                dateValueNode.get("date").textValue(),
                dateValueNode.get("value").doubleValue()
        )).collect(Collectors.toList());

        // populate daysMapList with { datetime of that day, [list of temperatures for that day] }
        for (DateValue dateValue : dateValueList) {
            DateTime currDate = new DateTime(dateValue.getDate()).withTimeAtStartOfDay();
            daysMapList.putIfAbsent(currDate, new ArrayList<Double>());
            daysMapList.get(currDate).add(dateValue.getValue());
        }

        // iterates through linked hash map and creates temperature object with the respective day's max, min, average
        for (Map.Entry<DateTime, List<Double>> mapEntry : daysMapList.entrySet()) {
            DateTime key = mapEntry.getKey();
            String dayOfYearISO = key.toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")); // sets to string ISO 8601 format
            List<Double> tempValues = mapEntry.getValue();
            tempList.add(new Temperature(dayOfYearISO,Collections.min(tempValues),Collections.max(tempValues),getAverage(tempValues)));
        }
        System.out.println(tempList);
        return tempList;
    }
}

