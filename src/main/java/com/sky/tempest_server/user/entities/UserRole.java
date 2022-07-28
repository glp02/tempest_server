package com.sky.tempest_server.user.entities;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority { SUBSCRIBER, NONSUBSCRIBER;

    @Override
    public String getAuthority() {
        return this.toString();
    }
}
