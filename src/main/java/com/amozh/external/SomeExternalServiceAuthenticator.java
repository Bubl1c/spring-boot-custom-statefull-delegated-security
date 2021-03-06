package com.amozh.external;

import com.amozh.auth.AuthenticationWithToken;
import com.amozh.auth.DomainUser;
import com.amozh.auth.SecurityRoles;
import com.amozh.auth.TokenService;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
/**
 * Created by Andrii on 19.10.2015.
 */
public class SomeExternalServiceAuthenticator implements ExternalServiceAuthenticator {

    @Override
    public AuthenticationWithToken authenticate(String username, String password) {

        // Do all authentication mechanisms required by external web service protocol and validated response.
        // Throw descendant of Spring AuthenticationException in case of unsucessful authentication.
        // For example BadCredentialsException

        // ...
        // ...

        // If authentication to external service succeeded then create authenticated wrapper
        // with proper Principal and GrantedAuthorities.
        // GrantedAuthorities may come from external service authentication or be hardcoded at our layer
        // as they are here with ROLE_DOMAIN_USER

        if(!isValidCredentials(username, password)){
            throw new ExternalServiceAuthenticationException("Invalid user credentials!");
        }

        AuthenticationWithToken authenticationWithToken =
                    new AuthenticationWithToken(
                            new DomainUser(username),
                            null,
                            AuthorityUtils.commaSeparatedStringToAuthorityList(SecurityRoles.ROLE_USER.toString()));

        String newToken = username + password;
        authenticationWithToken.setToken(newToken);

        return authenticationWithToken;
    }

    private boolean isValidCredentials(String username, String password) {
        String validUsername = "user";
        String validPassword = "password";
        return username.equalsIgnoreCase(validUsername) && password.equals(validPassword);
    }

}
