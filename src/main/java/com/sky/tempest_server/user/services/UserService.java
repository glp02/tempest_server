package com.sky.tempest_server.user.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.sky.tempest_server.user.entities.User;
import com.sky.tempest_server.user.entities.UserDTO;
import com.sky.tempest_server.user.exceptions.RegistrationException;
import com.sky.tempest_server.user.repositories.UserRepository;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.sky.tempest_server.user.services.AuthenticationService.SIGNING_KEY;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    static final String PREFIX = "Bearer";

    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectReader readJsonArrayToJsonNodeList = mapper.readerFor(new TypeReference<List<JsonNode>>() {
    });

    public String register(String body) {
        try {
            JsonNode responseJSON = mapper.readValue(body, JsonNode.class);
            String email = responseJSON.get("email").textValue();
            String firstName = responseJSON.get("first_name").textValue();
            String lastName = responseJSON.get("last_name").textValue();
            String rawPassword = responseJSON.get("password").textValue();
            String encryptedPassword = EncoderHolder.getEncoder().encode(rawPassword);
            User newUser = new User(email, firstName, lastName, encryptedPassword, "USER");
            repository.save(newUser);
            return ("Registered new User: " + newUser.getDTO().toString());
        } catch (JsonProcessingException e) {
            throw new RegistrationException();
        }
    }

    public UserDTO getUserDetails(@RequestHeader("Authorization") String token) {
        if (token != null) {
            String useremail = Jwts.parser()
                    .setSigningKey(SIGNING_KEY)
                    .parseClaimsJws(token.replace(PREFIX, ""))
                    .getBody()
                    .getSubject();
            return new UserDTO(repository.findUserByEmail(useremail).get());
        } else return null;
    }

}
