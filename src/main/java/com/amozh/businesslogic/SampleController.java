package com.amozh.businesslogic;

import com.amozh.auth.CurrentlyLoggedUser;
import com.amozh.auth.DomainUser;
import com.amozh.auth.SecurityRoles;
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
    @PreAuthorize(SecurityRoles.HAS_AUTHORITY_ROLE_USER)
    public String hello(@CurrentlyLoggedUser DomainUser domainUser) {
        return sampleService.hello(domainUser);
    }

    @RequestMapping(value = WORKER_HELLO_URL, method = RequestMethod.POST)
    @PreAuthorize(SecurityRoles.HAS_AUTHORITY_ROLE_WORKER)
    public String workerHello(@CurrentlyLoggedUser DomainUser domainUser) {
        return "Hello, worker " + domainUser.getUsername();
    }

    @RequestMapping(value = FREE_URL, method = RequestMethod.GET)
    public String free() {
        return "Free!";
    }
}
