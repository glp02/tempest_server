package com.sky.tempest_server.flights.exceptions;

import com.sky.tempest_server.flights.entities.Journey;
import org.hibernate.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND,
        reason = "Journey not found matching id.")
public class JourneyNotFoundException extends ObjectNotFoundException {
    public JourneyNotFoundException(Long id) {
        super(id, Journey.class.getCanonicalName());
    }
}

