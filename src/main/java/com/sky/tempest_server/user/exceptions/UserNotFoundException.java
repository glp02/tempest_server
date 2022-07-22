package com.sky.tempest_server.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.EntityNotFoundException;

@ResponseStatus(
        code = HttpStatus.NOT_FOUND,
        reason = "Account not found matching given email.")
public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(){
        super("Account not found matching given email.");
    }
}
