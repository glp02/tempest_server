package com.sky.tempest_server.user;

import com.sky.tempest_server.user.entities.User;
import com.sky.tempest_server.user.entities.UserDTO;
import com.sky.tempest_server.user.exceptions.UserAlreadyExistsException;
import com.sky.tempest_server.user.exceptions.UserNotFoundException;
import com.sky.tempest_server.user.exceptions.WrongPasswordException;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@NoArgsConstructor
public class UserService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private ModelMapper mapper;

    private UserDTO maptoDTO(User user){
        return this.mapper.map(user, UserDTO.class);
    }

    public UserDTO registerUser(String email, String firstName, String lastName, String password) {
        Optional<User> optionalUser = repository.findUserByEmail(email);
        if(optionalUser.isEmpty()){
            User newUser = new User(email,firstName,lastName,password);
            repository.save(newUser);
            System.out.println("Registered user: " + newUser);
            return maptoDTO(newUser);
        } else {
            throw new UserAlreadyExistsException();
        }
    }

    public UserDTO loginUser(String email, String password) throws WrongPasswordException {
        User user = this.repository.findUserByEmail(email).orElseThrow(UserNotFoundException:: new);
        if(user.passwordMatches(password)){
            return this.maptoDTO(user);
        } else{
            throw new WrongPasswordException();
        }
    }

    public UserDTO logoutUser(String email, String password) throws WrongPasswordException {
        User user = this.repository.findUserByEmail(email).orElseThrow(UserNotFoundException:: new);
        if(user.passwordMatches(password)){
            return this.maptoDTO(user);
        } else{
            throw new WrongPasswordException();
        }
    }

    public UserDTO changePassword(String email, String password, String newPassword) throws WrongPasswordException {
        Optional<User> optionalUser = repository.findUserByEmail(email);
        if(optionalUser.isPresent() && optionalUser.get().passwordMatches(password)){
            User user = optionalUser.get();
            System.out.println(user.toString() + " password changed to " + newPassword);
            user.setPassword(newPassword);
            repository.save(user);
            return maptoDTO(user);
        } else {
            throw new WrongPasswordException();
        }
    }

    public UserDTO deleteUser(String email, String password) throws WrongPasswordException {
        User user = repository.findUserByEmail(email).orElseThrow(UserNotFoundException:: new);
        if(user.passwordMatches(password)){
            repository.delete(user);
            return maptoDTO(user);
        } else {
            throw new WrongPasswordException();
        }
    }

    public String getUsers(){
        return repository.findAll().toString();
    }
}
