/**
 * Object that represents a user
 *
 * @author Evan Tran, Phu Vo, Ronnie Ho
 *
 */
package roomer.interfaces;

import java.time.LocalDateTime;

public class User {

    String email;
    String password;
    LocalDateTime drawTime;
    LocalDateTime swapTime;

    public User(String email, String password, LocalDateTime drawTime) {
        this.email = email;
        this.password = password;
        this.drawTime = drawTime;
    }

    // TODO

}
