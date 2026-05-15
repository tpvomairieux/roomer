/**
 * Represents a draw time posted for exchange by a user.
 *
 * @author Evan Tran, Phu Vo, Ronnie Ho
 */
package roomer.interfaces;

import java.time.LocalDateTime;

public class Listing {

    private final String ownerEmail;
    private final LocalDateTime drawTime;
    private final double price;

    public Listing(String ownerEmail, LocalDateTime drawTime, double price) {
        this.ownerEmail = ownerEmail;
        this.drawTime = drawTime;
        this.price = price;
    }

    public String getEmail() {
        return ownerEmail;
    }

    public LocalDateTime getDrawTime() {
        return drawTime;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Owner: " + ownerEmail + " | Draw Time: " + drawTime + " | Price: " + price;
    }
}