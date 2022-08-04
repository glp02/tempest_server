package com.sky.tempest_server.flights.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST,
        reason = "Invalid JSON structure.")
public class MyJsonProcessingException extends RuntimeException{
}
