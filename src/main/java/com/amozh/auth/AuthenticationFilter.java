package com.amozh.auth;

import com.amozh.auth.manager.ManagerAuthenticationWithToken;
import com.amozh.businesslogic.ApiController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Created by Andrii on 18.10.2015.
 */
public class AuthenticationFilter extends GenericFilterBean {

    private final static Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    public static final String TOKEN_SESSION_KEY = "token";
    public static final String USER_SESSION_KEY = "user";
    private AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    private static final String MANAGER_ENDPOINT_URL_PATTERN = "/manager";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = AuthUtils.asHttp(request);
        HttpServletResponse httpResponse = AuthUtils.asHttp(response);

        Optional<String> username = Optional.ofNullable(httpRequest.getHeader(AuthHeaders.USERNAME));
        Optional<String> password = Optional.ofNullable(httpRequest.getHeader(AuthHeaders.PASSWORD));
        Optional<String> token = Optional.ofNullable(httpRequest.getHeader(AuthHeaders.TOKEN));

        String resourcePath = new UrlPathHelper().getPathWithinApplication(httpRequest);

        try {
            if (AuthUtils.isAuthenticationCall(httpRequest, resourcePath)) {
                logger.debug("Trying to authenticate user {" + username + "} by "+AuthHeaders.USERNAME+" method");
                processUsernamePasswordAuthentication(httpResponse, username, password);
                return;
            }

            if (AuthUtils.isManagerResourceCall(resourcePath)) {
                logger.debug("Trying to authenticate manager by "+AuthHeaders.TOKEN+" method. Token: {" + token + "}");
                processManagerAuthentication(token);
            }
            else if (token.isPresent()) {
                logger.debug("Trying to authenticate " + AuthHeaders.TOKEN + ": {" + token + "}");
                processTokenAuthentication(token);
            }

            logger.debug("AuthenticationFilter is passing request down the filter chain");
//            addSessionContextToLogging(); Uncomment to use HTTPS
            chain.doFilter(request, response);
        } catch (InternalAuthenticationServiceException internalAuthenticationServiceException) {
            SecurityContextHolder.clearContext();
            logger.error("Internal authentication service exception", internalAuthenticationServiceException);
            httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (AuthenticationException authenticationException) {
            SecurityContextHolder.clearContext();
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, authenticationException.getMessage());
        } finally {
            MDC.remove(TOKEN_SESSION_KEY);
            MDC.remove(USER_SESSION_KEY);
        }
    }

    private void addSessionContextToLogging() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String tokenValue = "EMPTY";
        if (authentication != null && !StringUtils.isEmpty(authentication.getDetails().toString())) {
            MessageDigestPasswordEncoder encoder = new MessageDigestPasswordEncoder("SHA-1");
            tokenValue = encoder.encodePassword(authentication.getDetails().toString(), "not_so_random_salt");
        }
        MDC.put(TOKEN_SESSION_KEY, tokenValue);

        String userValue = "EMPTY";
        if (authentication != null && !StringUtils.isEmpty(authentication.getPrincipal().toString())) {
            userValue = authentication.getPrincipal().toString();
        }
        MDC.put(USER_SESSION_KEY, userValue);
    }

    /*
    * UsernamePasswordAuthentication
    */
    private void processUsernamePasswordAuthentication(HttpServletResponse httpResponse, Optional<String> username, Optional<String> password) throws IOException {
        Authentication resultOfAuthentication = tryToAuthenticateWithUsernameAndPassword(username, password);
        SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
        httpResponse.setStatus(HttpServletResponse.SC_OK);
        TokenResponse tokenResponse = new TokenResponse(resultOfAuthentication.getDetails().toString());
        String tokenJsonResponse = new ObjectMapper().writeValueAsString(tokenResponse);
        httpResponse.addHeader("Content-Type", "application/json");
        httpResponse.getWriter().print(tokenJsonResponse);
    }

    private Authentication tryToAuthenticateWithUsernameAndPassword(Optional<String> username, Optional<String> password) {
        UsernamePasswordAuthenticationToken requestAuthentication = new UsernamePasswordAuthenticationToken(username, password);
        return tryToAuthenticate(requestAuthentication);
    }

    /*
    * ManagerAuthentication
    */
    private void processManagerAuthentication(Optional<String> token) {
        ManagerAuthenticationWithToken managerAuthenticationWithToken = new ManagerAuthenticationWithToken(token, null);
        Authentication resultOfAuthentication = tryToAuthenticate(managerAuthenticationWithToken);
        SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
    }

    /*
    * TokenAuthentication
    */
    private void processTokenAuthentication(Optional<String> token) {
        Authentication resultOfAuthentication = tryToAuthenticateWithToken(token);
        SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
    }

    private Authentication tryToAuthenticateWithToken(Optional<String> token) {
        PreAuthenticatedAuthenticationToken requestAuthentication = new PreAuthenticatedAuthenticationToken(token, null);
        return tryToAuthenticate(requestAuthentication);
    }

    /**
     * Common authentication method
     * <p>
     * Uses {@link ProviderManager} to decide what {@link AuthenticationProvider} to use
     * based on the class of {@code requestAuthentication} param
     * </p>
     * <p>
     * Example: if you pass {@link UsernamePasswordAuthenticationToken} as this method param -
     *          {@link ProviderManager} will use {@link UsernamePasswordAuthenticationProvider#supports(Class)}
     *          method to check if it supports {@code UsernamePasswordAuthenticationToken} class and if it does then
     *          {@link UsernamePasswordAuthenticationProvider#authenticate(Authentication)} method will be called.
     *
     * <br/><br/>You may find registered authentication providers
     * in {@link SecurityConfig#configure(AuthenticationManagerBuilder)} configuration metod
     * <p/>
     * @param requestAuthentication
     * @return
     */
    private Authentication tryToAuthenticate(Authentication requestAuthentication) {
        Authentication responseAuthentication = authenticationManager.authenticate(requestAuthentication);
        if (responseAuthentication == null || !responseAuthentication.isAuthenticated()) {
            throw new InternalAuthenticationServiceException(
                    "Unable to authenticate " + requestAuthentication.getClass().getName()
                            + " for provided principal: " + requestAuthentication.getPrincipal()
                            + ", credentials: " + requestAuthentication.getCredentials());
        }
        logger.debug(requestAuthentication.getClass().getName() + " successfully authenticated");
        return responseAuthentication;
    }
}
