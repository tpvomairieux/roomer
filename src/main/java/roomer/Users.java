/**
 * Object that represents a list of all users
 *
 * @author Evan Tran, Phu Vo, Ronnie Ho
 *
 */
package roomer;

import java.util.HashMap;

public class Users {

    private HashMap<String, User> users = new HashMap<>();

    public Users() {
        this.users = new HashMap<>();
    }

    public void add(User user) {
        users.put(user.email, user);
    }

    public User get(String email) {
        return users.get(email);
    }

    public void remove(String email) {
        users.remove(email);
    }

}
