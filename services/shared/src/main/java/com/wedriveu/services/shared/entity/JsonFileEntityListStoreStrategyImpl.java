package com.wedriveu.services.shared.entity;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Nicola Lasagni on 31/07/2017.
 */
public class JsonFileEntityListStoreStrategyImpl<T> implements EntityListStoreStrategy<T> {

    private Class<T> entityClass;
    private File file;
    private ObjectMapper objectMapper;

    public JsonFileEntityListStoreStrategyImpl(Class<T> entityClass, String fileName) throws IOException {
        this.entityClass = entityClass;
        this.file = new File(fileName);
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<T> getEntities() throws IOException {
        JavaType stringCollection = objectMapper.getTypeFactory().constructCollectionType(List.class, entityClass);
        return objectMapper.readValue(file, stringCollection);
    }

    @Override
    public void storeEntities(List<T> entity) throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, entity);
    }

    @Override
    public void clear() throws IOException  {
        file.delete();
    }

}
