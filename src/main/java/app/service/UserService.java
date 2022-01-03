package app.service;

import app.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserService {
    private List<User> userData;

    public UserService() {
        userData = new ArrayList<>();
        userData.add(new User("novalis", "supersafe"));
        userData.add(new User("bot", "coolpw"));
        userData.add(new User("sarrah", "luna"));
    }

    // GET /user/:id
    /*public User getUser(Integer ID) {
        User foundUser = userData.stream()
                .filter(user -> Objects.equals(ID, user.getId()))
                .findAny()
                .orElse(null);

        return foundUser;
    }*/

    // GET /user
    public List<User> getUser() {
        return userData;
    }

    // POST /user
    public void addUser(User user) {
        userData.add(user);
    }
}
