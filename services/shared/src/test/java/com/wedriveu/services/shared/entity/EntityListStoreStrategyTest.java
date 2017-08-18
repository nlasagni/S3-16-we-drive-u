package com.wedriveu.services.shared.entity;

import com.wedriveu.services.shared.store.EntityListStoreStrategy;
import com.wedriveu.services.shared.store.JsonFileEntityListStoreStrategyImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.assertTrue;

/**
 * @author Nicola Lasagni on 31/07/2017.
 */
public class EntityListStoreStrategyTest {

    private static final int ENTITY_COUNT = 5;
    private static final String ENTITY = "entity";
    private static final String DATABASE_FILE_NAME = EntityListStoreStrategyTest.class.getSimpleName() + ".json";

    private EntityListStoreStrategy<String> stringEntityStoreStrategy;
    private List<String> entities;

    @Before
    public void setUp() throws Exception {
        stringEntityStoreStrategy = new JsonFileEntityListStoreStrategyImpl<>(String.class, DATABASE_FILE_NAME);
        entities = createEntities();
    }

    @Test
    public void storeAndGet() throws Exception {
        stringEntityStoreStrategy.storeEntities(entities);
        List<String> storedEntities = stringEntityStoreStrategy.getEntities();
        assertTrue(storedEntities != null && storedEntities.size() == entities.size());
    }

    @Test
    public void clear() throws Exception {
        stringEntityStoreStrategy.clear();
        assertTrue(stringEntityStoreStrategy.getEntities().isEmpty());
    }

    private List<String> createEntities() {
        List<String> entities = new ArrayList<>();
        IntStream.range(0, ENTITY_COUNT).forEach(id -> {
            entities.add(ENTITY + id);
        });
        return entities;
    }

}