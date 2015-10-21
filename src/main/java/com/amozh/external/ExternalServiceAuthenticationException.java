package com.amozh.external;

import org.springframework.security.core.AuthenticationException;

/**
 * Created by Andrii on 21.10.2015.
 */
public class ExternalServiceAuthenticationException extends AuthenticationException {
    public ExternalServiceAuthenticationException(String msg, Throwable t) {
        super(msg, t);
    }

    public ExternalServiceAuthenticationException(String msg) {
        super(msg);
    }
}
