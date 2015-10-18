package com.amozh.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Andrii on 19.10.2015.
 */
public class TokenResponse {
    @JsonProperty
    private String token;

    public TokenResponse() {
    }

    public TokenResponse(String token) {
        this.token = token;
    }
}
