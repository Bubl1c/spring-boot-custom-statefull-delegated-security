package com.amozh.auth.manager;

import com.amozh.auth.*;
import com.amozh.auth.AuthenticationStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by Andrii on 19.10.2015.
 */
public class ManagerAuthenticationProvider implements AuthenticationProvider {

    public static final String INVALID_MANAGER_TOKEN = "Invalid Manager Token";

    private TokenService tokenService;

    private Set<String> validManagerTokens;

    public ManagerAuthenticationProvider(TokenService tokenService) {
        this.tokenService = tokenService;
        this.validManagerTokens = prepareValidManagerTokens();
    }

    private Set<String> prepareValidManagerTokens() {
        Set<String> tokensSet = new ConcurrentSkipListSet<>();
        tokensSet.add("manager1");
        tokensSet.add("manager2");
        return tokensSet;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Optional<String> token = (Optional) authentication.getPrincipal();

        if (!isValidToken(token)) {
            throw new BadCredentialsException(INVALID_MANAGER_TOKEN);
        }

        AuthenticationWithToken resultOfAuthentication =  new AuthenticationWithToken(
                new DomainUser("hardcoded_manager"),
                null,
                AuthorityUtils.commaSeparatedStringToAuthorityList(SecurityRoles.ROLE_MANAGER.toString()));
        resultOfAuthentication.setToken(token.get());

        tokenService.store(resultOfAuthentication.getToken(), resultOfAuthentication);

        return resultOfAuthentication;
    }

    private boolean isValidToken(Optional<String> token) {
        return token.isPresent() && this.validManagerTokens.contains(token.get());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(ManagerAuthenticationWithToken.class);
    }
}

