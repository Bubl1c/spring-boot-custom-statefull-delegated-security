package com.amozh.service;

import com.amozh.auth.DomainUser;
import org.springframework.stereotype.Service;

/**
 * Created by Andrii on 19.10.2015.
 */
@Service
public class SampleService {

    public String hello(DomainUser domainUser) {
        return "Hello, " + domainUser.getUsername() + "!";
    }
}
