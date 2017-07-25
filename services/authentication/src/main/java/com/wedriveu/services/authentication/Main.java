package com.wedriveu.services.authentication;

import com.wedriveu.services.authentication.boundary.AuthenticationServiceImpl;

/**
 * Created by Michele on 18/07/2017.
 */
public class Main {

    public static void main(String[] args) {
        new AuthenticationServiceImpl().startService();
    }
}
