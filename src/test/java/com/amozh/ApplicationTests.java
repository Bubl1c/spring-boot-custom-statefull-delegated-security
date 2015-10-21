package com.amozh;

import com.amozh.auth.AuthenticationWithToken;
import com.amozh.businesslogic.ApiController;
import com.amozh.external.ExternalServiceAuthenticator;
import com.amozh.businesslogic.SampleService;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class, ApplicationTests.SecurityTestConfig.class})
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class ApplicationTests {

	private static final String X_AUTH_USERNAME = "X-Auth-Username";
	private static final String X_AUTH_PASSWORD = "X-Auth-Password";
	private static final String X_AUTH_TOKEN = "X-Auth-Token";

	@Value("${local.server.port}")
	int port;

//	@Value("${keystore.file}")
//	String keystoreFile;
//
//	@Value("${keystore.pass}")
//	String keystorePass;

	@Autowired
	ExternalServiceAuthenticator mockedExternalServiceAuthenticator;

	@Autowired
	SampleService mockedSampleService;

	@Configuration
	public static class SecurityTestConfig {
		@Bean
		public ExternalServiceAuthenticator someExternalServiceAuthenticator() {
			return mock(ExternalServiceAuthenticator.class);
		}

		@Bean
		@Primary
		public SampleService sampleService() {
			return mock(SampleService.class);
		}
	}

	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
//		RestAssured.keystore(keystoreFile, keystorePass);
		RestAssured.port = port;
		Mockito.reset(mockedExternalServiceAuthenticator, mockedSampleService);
	}

	@Test
	public void freeEndpoint_availableForEveryone() {
		ValidatableResponse r = when().get(ApiController.FREE_URL).then();
		System.out.println("freeEndpoint_availableForEveryone " + r.extract().statusCode());
		r.statusCode(HttpStatus.OK.value()).toString().equals("Free!");
	}

	@Test
	public void authenticate_withoutPassword_unauthorized() {
		given().header(X_AUTH_USERNAME, "user").
				when().post(ApiController.AUTHENTICATE_URL).
				then().statusCode(HttpStatus.UNAUTHORIZED.value());

		BDDMockito.verifyNoMoreInteractions(mockedExternalServiceAuthenticator);
	}

	@Test
	public void authenticate_InvalidUsernamePassword_unauthorized() {
		String username = "user";
		String password = "InvalidPassword";
		given().header(X_AUTH_USERNAME, username).header(X_AUTH_PASSWORD, password).
				when().post(ApiController.AUTHENTICATE_URL).
				then().statusCode(HttpStatus.UNAUTHORIZED.value());

		BDDMockito.verifyNoMoreInteractions(mockedExternalServiceAuthenticator);
	}

	@Test
	public void authenticate_ValidUsernamePassword_ok() {
		String username = "user";
		String password = "password";
		given().header(X_AUTH_USERNAME, username).header(X_AUTH_PASSWORD, password).
				when().post(ApiController.AUTHENTICATE_URL).
				then().statusCode(HttpStatus.OK.value());

		BDDMockito.verifyNoMoreInteractions(mockedExternalServiceAuthenticator);
	}

	@Test
	public void helloEndpoint_unauthorized() {
		when().get("/hello").
				then().statusCode(HttpStatus.UNAUTHORIZED.value());
	}

	@Test
	public void helloEndpoint_withInvalidCredentials_unauthorized() {
		String username = "user";
		String password = "InvalidPassword";
		given().header(X_AUTH_USERNAME, username).header(X_AUTH_PASSWORD, password).
				when().get("/hello").
				then().statusCode(HttpStatus.UNAUTHORIZED.value());
	}

	@Test
	public void helloEndpoint_withValidCredentials_ok() {
		String username = "user";
		String password = "password";
		given().header(X_AUTH_USERNAME, username).header(X_AUTH_PASSWORD, password).
				when().get("/hello").
				then().statusCode(HttpStatus.OK.value());
	}

	@Test
	public void authenticate_withValidUsernameAndPassword_returnsToken() {
		authenticateByUsernameAndPasswordAndGetToken();
	}

	@Test
	public void authenticate_withInvalidUsernameOrPassword_returnsUnauthorized() {
		String username = "user";
		String password = "InvalidPassword";

		BDDMockito.when(mockedExternalServiceAuthenticator.authenticate(anyString(), anyString())).
				thenThrow(new BadCredentialsException("Invalid Credentials"));

		given().header(X_AUTH_USERNAME, username).header(X_AUTH_PASSWORD, password).
				when().post(ApiController.AUTHENTICATE_URL).
				then().statusCode(HttpStatus.UNAUTHORIZED.value());
	}

	@Test
	public void hello_withoutToken_unauthorized() {
		when().get(ApiController.HELLO_URL).
				then().statusCode(HttpStatus.UNAUTHORIZED.value());
	}

	@Test
	public void hello_InvalidToken_unathorized() {
		given().header(X_AUTH_TOKEN, "InvalidToken").
				when().get(ApiController.HELLO_URL).
				then().statusCode(HttpStatus.UNAUTHORIZED.value());
	}

	@Test
	public void hello_ValidToken_returnsData() {
		String generatedToken = authenticateByUsernameAndPasswordAndGetToken();

		given().header(X_AUTH_TOKEN, generatedToken).
				when().get(ApiController.HELLO_URL).
				then().statusCode(HttpStatus.OK.value());
	}

	private String authenticateByUsernameAndPasswordAndGetToken() {
		String username = "user";
		String password = "password";

		AuthenticationWithToken authenticationWithToken = new AuthenticationWithToken(username, null,
				AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_DOMAIN_USER"));
		BDDMockito.when(mockedExternalServiceAuthenticator.authenticate(eq(username), eq(password))).
				thenReturn(authenticationWithToken);

		ValidatableResponse validatableResponse = given().header(X_AUTH_USERNAME, username).
				header(X_AUTH_PASSWORD, password).
				when().post(ApiController.AUTHENTICATE_URL).
				then().statusCode(HttpStatus.OK.value());
		String generatedToken = authenticationWithToken.getToken();
		validatableResponse.body("token", equalTo(generatedToken));

		return generatedToken;
	}

}
