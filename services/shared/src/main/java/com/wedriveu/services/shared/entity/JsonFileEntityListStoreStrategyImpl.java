package com.wedriveu.services.shared.entity;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nicola Lasagni on 31/07/2017.
 */
public class JsonFileEntityListStoreStrategyImpl<T> implements EntityListStoreStrategy<T> {

    private static final String STORE_FOLDER = "store";

    private Class<T> entityClass;
    private File file;
    private ObjectMapper objectMapper;
    private String fileName;

    public JsonFileEntityListStoreStrategyImpl(Class<T> entityClass, String fileName) throws Exception {
        this.entityClass = entityClass;
        this.fileName = fileName;
        this.objectMapper = new ObjectMapper();
        initStore();
    }

    private void initStore() throws Exception {
        new File(STORE_FOLDER).mkdir();
        file = new File(STORE_FOLDER + File.separator + fileName);
        file.createNewFile();
        storeEntities(new ArrayList<>());
    }

    @Override
    public List<T> getEntities() throws IOException {
        JavaType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, entityClass);
        return objectMapper.readValue(file, collectionType);
    }

    @Override
    public void storeEntities(List<T> entity) throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, entity);
    }

    @Override
    public void clear() throws Exception  {
        file.delete();
        initStore();
    }

}
