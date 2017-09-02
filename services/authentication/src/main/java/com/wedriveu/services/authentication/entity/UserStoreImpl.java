package com.wedriveu.services.authentication.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.services.shared.model.User;
import com.wedriveu.shared.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * The effective UserStore implementation.
 *
 * @author Michele Donati on 12/07/2017.
 * @author Nicola Lasagni
 */
public class UserStoreImpl implements UserStore {

    private static final String STORE_FOLDER = "store";
    private static final String USERS_DATABASE_FILENAME = "users.json";

    private File file;

    /**
     * Instantiates a new UserStore.
     *
     * @throws IOException if it wasn't possible to create the store data container.
     */
    public UserStoreImpl() throws IOException {
        new File(STORE_FOLDER).mkdir();
        file = new File(STORE_FOLDER + File.separator + USERS_DATABASE_FILENAME);
        file.createNewFile();
    }

    @Override
    public boolean addUser(User user) {
        List<User> users = getUsers();
        users.add(user);
        writeJSonUsersFile(users);
        return true;
    }

    @Override
    public void clear() {
        writeJSonUsersFile(new ArrayList<>());
    }

    private List<User> getUsers() {
        List<User> users = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            users = mapper.readValue(file, new TypeReference<List<User>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    private void writeJSonUsersFile(List<User> userListToJSon) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(file, userListToJSon);
            String jsonInString = mapper.writeValueAsString(userListToJSon);
            Log.info(jsonInString);
            jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(userListToJSon);
            Log.info(jsonInString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private User getRequestedUser(List<User> users, String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

}
