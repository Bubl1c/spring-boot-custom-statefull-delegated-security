package com.amozh.auth;

/**
 * Created by Andrii on 22.10.2015.
 */
public final class SecurityRoles {

    protected static final String HAS_AUTHORITY_PRE = "hasAuthority('";
    protected static final String HAS_AUTHORITY_POST = "')";

    public static final String ROLE_USER = "ROLE_USER";
    public static final String HAS_AUTHORITY_ROLE_USER = HAS_AUTHORITY_PRE + ROLE_USER + HAS_AUTHORITY_POST;

    public static final String ROLE_WORKER = "ROLE_WORKER";
    public static final String HAS_AUTHORITY_ROLE_WORKER = HAS_AUTHORITY_PRE + ROLE_WORKER + HAS_AUTHORITY_POST;
}
