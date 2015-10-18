package com.amozh.auth;

/**
 * Created by Andrii on 19.10.2015.
 */
public class DomainUser {
    private String username;

    public DomainUser(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return username;
    }
}
