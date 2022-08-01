package com.sky.tempest_server.weather;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.sky.tempest_server.weather.entities.*;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
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

    public List<WeatherDTO>  getWeatherByLatLong(double latitude, double longitude, String dateTimeFrom, String dateTimeTo) throws IOException {

        Map<DateTime, List<Double>> daysMapListTemp = new LinkedHashMap<DateTime, List<Double>>();
        Map<DateTime, List<Double>> daysMapListPrecipitation = new LinkedHashMap<DateTime, List<Double>>();

        List<Temperature> temperatureList = new ArrayList();
        List<WeatherDTO.ProbOfPrecipitation> precipitationProbList = new ArrayList();
        List<String> dateList = new ArrayList();


        // Check that inputted dateFrom and dateTo falls within the 14 day forecasting period,
        // if dateTo is not, then change dateTo parameter to range limit
        // if dateFrom is not, then change
        DateTime rangeLimit = new DateTime().plus(Period.days(14));
        DateTime dateTimeFromDT = new DateTime(dateTimeFrom);
        DateTime dateTimeToDT = new DateTime(dateTimeTo);
        if(dateTimeFromDT.isAfter(rangeLimit)) {
            return new ArrayList<>();
        } else if (dateTimeToDT.isAfter(rangeLimit)) {
            dateTimeTo = rangeLimit.toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        }

        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("dateTimeTo", dateTimeTo);
        uriVariables.put("dateTimeFrom", dateTimeFrom);
        uriVariables.put("latitude", latitude);
        uriVariables.put("longitude", longitude);

        // BUILD URL + ENCODE
        String queryUrlParams = UriComponentsBuilder.fromPath("")
                .pathSegment("{dateTimeFrom}--{dateTimeTo}:PT1H")
                .pathSegment("t_2m:C,prob_precip_1h:p")
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
        JsonNode precipitationJSON = weatherJSON.get(1).get("coordinates").get(0).get("dates");

        populateMapLists(temperatureJSON, daysMapListTemp);
        populateMapLists(precipitationJSON, daysMapListPrecipitation);

        // iterates through linked hash map and creates temperature object with the respective day's max, min, average
        for (Map.Entry<DateTime, List<Double>> mapEntry : daysMapListTemp.entrySet()) {
            DateTime specificDay = mapEntry.getKey();
            String dayOfYearISO = specificDay.toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")); // sets to string ISO 8601 format
            List<Double> tempValues = mapEntry.getValue();
            dateList.add(dayOfYearISO);
            temperatureList.add(new Temperature(Collections.min(tempValues),Collections.max(tempValues),getAverage(tempValues)));
        }
        // iterates through linked hash map and creates temperature object with the respective day's max, min, average
        for (Map.Entry<DateTime, List<Double>> mapEntry : daysMapListPrecipitation.entrySet()) {
            DateTime specificDay = mapEntry.getKey();
            String dayOfYearISO = specificDay.toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")); // sets to string ISO 8601 format
            List<Double> precipitationValues = mapEntry.getValue();
            WeatherDTO.ProbOfPrecipitation lowMediumHigh;
            double precipitationAveragePercent = getAverage(precipitationValues);
            if(precipitationAveragePercent >= 50) {
                lowMediumHigh = WeatherDTO.ProbOfPrecipitation.HIGH;
            } else if(precipitationAveragePercent >= 30) {
                lowMediumHigh = WeatherDTO.ProbOfPrecipitation.MEDIUM;
            } else {
                lowMediumHigh = WeatherDTO.ProbOfPrecipitation.LOW;
            }
            precipitationProbList.add(lowMediumHigh);
        }
        List<WeatherDTO> weatherDTOList = new ArrayList<WeatherDTO>();
        for(int i = 0; i < temperatureList.size(); i++) {
            weatherDTOList.add(new WeatherDTO(dateList.get(i),temperatureList.get(i),precipitationProbList.get(i)));
        }
        return weatherDTOList;
    }

    private void populateMapLists(JsonNode jsonNode, Map<DateTime, List<Double>> daysMapList) throws IOException {
        List<JsonNode> jsonNodeList = readJsonArrayToJsonNodeList.readValue(jsonNode);
        List<DateValue> dateValueList = jsonNodeList.stream().map((dateValueNode) -> new DateValue(
                dateValueNode.get("date").textValue(),
                dateValueNode.get("value").doubleValue()
        )).collect(Collectors.toList());

        // populate daysMapList with { datetime of that day, [list of temperatures/precipitation for that day] }
        for (DateValue dateValue : dateValueList) {
            DateTime currDate = new DateTime(dateValue.getDate()).withTimeAtStartOfDay();
            daysMapList.putIfAbsent(currDate, new ArrayList<Double>());
            daysMapList.get(currDate).add(dateValue.getValue());
        }
    }
}

