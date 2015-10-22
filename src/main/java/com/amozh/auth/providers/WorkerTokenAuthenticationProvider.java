package com.amozh.auth.providers;

import com.amozh.auth.AuthenticationWithToken;
import com.amozh.auth.SecurityRoles;
import com.google.common.base.Optional;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by Andrii on 19.10.2015.
 */
public class WorkerTokenAuthenticationProvider implements AuthenticationProvider {

    public static final String INVALID_WORKER_TOKEN = "Invalid Worker Token";

    private Set<String> validWorkerTokens;

    public WorkerTokenAuthenticationProvider() {
        this.validWorkerTokens = prepareValidWorkerTokens();
    }

    private Set<String> prepareValidWorkerTokens() {
        Set<String> tokensSet = new ConcurrentSkipListSet<>();
        tokensSet.add("worker1");
        tokensSet.add("worker2");
        return tokensSet;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getPrincipal();

        if (!isValidToken(token)) {
            throw new BadCredentialsException(INVALID_WORKER_TOKEN);
        }

        return new AuthenticationWithToken(token, null,
                AuthorityUtils.commaSeparatedStringToAuthorityList(SecurityRoles.ROLE_WORKER.toString()));
    }

    private boolean isValidToken(String token) {
        return !StringUtils.isEmpty(token) && this.validWorkerTokens.contains(token);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(AuthenticationWithToken.class);
    }
}

