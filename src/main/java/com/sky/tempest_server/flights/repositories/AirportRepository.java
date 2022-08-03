package com.sky.tempest_server.flights.repositories;

import com.sky.tempest_server.flights.entities.Airport;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirportRepository extends CrudRepository<Airport,String> {
}
