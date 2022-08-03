package com.sky.tempest_server.flights.repositories;

import com.sky.tempest_server.flights.entities.Flight;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightsRepository extends CrudRepository<Flight,String> {
}
