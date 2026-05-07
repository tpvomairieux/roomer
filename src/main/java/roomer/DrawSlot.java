/**
 * Represents a draw time posted for exchange by a user.
 *
 * @author Evan Tran, Phu Vo, Ronnie Ho
 */
package roomer;

import java.time.LocalDateTime;

public class DrawSlot {

    private final String id;
    private final String ownerEmail;
    private final LocalDateTime drawTime;

    public DrawSlot(String id, String ownerEmail, LocalDateTime drawTime) {
        this.id = id;
        this.ownerEmail = ownerEmail;
        this.drawTime = drawTime;
    }

    public String getId() {
        return id;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public LocalDateTime getDrawTime() {
        return drawTime;
    }

    @Override
    public String toString() {
        return "Slot[" + id + "] | Owner: " + ownerEmail + " | Draw Time: " + drawTime;
    }
}