package com.sky.tempest_server.user.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDTO {
    private String firstName;
    private String lastName;
    private String email;
}
