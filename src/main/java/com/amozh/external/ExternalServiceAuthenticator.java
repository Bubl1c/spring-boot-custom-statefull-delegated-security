package com.amozh.external;

import com.amozh.auth.AuthenticationWithToken;

/**
 * Created by Andrii on 19.10.2015.
 */
public interface ExternalServiceAuthenticator {

    AuthenticationWithToken authenticate(String username, String password);
}