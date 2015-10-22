package com.amozh.auth.filters;

import com.amozh.auth.AuthHeaders;
import com.amozh.auth.AuthenticationWithToken;
import com.amozh.businesslogic.ApiController;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Andrii on 19.10.2015.
 */
public class WorkerAuthenticationFilter extends GenericFilterBean {

    private final static Logger logger = LoggerFactory.getLogger(WorkerAuthenticationFilter.class);
    private AuthenticationManager authenticationManager;
    private Set<String> workerEndpoints;

    public WorkerAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        prepareManagementEndpointsSet();
    }

    private void prepareManagementEndpointsSet() {
        workerEndpoints = new HashSet<>();
        workerEndpoints.add(ApiController.WORKER_HELLO_URL);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = asHttp(request);
        HttpServletResponse httpResponse = asHttp(response);

        Optional<String> token = Optional.fromNullable(httpRequest.getHeader(AuthHeaders.TOKEN));

        String resourcePath = new UrlPathHelper().getPathWithinApplication(httpRequest);

        try {
            if(postToWorkerEndpoints(resourcePath)) {
                if(!token.isPresent()) {
                    throw new PreAuthenticatedCredentialsNotFoundException("Token not found");
                }
                logger.debug("Trying to authenticate worker {} for worker endpoint by token", token);
                processWorkerEndpointTokenAuthentication(token.get());
            }

            logger.debug("WorkerAuthenticationFilter is passing request down the filter chain");
            chain.doFilter(request, response);
        } catch (AuthenticationException authenticationException) {
            SecurityContextHolder.clearContext();
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, authenticationException.getMessage());
        }
    }

    private HttpServletRequest asHttp(ServletRequest request) {
        return (HttpServletRequest) request;
    }

    private HttpServletResponse asHttp(ServletResponse response) {
        return (HttpServletResponse) response;
    }

    private boolean postToWorkerEndpoints(String resourcePath) {
        return workerEndpoints.contains(resourcePath);
    }

    private void processWorkerEndpointTokenAuthentication(String token) throws IOException {
        Authentication resultOfAuthentication = tryToAuthenticateWithToken(token);
        SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
    }

    private Authentication tryToAuthenticateWithToken(String token) {
        AuthenticationWithToken requestAuthentication = new AuthenticationWithToken(token, null);
        return tryToAuthenticate(requestAuthentication);
    }

    private Authentication tryToAuthenticate(Authentication requestAuthentication) {
        Authentication responseAuthentication = authenticationManager.authenticate(requestAuthentication);
        if (responseAuthentication == null || !responseAuthentication.isAuthenticated()) {
            throw new InternalAuthenticationServiceException("Unable to authenticate Worker for provided token.");
        }
        logger.debug("Worker successfully authenticated");
        return responseAuthentication;
    }
}

