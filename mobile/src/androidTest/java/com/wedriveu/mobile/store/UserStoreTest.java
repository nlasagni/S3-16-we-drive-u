package com.wedriveu.mobile.store;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import com.wedriveu.mobile.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * @author Nicola Lasagni on 18/07/2017.
 */
@RunWith(AndroidJUnit4.class)
public class UserStoreTest {

    private UserStore mUserStore;

    @Before
    public void init() {
        mUserStore = new UserStoreImpl(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void getUser() throws Exception {
        assertTrue(mUserStore.getUser() == null);
    }

    @Test
    public void storeUser() throws Exception {
        User user = new User("Mario", "Rossi");
        mUserStore.storeUser(user);
        assertEquals(user, mUserStore.getUser());
    }

}