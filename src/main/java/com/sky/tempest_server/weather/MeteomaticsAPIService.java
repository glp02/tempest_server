package com.sky.tempest_server.weather;

        import org.apache.tomcat.util.codec.binary.Base64;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.beans.factory.annotation.Value;
        import org.springframework.boot.web.client.RestTemplateBuilder;
        import org.springframework.http.HttpEntity;
        import org.springframework.http.HttpHeaders;
        import org.springframework.http.HttpMethod;
        import org.springframework.http.MediaType;
        import org.springframework.stereotype.Service;
        import org.springframework.web.client.RestTemplate;

        import java.nio.charset.StandardCharsets;

@Service
public class MeteomaticsAPIService {

    @Value("${meteomatics.username}")
    String METEOMATICS_USERNAME;

    @Value("${meteomatics.password}")
    String METEOMATICS_PASSWORD;

    private final RestTemplate restTemplate;

    @Autowired
    MeteomaticsAPIService(RestTemplateBuilder restTemplateBuilder) {
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


    HttpEntity<String> getRequestResponse(String url, String queryUrlParams){
        HttpHeaders headers = createHeaders(METEOMATICS_USERNAME, METEOMATICS_PASSWORD);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        //GET JSON RESPONSE AS STRING
        return restTemplate.exchange(
                url,
//                url +  queryUrlParams,
                HttpMethod.GET,
                entity,
                String.class);
    }

}