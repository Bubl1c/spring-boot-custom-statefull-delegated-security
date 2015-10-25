package com.amozh;

import com.amozh.auth.AuthenticationWithToken;
import com.amozh.auth.SecurityRoles;
import com.amozh.businesslogic.ApiController;
import com.amozh.external.SomeExternalServiceAuthenticator;
import com.jayway.restassured.response.ValidatableResponse;
import org.junit.Assert;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.AuthorityUtils;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created by Andrii on 25.10.2015.
 */
public class TestingAuthenticationProvider {
    private static final String X_AUTH_USERNAME = "X-Auth-Username";
    private static final String X_AUTH_PASSWORD = "X-Auth-Password";

    public static String authenticateUser() {
        return authenticateUser("user", "password");
    }

    public static String authenticateManager() {
        return "manager1";
    }

    public static String authenticateUser(String username, String password) {
        AuthenticationWithToken authenticationWithToken = new SomeExternalServiceAuthenticator().authenticate(username, password);

        Assert.assertTrue(authenticationWithToken.isAuthenticated());
        Assert.assertEquals(authenticationWithToken.getAuthorities(),
                AuthorityUtils.commaSeparatedStringToAuthorityList(SecurityRoles.ROLE_USER));

        ValidatableResponse validatableResponse = given().header(X_AUTH_USERNAME, username).
                header(X_AUTH_PASSWORD, password).
                when().post(ApiController.AUTHENTICATE_URL).
                then().statusCode(HttpStatus.OK.value());
        String generatedToken = authenticationWithToken.getToken();
        validatableResponse.body("token", equalTo(generatedToken));

        return generatedToken;
    }
}
