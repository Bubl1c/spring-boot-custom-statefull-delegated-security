package com.amozh.auth;

/**
 * Created by Andrii on 25.10.2015.
 */
public class AuthenticatedUser {
    private String username;
    private Long storedTime;

    public AuthenticatedUser() {
    }

    public AuthenticatedUser(String username, Long storedTime) {
        this.username = username;
        this.storedTime = storedTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getStoredTime() {
        return storedTime;
    }

    public void setStoredTime(Long storedTime) {
        this.storedTime = storedTime;
    }
}
