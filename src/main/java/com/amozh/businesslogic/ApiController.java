package com.amozh.businesslogic;

/**
 * Created by Andrii on 19.10.2015.
 */
public interface ApiController {
    String API_PATH = "";

    String AUTHENTICATE_URL = API_PATH + "/authenticate";
    String FREE_URL = "/free";
    String HELLO_URL = API_PATH + "/hello";

    //Management TODO: change here and in filter to "/manage/*" to unify management paths
    String MANAGER_RESOURCE_PATTERN = "/manager";
    String MANAGER_HELLO_URL = "/manager/hello";
}
