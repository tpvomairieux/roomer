/**
 * Manages all posted draw-time exchange listings.
 * Uses two HashMaps: one maps slot ID to Listing,
 * the other maps user email to their slot ID.
 *
 * @author Evan Tran, Phu Vo, Ronnie Ho
 */
package roomer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import roomer.interfaces.Listing;
import roomer.interfaces.Price;
import roomer.interfaces.User;
import roomer.interfaces.Users;

public class DrawExchange {

    /** Maps a unique slot ID to its Listing. */
    private HashMap<String, Listing> slotById;

    /** Maps a user's email to the ID of their posted slot. One slot per user. */
    private HashMap<String, String> userToSlotId;

    private int nextId = 1;

    public DrawExchange() {
        this.slotById = new HashMap<>();
        this.userToSlotId = new HashMap<>();
    }

    /**
     * Posts a user's draw time for exchange.
     * A user may only have one active posting at a time.
     *
     * @param user the user posting their draw time
     * @return the created Listing, or null if user has no draw time or already
     *         posted
     */
    public Listing postSlot(User user, Price priceInfo) {
        if (user.getDrawTime() == null || userToSlotId.containsKey(user.getEmail())) {
            return null;
        }
        String id = "slot-" + nextId++;
        Listing slot = new Listing(user.getEmail(), user.getDrawTime(), priceInfo);
        slotById.put(id, slot);
        userToSlotId.put(user.getEmail(), id);
        return slot;
    }

    /**
     * Removes a user's posted draw slot.
     *
     * @param email the email of the user removing their slot
     * @return true if removed, false if no slot was found
     */
    public boolean removeSlot(String email) {
        String slotId = userToSlotId.get(email);
        if (slotId == null) {
            return false;
        }
        slotById.remove(slotId);
        userToSlotId.remove(email);
        return true;
    }

    /**
     * Gets a user's posted slot by their email.
     *
     * @param email the user's email
     * @return their Listing, or null if they have no active posting
     */
    public Listing getSlotByUser(String email) {
        String slotId = userToSlotId.get(email);
        if (slotId == null) {
            return null;
        }
        return slotById.get(slotId);
    }

    /**
     * Returns all currently posted draw slots.
     *
     * @return list of all DrawSlots
     */
    public List<Listing> getAllSlots() {
        return new ArrayList<>(slotById.values());
    }

    /**
     * Checks whether two users can directly trade draw times.
     * A trade is valid only if both users have an active posting.
     *
     * @param emailA first user's email
     * @param emailB second user's email
     * @return true if both users have posted a draw slot
     */
    public boolean canTrade(String emailA, String emailB) {
        return userToSlotId.containsKey(emailA) && userToSlotId.containsKey(emailB);
    }

    /**
     * Executes a direct trade by swapping the draw times of two users.
     * Removes both postings from the exchange after the swap.
     *
     * @param emailA first user's email
     * @param emailB second user's email
     * @param users  the Users directory to update the actual User objects
     * @return true if trade succeeded, false if either user has no active posting
     */
    public boolean executeTrade(String emailA, String emailB, Users users) {
        if (!canTrade(emailA, emailB)) {
            return false;
        }
        User userA = users.get(emailA);
        User userB = users.get(emailB);
        if (userA == null || userB == null) {
            return false;
        }
        // Swap the draw times between the two users
        LocalDateTime temp = userA.getDrawTime();
        userA.setTime(userB.getDrawTime());
        userB.setTime(temp);
        removeSlot(emailA);
        removeSlot(emailB);
        return true;
    }

    /**
     * Returns the number of active posted slots.
     *
     * @return number of slots
     */
    public int size() {
        return slotById.size();
    }
}