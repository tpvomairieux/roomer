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
    private Price priceInfo;

    public Listing(String ownerEmail, LocalDateTime drawTime, Price priceInfo) {
        this.ownerEmail = ownerEmail;
        this.drawTime = drawTime;
        this.priceInfo = priceInfo;
    }

    public String getEmail() {
        return ownerEmail;
    }

    public LocalDateTime getDrawTime() {
        return drawTime;
    }

    @Override
    public String toString() {
        return "Owner: " + ownerEmail + " | Draw Time: " + drawTime;
    }
}