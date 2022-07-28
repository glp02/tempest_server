package com.sky.tempest_server.user.services;
import com.sky.tempest_server.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try{
            return repository.findUserByEmail(username).get();
        } catch (NoSuchElementException e) {
            throw new UsernameNotFoundException("No user with email " + username + " was found.");
        }
    }
}
