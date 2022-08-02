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
        Map<DateTime, List<Double>> daysMapListWeatherSymbol = new LinkedHashMap<DateTime, List<Double>>();


        List<Temperature> temperatureList = new ArrayList();
        List<WeatherSymbol> weatherSymbolList = new ArrayList();
        List<String> dateList = new ArrayList();
//        List<WeatherSymbol> returnedWeatherSymbolList = new ArrayList();


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
                .pathSegment("t_2m:C,weather_symbol_1h:idx")
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
        JsonNode weatherSymbolJSON = weatherJSON.get(1).get("coordinates").get(0).get("dates");

        populateMapLists(temperatureJSON, daysMapListTemp);
        populateMapLists(weatherSymbolJSON, daysMapListWeatherSymbol);

        // iterates through linked hash map and creates temperature object with the respective day's max, min, average
        for (Map.Entry<DateTime, List<Double>> mapEntry : daysMapListTemp.entrySet()) {
            DateTime specificDay = mapEntry.getKey();
            String dayOfYearISO = specificDay.toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")); // sets to string ISO 8601 format
            List<Double> tempValues = mapEntry.getValue();
            dateList.add(dayOfYearISO);
            temperatureList.add(new Temperature(Collections.min(tempValues),Collections.max(tempValues),getAverage(tempValues)));
        }
        // iterates through linked hash map and creates temperature object with the respective day's max, min, average
        for (Map.Entry<DateTime, List<Double>> mapEntry : daysMapListWeatherSymbol.entrySet()) {
            DateTime specificDay = mapEntry.getKey();
            String dayOfYearISO = specificDay.toString(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")); // sets to string ISO 8601 format
            List<Double> weatherSymbolValues = mapEntry.getValue();
            weatherSymbolList.add(getMode(weatherSymbolValues));
        };
        List<WeatherDTO> weatherDTOList = new ArrayList<WeatherDTO>();
        for(int i = 0; i < temperatureList.size(); i++) {
            weatherDTOList.add(new WeatherDTO(dateList.get(i),temperatureList.get(i),weatherSymbolList.get(i)));
        }
        return weatherDTOList;
    }

    private final WeatherSymbol getMode(List<Double> weatherSymbolValues) {
        List<Double> dayWeatherSymbolValues = new ArrayList<>();
        Map<Integer, String> weatherSymbolDictionary = new HashMap<>();
        weatherSymbolDictionary.put(1,"Clear sky");
        weatherSymbolDictionary.put(2,"Light clouds");
        weatherSymbolDictionary.put(3,"Partly cloudy");
        weatherSymbolDictionary.put(4,"Cloudy");
        weatherSymbolDictionary.put(5,"Rain");
        weatherSymbolDictionary.put(6,"Rain and snow");
        weatherSymbolDictionary.put(7,"Snow");
        weatherSymbolDictionary.put(8,"Rain shower");
        weatherSymbolDictionary.put(9,"Snow shower");
        weatherSymbolDictionary.put(10,"Sleet shower");
        weatherSymbolDictionary.put(11,"Light fog");
        weatherSymbolDictionary.put(12,"Dense fog");
        weatherSymbolDictionary.put(13,"Freezing rain");
        weatherSymbolDictionary.put(14,"Thunderstorms");
        weatherSymbolDictionary.put(15,"Drizzle");
        weatherSymbolDictionary.put(16,"Sandstorm");

        for (double weatherSymbol: weatherSymbolValues) {
            if (Double.compare(weatherSymbol, 100d)<0) {
                dayWeatherSymbolValues.add(weatherSymbol);
            }
        }
            int maxValue = 0, maxCount = 0;

            for (int i = 0; i < dayWeatherSymbolValues.size(); ++i)
            {
                int count = 0;
                for (int j = 0; j < dayWeatherSymbolValues.size(); ++j)
                {
                    if (dayWeatherSymbolValues.get(j) == dayWeatherSymbolValues.get(i))
                        ++count;
                }
                if (count > maxCount)
                {
                    maxCount = count;
                    maxValue = dayWeatherSymbolValues.get(i).intValue();
                }
            }
            return new WeatherSymbol(maxValue, weatherSymbolDictionary.get(maxValue));
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

