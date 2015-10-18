package com.amozh.auth.admin;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * Created by Andrii on 19.10.2015.
 */
public class BackendAdminUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {
    public BackendAdminUsernamePasswordAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }
}
