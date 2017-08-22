package com.wedriveu.services.authentication.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.services.shared.model.User;
import com.wedriveu.shared.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Michele on 12/07/2017.
 */
public class UserStoreImpl implements UserStore {

    private static final String STORE_FOLDER = "store";
    private static final String USERS_DATABASE_FILENAME = "users.json";

    private File file;

    public UserStoreImpl() throws IOException {
        new File(STORE_FOLDER).mkdir();
        file = new File(STORE_FOLDER + File.separator + USERS_DATABASE_FILENAME);
        file.createNewFile();
    }

    @Override
    public void mapEntityToJson() {
        List<User> userListToJSon = Arrays.asList(User.USERS);
        writeJSonUsersFile(userListToJSon);
    }

    @Override
    public User getUser(String username) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            List<User> users =
                    mapper.readValue(file, new TypeReference<List<User>>() {
                    });
            return getRequestedUuser(users, username);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
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

    private User getRequestedUuser(List<User> users, String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

}
