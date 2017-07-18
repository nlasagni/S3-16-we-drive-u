package entity;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
/**
 * Created by Michele on 12/07/2017.
 */
public class UserStoreImpl implements UserStore {

    //FIXME Fix the path where we should saving the database
    private final static String USERS_DATABASE_PATH = "D:\\users.json";
  //  private utilities.Util utils = new utilities.Util();
  //  private String USERS_DATABASE_PATH = "./data/users.json";

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
         //   User[] users= mapper.readValue(new File(getClass().getResource(USERS_DATABASE_PATH).getPath()), User[].class);
            User[] users= mapper.readValue(new File(USERS_DATABASE_PATH), User[].class);
            return checkUsersList(users, username);
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

         //   mapper.writeValue(new File(getClass().getResource(USERS_DATABASE_PATH).getPath()), userListToJSon);
            mapper.writeValue(new File(USERS_DATABASE_PATH), userListToJSon);
            String jsonInString = mapper.writeValueAsString(userListToJSon);
            log(jsonInString);
            jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(userListToJSon);
            log(jsonInString);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private User checkUsersList(User[] users, String username){
        for (User user : users) {
            if(user.getUsername().equals(username)) {
                log("Login success! " + "Username: " + user.getUsername() + ", Password: " + user.getPassword());
                return user;
            }
        }
        log("Login failed, retry!");
        return null;
    }

    private void log(String toLog){
        System.out.println(toLog);
    }

}
