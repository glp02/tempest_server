package com.sky.tempest_server.user.services;

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
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

import static com.sky.tempest_server.user.services.AuthenticationService.SIGNING_KEY;

@Service
public class UserService implements UserDetailsService {

    public static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository repository;

    static final String PREFIX = "Bearer";

    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectReader readJsonArrayToJsonNodeList = mapper.readerFor(new TypeReference<List<JsonNode>>() {
    });

    public String register(String body) throws RegistrationException {
        try {
            JsonNode responseJSON = mapper.readValue(body, JsonNode.class);
            String email = responseJSON.get("email").textValue();
            String firstName = responseJSON.get("first_name").textValue();
            String lastName = responseJSON.get("last_name").textValue();
            String rawPassword = responseJSON.get("password").textValue();
            String encryptedPassword = ENCODER.encode(rawPassword);
            User newUser = new User(email, firstName, lastName, encryptedPassword, "USER");
            if(repository.findUserByEmail(email).isEmpty()){
                repository.save(newUser);
                return ("Registered new User: " + newUser.getDTO().toString());
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new RegistrationException();
        }
    }

    public UserDTO getUserDetails(@RequestHeader("Authorization") String token) {
        if (token != null) {
            String userEmail = Jwts.parser()
                    .setSigningKey(SIGNING_KEY)
                    .parseClaimsJws(token.replace(PREFIX, ""))
                    .getBody()
                    .getSubject();
            return repository.findUserByEmail(userEmail).get().getDTO();
        } else return null;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User currentUser = repository.findUserByEmail(username).get();
        return new org.springframework.security.core
                .userdetails.User(username, currentUser.getPassword()
                , true, true, true, true,
                AuthorityUtils.createAuthorityList(currentUser.getRole()));
    }

}
