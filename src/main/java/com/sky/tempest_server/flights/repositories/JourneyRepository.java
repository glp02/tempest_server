package com.sky.tempest_server.flights.repositories;

import com.sky.tempest_server.flights.entities.Journey;
import com.sky.tempest_server.user.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JourneyRepository extends CrudRepository<Journey,Long> {
    List<Journey> findJourneysByUser(User user);
}
