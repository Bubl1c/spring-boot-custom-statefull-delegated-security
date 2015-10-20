package com.amozh.businesslogic;

import com.amozh.auth.CurrentlyLoggedUser;
import com.amozh.auth.DomainUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Andrii on 18.10.2015.
 */
@RestController
public class SampleController implements ApiController{
    @Autowired
    private SampleService sampleService;

    @RequestMapping(value = HELLO_URL, method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('ROLE_DOMAIN_USER')")
    public String hello(@CurrentlyLoggedUser DomainUser domainUser) {
        return sampleService.hello(domainUser);
    }

    @RequestMapping(value = FREE_URL, method = RequestMethod.GET)
    public void free(@CurrentlyLoggedUser DomainUser domainUser) {
    }
}
