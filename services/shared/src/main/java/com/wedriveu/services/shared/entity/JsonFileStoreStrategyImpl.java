package com.wedriveu.services.shared.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.services.shared.utilities.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by nicolalasagni on 31/07/2017.
 */
public class JsonFileStoreStrategyImpl<T> implements EntityStoreStrategy<T> {

    private static final String TAG = JsonFileStoreStrategyImpl.class.getSimpleName();
    private static final String CREATE_FILE_ERROR = "Error while creating file";
    private static final String STORE_ERROR = "Error while storing entity";
    private static final String GET_ERROR = "Error while getting entity";

    private File file;
    private ObjectMapper objectMapper;

    public JsonFileStoreStrategyImpl(String fileName) {
        this.file = createFile(fileName);
        this.objectMapper = new ObjectMapper();
    }

    private File createFile(String fileName) {
        File file = new File(fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            Log.error(TAG, CREATE_FILE_ERROR, e);
        }
        return file;
    }

    @Override
    public T get() {
        try {
            return objectMapper.readValue(file, new TypeReference<T>(){});
        } catch (IOException e) {
            Log.error(TAG, GET_ERROR, e);
        }
        return null;
    }

    @Override
    public boolean store(T entity) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, entity);
            return true;
        } catch (IOException e) {
            Log.error(TAG, STORE_ERROR, e);
        }
        return false;
    }

}
