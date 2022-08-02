package com.sky.tempest_server.user.controllers;

import com.sky.tempest_server.user.entities.UserDTO;
import com.sky.tempest_server.user.exceptions.RegistrationException;
import com.sky.tempest_server.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @Autowired
    UserService service;
    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public String register(@RequestBody String body) throws RegistrationException {
        return service.register(body);
    }

    @RequestMapping(value = "/getuserdetails",method = RequestMethod.GET)
    public UserDTO getUserDetails(@RequestHeader("Authorization") String token) {
        return service.getUserDetails(token);
    }
}


