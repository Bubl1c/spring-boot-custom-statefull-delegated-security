package com.amozh.auth;

import com.amozh.businesslogic.ApiController;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Andrii on 25.10.2015.
 */
public final class AuthUtils {
    public static HttpServletRequest asHttp(ServletRequest request) {
        return (HttpServletRequest) request;
    }

    public static HttpServletResponse asHttp(ServletResponse response) {
        return (HttpServletResponse) response;
    }

    public static boolean isAuthenticationCall(HttpServletRequest httpRequest, String resourcePath) {
        return ApiController.AUTHENTICATE_URL.equalsIgnoreCase(resourcePath) && httpRequest.getMethod().equals("POST");
    }

    public static boolean isManagerResourceCall(String resourcePath) {
        return resourcePath.indexOf(ApiController.MANAGER_RESOURCE_PATTERN) != -1;
    }
}
