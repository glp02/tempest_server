package com.sky.tempest_server.user.controllers;

import com.sky.tempest_server.user.UserRepository;
import com.sky.tempest_server.user.entities.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    UserRepository repository;

    @RequestMapping("/login")
    public String getUser(@RequestBody String body){
        //return repository.findUserByEmail()
        return "Your user details here";
    }

    @RequestMapping("/gettext")
    public String gettext(){
        return "You should only see this if you're logged in.";
    }
}
