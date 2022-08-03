package com.sky.tempest_server.flights.repositories;

import com.sky.tempest_server.flights.entities.Journey;
import org.springframework.data.repository.CrudRepository;

public interface FlightsRepository extends CrudRepository<Journey,Long> {
}
