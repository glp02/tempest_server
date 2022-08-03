package com.sky.tempest_server.flights.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST,
        reason = "Location type must be either \"city\" or \"airport\".")
public class InvalidLocationTypeException extends RuntimeException{
}
