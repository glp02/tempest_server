package com.sky.tempest_server.user.entities;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(unique = true,nullable = false,name = "user_id" , updatable = false)
    @val
    //@OnDelete(action = OnDeleteAction.NO_ACTION)
    private long id;

    @NotNull
    @Email(message = "Email should be valid")
    @Column(unique = true,nullable = false, name = "user_email")
    private @NotBlank String email;

    @Column(nullable = false, name = "user_first_name")
    @Size(min=1,max=25)
    @Pattern(regexp = "^[A-Za-z]+((\\s)?((\\'|\\-|\\.)?([A-Za-z])+))*$")
    @NotBlank
    private String firstName;

    @Column(nullable = false, name = "user_last_name")
    @Size(min=1,max=25)
    @Pattern(regexp = "^[A-Za-z]+((\\s)?((\\'|\\-|\\.)?([A-Za-z])+))*$")
    @NotBlank
    private String lastName;

    @Column(nullable = false)
    @NotBlank
    private String password;

    @Column(nullable = false)
    private String role;

    public User(@NotBlank String email,
                @NotBlank String firstName,
                @NotBlank String lastName,
                @NotBlank String password,
                @NotBlank String role) {
        setEmail(email);
        setPassword(password);
        setFirstName(firstName);
        setLastName(lastName);
        setRole(role);
    }

    public UserDTO getDTO(){
        return new UserDTO(this);
    }

}
