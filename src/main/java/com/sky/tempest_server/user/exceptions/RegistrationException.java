package com.sky.tempest_server.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT,
        reason = "Registration Unsuccessful. Email may be in use.")
public class RegistrationException extends Exception{
    public RegistrationException(){
        super("Registration Unsuccessful");
    }
}
