package com.wedriveu.services.shared.entity;

import com.wedriveu.services.shared.utilities.Constants;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Nicola Lasagni on 31/07/2017.
 */
public class EntityStoreStrategyTest {

    private static final String ENTITY = "entity";

    private EntityStoreStrategy<String> stringEntityStoreStrategy;

    @Before
    public void setUp() throws Exception {
        stringEntityStoreStrategy = new JsonFileStoreStrategyImpl<>(Constants.BOOKINGS_DATABASE_PATH);
    }

    @Test
    public void get() throws Exception {
        storeEntity();
        String string = stringEntityStoreStrategy.get();
        assertTrue(string != null && string.equals(ENTITY));
    }

    @Test
    public void store() throws Exception {
        assertTrue(storeEntity());
    }

    private boolean storeEntity() {
        return stringEntityStoreStrategy.store(ENTITY);
    }

}