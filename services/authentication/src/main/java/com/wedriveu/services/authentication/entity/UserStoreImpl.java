package com.wedriveu.services.authentication.entity;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.services.shared.entity.User;
import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.shared.utilities.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michele on 12/07/2017.
 */
public class UserStoreImpl implements UserStore {

    private static final String STORE_FOLDER = "store";

    private File file;

    public UserStoreImpl() throws IOException {
        new File(STORE_FOLDER).mkdir();
        file = new File(STORE_FOLDER + File.separator + Constants.USERS_DATABASE_FILENAME);
        file.createNewFile();
    }

    @Override
    public void mapEntityToJson() {

        User user = createDummyObject("Michele","PASSWORD1");
        User user2 = createDummyObject("Stefano","PASSWORD2");
        User user3 = createDummyObject("Nicola","PASSWORD3");
        User user4 = createDummyObject("Marco","PASSWORD4");

        ArrayList<User> userListToJSon = new ArrayList<User>();
        userListToJSon.add(user);
        userListToJSon.add(user2);
        userListToJSon.add(user3);
        userListToJSon.add(user4);

        writeJSonUsersFile(userListToJSon);
    }

    @Override
    public User getUser(String username) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            List<User> users =
                    mapper.readValue(file, new TypeReference<List<User>>(){});
            return getRequestedUuser(users, username);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private User createDummyObject(String username, String password) {
        return new User(username, password);
    }

    private void writeJSonUsersFile(ArrayList<User> userListToJSon){
        try {
            ObjectMapper mapper = new ObjectMapper();

            mapper.writeValue(file, userListToJSon);
            String jsonInString = mapper.writeValueAsString(userListToJSon);
            Log.log(jsonInString);
            jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(userListToJSon);
            Log.log(jsonInString);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private User getRequestedUuser(List<User> users, String username){
        for (User user : users) {
            if(user.getUsername().equals(username)) {
                Log.log("Login success! " +
                        "Username: " +
                        user.getUsername() +
                        ", Password: " +
                        user.getPassword());
                return user;
            }
        }
        Log.log("Login failed, retry!");
        return null;
    }

}
