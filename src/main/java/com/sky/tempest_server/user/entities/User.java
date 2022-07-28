package com.sky.tempest_server.user.entities;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(unique = true,nullable = false,name = "user_id" , updatable = false)
    @val
    //@OnDelete(action = OnDeleteAction.NO_ACTION)
    private long id;

    @NotNull
    @Email
    @Column(unique = true,nullable = false, name = "user_email")
    private @NotBlank String email;

    @Column(nullable = false, name = "user_first_name")
    @NotBlank
    private String firstName;

    @Column(nullable = false, name = "user_last_name")
    @NotBlank
    private String lastName;

    @Column(nullable = false)
    @NotBlank
    private String password;

    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private boolean isAccountNonLocked = true;

    @Column(nullable = false)
    private boolean isAccountNonExpired = true;

    @Column(nullable = false)
    private boolean isCredentialsNonExpired = true;

    @Column(nullable = false)
    private boolean isEnabled = true;



    public User(@NotBlank String email,
                @NotBlank String firstName,
                @NotBlank String lastName,
                @NotBlank String password,
                @NotBlank UserRole role) {
        setEmail(email);
        setPassword(password);
        setFirstName(firstName);
        setLastName(lastName);
        setRole(role);
    }

    public boolean passwordMatches(String attemptedPassword) {
        return(this.getPassword().equals(attemptedPassword));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(role);
    }

    @Override
    public String getUsername() {
        return getEmail();
    }


}
