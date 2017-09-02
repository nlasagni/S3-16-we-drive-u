package com.wedriveu.services.authentication.entity;

import com.wedriveu.services.shared.model.User;
import com.wedriveu.shared.util.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author Nicola Lasagni on 02/09/2017.
 */
public class UserStoreTest {

    private UserStore userStore;
    private User user = User.USERS[0];

    @Before
    public void setUp() {
        try {
            userStore = new UserStoreImpl();
        } catch (IOException e) {
            Log.error(this.getClass().getSimpleName(), "Error on setup");
        }
    }

    @After
    public void tearDown() {
        if (userStore != null) {
            userStore.clear();
        }
    }

    @Test
    public void addUser() throws Exception {
        assertTrue(userStore.addUser(user));
    }

}