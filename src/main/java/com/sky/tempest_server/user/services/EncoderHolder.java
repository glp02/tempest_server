package com.sky.tempest_server.user.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncoderHolder {
    private static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    public static BCryptPasswordEncoder getEncoder(){
        return encoder;
    }
}
