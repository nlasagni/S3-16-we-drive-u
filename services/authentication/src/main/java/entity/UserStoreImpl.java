package entity;

import com.fasterxml.jackson.core.JsonGenerationException;
        import com.fasterxml.jackson.databind.JsonMappingException;
        import com.fasterxml.jackson.databind.ObjectMapper;

        import java.io.File;
        import java.io.IOException;
        import java.util.ArrayList;
/**
 * Created by Michele on 12/07/2017.
 */
public class UserStoreImpl implements UserStore {

    @Override
    public void mapUsersToJSon() {
        ObjectMapper mapper = new ObjectMapper();

        User user = createDummyObject("Michele","PASSWORD1");
        User user2 = createDummyObject("Stefano","PASSWORD2");
        User user3 = createDummyObject("Nicola","PASSWORD3");
        User user4 = createDummyObject("Marco","PASSWORD4");

        ArrayList<User> userListToJSon = new ArrayList<User>();
        userListToJSon.add(user);
        userListToJSon.add(user2);
        userListToJSon.add(user3);
        userListToJSon.add(user4);


        try {
            // Convert object to JSON string and save into a file directly
            mapper.writeValue(new File("D:\\users.json"), userListToJSon);

            // Convert object to JSON string
            String jsonInString = mapper.writeValueAsString(userListToJSon);
            System.out.println(jsonInString);

            // Convert object to JSON string and pretty print
            jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(userListToJSon);
            System.out.println(jsonInString);

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User getUser(String username) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            // Convert JSON string from file to Object
            User[] userListFromJSon= new User[10];
            userListFromJSon = mapper.readValue(new File("D:\\users.json"), User[].class);
            for (int i = 0; i < userListFromJSon.length; i++) {
                User actualUser = userListFromJSon[i];
                if(actualUser.getUsername().equals(username)) {
                    System.out.println("Login success! " + "Username: " +actualUser.getUsername() + ", Password: " + actualUser.getPassword());
                    return actualUser;
                }
            }
            System.out.println("Login failed, retry!");
            return null;

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

        User user = new User(username, password);
        return user;

    }

}
