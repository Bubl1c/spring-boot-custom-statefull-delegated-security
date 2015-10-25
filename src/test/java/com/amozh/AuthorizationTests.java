package com.amozh;

import com.amozh.businesslogic.ApiController;
import com.amozh.external.ExternalServiceAuthenticator;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static com.jayway.restassured.RestAssured.given;
import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class AuthorizationTests {

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

	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
//		RestAssured.keystore(keystoreFile, keystorePass);
		RestAssured.port = port;
//		Mockito.reset(mockedExternalServiceAuthenticator, mockedSampleService);
	}

	@Autowired
	ExternalServiceAuthenticator externalServiceAuthenticator;



}
