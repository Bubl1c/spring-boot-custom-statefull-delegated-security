package com.amozh.auth;

/**
 * Created by Andrii on 25.10.2015.
 */
public interface AuthenticationStorage {
    int KEY_EXPIRATION_TIME_MINUTES = 2;

    void store(String key, AuthenticatedUser authentication);
    boolean contains(String key);
    AuthenticatedUser retrieve(String key);
    void remove(String key);
}
