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
    double balance;
    boolean admin; // How do we want to set?

    public User(String email, String password, LocalDateTime drawTime) {
        this.email = email;
        this.password = password;
        this.drawTime = drawTime;
    }

    public String getEmail() {
        return email;
    }

    public void addMoney(double amount) { // Temp, can replace
        this.balance += amount;
    }

    public void subtractMoney(double amount) {
        this.balance -= amount;
    }

    public LocalDateTime getDrawTime() { // Method for printing out better
        return drawTime;
    }

    public void setTime(LocalDateTime newTime) {
        this.drawTime = newTime;
    }

}
