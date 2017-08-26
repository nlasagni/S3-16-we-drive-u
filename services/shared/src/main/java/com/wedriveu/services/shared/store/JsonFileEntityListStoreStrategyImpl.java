package com.wedriveu.services.shared.store;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * class for storing a list of classes into a json file
 *
 * @author Nicola Lasagni on 31/07/2017.
 */
public class JsonFileEntityListStoreStrategyImpl<T> implements EntityListStoreStrategy<T> {

    private static final String STORE_FOLDER = "store";

    private Class<T> entityClass;
    private File file;
    private ObjectMapper objectMapper;
    private String fileName;

    /**
     * creates a new store for the entity classes given in the filename file
     *
     * @param entityClass the classes to be put in the store
     * @param fileName the name of the file where the data will be put
     * @throws Exception in case it cannot write
     */
    public JsonFileEntityListStoreStrategyImpl(Class<T> entityClass, String fileName) throws Exception {
        this.entityClass = entityClass;
        this.fileName = fileName;
        this.objectMapper = new ObjectMapper();
        initStore();
    }

    private void initStore() throws Exception {
        new File(STORE_FOLDER).mkdir();
        file = new File(STORE_FOLDER + File.separator + fileName);
        if (!file.exists()) {
            file.createNewFile();
            storeEntities(new ArrayList<>());
        }
    }

    /**
     * gets the list of entities stored in the json file
     *
     * @return a list of entities
     * @throws IOException
     */
    @Override
    public List<T> getEntities() throws IOException {
        JavaType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, entityClass);
        return objectMapper.readValue(file, collectionType);
    }

    /**
     * stores all the entities in the json file
     *
     * @param entity the list of entities to be stored
     *
     * @throws IOException
     */
    @Override
    public void storeEntities(List<T> entity) throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, entity);
    }

    /**
     * clears the database
     *
     * @throws Exception
     */
    @Override
    public void clear() throws Exception {
        storeEntities(new ArrayList<>());
        initStore();
    }

}
