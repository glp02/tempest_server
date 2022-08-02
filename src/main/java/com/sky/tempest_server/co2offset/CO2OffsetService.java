package com.sky.tempest_server.co2offset;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.sky.tempest_server.co2offset.entities.CO2Offset;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class CO2OffsetService {
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectReader readJsonArrayToJsonNodeList = mapper.readerFor(new TypeReference<List<JsonNode>>() {});

    @Value("${goclimate.apikey}")
    String goClimateApiKey;

    @Autowired
    public CO2OffsetService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    HttpHeaders createHeaders(String username, String password){
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(StandardCharsets.US_ASCII) );
            String authHeader = "Basic " + new String( encodedAuth );
            set( "Authorization", authHeader );
        }};
    }

    public CO2Offset getCO2OffsetByAirportCodes(String airportCodeFrom, String airportCodeTo, String cabinClass) throws IOException {

        //SET API KEY IN HEADER
        HttpHeaders headers = createHeaders(goClimateApiKey, "");
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        //BUILD URL WITH QUERY PARAMETERS
        String url = "https://api.goclimate.com/v1/flight_footprint?segments[0][origin]=ARN&segments[0][destination]=BCN&cabin_class=economy";
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("segments[0][origin]", airportCodeFrom)
                .queryParam("segments[0][destination]", airportCodeTo)
                .queryParam("cabin_class", cabinClass)
                .queryParam("currencies[]", "EUR")
                .build().toUri().toString();

        //GET JSON RESPONSE AS STRING
        HttpEntity<String> response = restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                entity,
                String.class);

        //MANIPULATE JSON RESPONSE
        JsonNode responseJSON = mapper.readValue(response.getBody(), JsonNode.class);
        return new CO2Offset(responseJSON.get("footprint").intValue(),
                responseJSON.get("offset_prices").get(0).get("amount").intValue(),
                responseJSON.get("offset_prices").get(0).get("offset_url").textValue());
    }
}


