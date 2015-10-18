package com.amozh.external;

import com.amozh.auth.AuthenticationWithToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Created by Andrii on 19.10.2015.
 */
public class AuthenticatedExternalWebService extends AuthenticationWithToken {

    private ExternalWebServiceStub externalWebService;

    public AuthenticatedExternalWebService(Object aPrincipal, Object aCredentials, Collection<? extends GrantedAuthority> anAuthorities) {
        super(aPrincipal, aCredentials, anAuthorities);
    }

    public void setExternalWebService(ExternalWebServiceStub externalWebService) {
        this.externalWebService = externalWebService;
    }

    public ExternalWebServiceStub getExternalWebService() {
        return externalWebService;
    }
}