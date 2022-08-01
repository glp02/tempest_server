package com.sky.tempest_server.user.exceptions;

public class RegistrationException extends RuntimeException{
    public RegistrationException(){
        super("Registration Unsuccessful");
    }
}
