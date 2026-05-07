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
    String username;
    String password;
    LocalDateTime drawTime;

    public User(String email, String username, String password, LocalDateTime drawTime) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.drawTime = drawTime;
    }

    public void setTime(LocalDateTime newTime) {
        this.drawTime = newTime;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getDrawTime() {
        return drawTime;
    }
}
