package com.amozh.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Created by Andrii on 25.10.2015.
 */
@Service
public class RedisAuthenticationStorage implements AuthenticationStorage {
    @Autowired
    RedisTemplate<String, AuthenticatedUser> template;

    @Override
    public void store(String key, AuthenticatedUser authentication) {
        setValue(key, authentication);
    }

    @Override
    public boolean contains(String key) {
        return getValue(key) != null;
    }

    @Override
    public AuthenticatedUser retrieve(String key) {
        return getValue(key);
    }

    @Override
    public void remove(String key) {
        template.delete(key);
    }

    private AuthenticatedUser getValue(final String key) {
        return template.opsForValue().get(key);
    }

    private void setValue(final String key, final AuthenticatedUser value) {
        template.opsForValue().set(key, value);
        template.expire(key, KEY_EXPIRATION_TIME_MINUTES, TimeUnit.MINUTES);
    }
}
