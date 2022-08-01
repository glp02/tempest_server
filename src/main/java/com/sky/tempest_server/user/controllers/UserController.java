package com.sky.tempest_server.user.controllers;

import com.sky.tempest_server.user.entities.UserDTO;
import com.sky.tempest_server.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    UserService service;

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public String register(@RequestBody String body){
        return service.register(body);
    }

    @RequestMapping(value = "/getuserdetails",method = RequestMethod.GET)
    public UserDTO getUserDetails(@RequestHeader("Authorization") String token) {
        return service.getUserDetails(token);
    }
}


