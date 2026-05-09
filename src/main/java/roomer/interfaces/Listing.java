/**
 * Represents a draw time posted for exchange by a user.
 *
 * @author Evan Tran, Phu Vo, Ronnie Ho
 */
package roomer.interfaces;

import java.time.LocalDateTime;
import java.util.Objects;

public class Listing {

    private final String id;
    private final String ownerEmail;
    private final LocalDateTime drawTime;
    private Price priceInfo;

    public Listing(String id, String ownerEmail, LocalDateTime drawTime, Price priceInfo) {
        this.id = id;
        this.ownerEmail = ownerEmail;
        this.drawTime = drawTime;
        this.priceInfo = priceInfo;
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

    // ── Object overrides ─────────────────────────────────────────────────────

    /** Two listings are equal iff their ids match. */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Listing other))
            return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}