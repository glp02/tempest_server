package com.sky.tempest_server.user;

import com.sky.tempest_server.user.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User,Long> {
    Optional<User> findUserByEmail(String email);
}