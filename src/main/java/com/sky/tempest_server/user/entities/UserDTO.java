package com.sky.tempest_server.user.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String firstName;
    private String lastName;
    private String email;

    public UserDTO(User user){
        this(user.getFirstName(), user.getLastName(), user.getEmail());
    }
}
