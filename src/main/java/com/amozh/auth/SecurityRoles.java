package com.amozh.auth;

/**
 * Created by Andrii on 22.10.2015.
 */
public final class SecurityRoles {

    protected static final String HAS_AUTHORITY_PRE = "hasRole('";
    protected static final String HAS_AUTHORITY_POST = "')";

    public static final String ROLE_USER = "ROLE_USER";
    public static final String HAS_AUTHORITY_ROLE_USER = HAS_AUTHORITY_PRE + ROLE_USER + HAS_AUTHORITY_POST;

    public static final String ROLE_MANAGER = "ROLE_MANAGER";
    public static final String HAS_AUTHORITY_ROLE_MANAGER = HAS_AUTHORITY_PRE + ROLE_MANAGER + HAS_AUTHORITY_POST;
}
