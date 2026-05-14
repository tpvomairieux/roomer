/**
 * Handles posting, trading, and purchasing draw times.
 * Uses ListingTree to keep listings sorted by draw time.
 *
 * @author Evan Tran, Phu Vo, Ronnie Ho
 */
package roomer;

import roomer.interfaces.Listing;
import roomer.interfaces.User;
import roomer.interfaces.Users;

import java.time.LocalDateTime;
import java.util.List;

public class DrawExchange {

    // Tree-based structure sorting listings by draw time
    private final ListingTree listings = new ListingTree();

    /**
     * Posts a user's draw time to the exchange with an optional price.
     * Returns null if the user already has an active listing.
     */
    public Listing post(User user, double price) {
        if (listings.containsEmail(user.getEmail())) return null;
        Listing listing = new Listing(user.getEmail(), user.getDrawTime(), price);
        listings.add(listing);
        return listing;
    }

    /**
     * Returns all active listings sorted by draw time (earliest first).
     */
    public List<Listing> getAllListings() {
        return listings.allSorted();
    }

    /**
     * Returns true if both users have active listings posted.
     */
    public boolean canTrade(String emailA, String emailB) {
        return listings.containsEmail(emailA) && listings.containsEmail(emailB);
    }

    /**
     * Swaps draw times between two users and removes their listings.
     * Returns true if the trade succeeded.
     */
    public boolean executeTrade(String emailA, String emailB, Users users) {
        if (!canTrade(emailA, emailB)) return false;

        User userA = users.get(emailA);
        User userB = users.get(emailB);
        if (userA == null || userB == null) return false;

        // swap their times using a temp variable
        LocalDateTime temp = userA.getDrawTime();
        userA.setTime(userB.getDrawTime());
        userB.setTime(temp);

        // Remove both listings
        listings.remove(emailA);
        listings.remove(emailB);
        return true;
    }

    /**
     * Executes a marketplace purchase: buyer pays seller's price, draw times swap.
     * Returns true if the purchase succeeded.
     */
    public boolean purchase(String buyerEmail, String sellerEmail, Users users) {
        Listing sellerListing = listings.find(sellerEmail);
        if (sellerListing == null) return false;

        User buyer = users.get(buyerEmail);
        User seller = users.get(sellerEmail);
        if (buyer == null || seller == null) return false;
        if (buyer.getBalance() < sellerListing.getPrice()) return false;

        // Transfer money and swap draw time
        buyer.subtractMoney(sellerListing.getPrice());
        seller.addMoney(sellerListing.getPrice());
        buyer.setTime(sellerListing.getDrawTime());

        listings.remove(sellerEmail);
        return true;
    }
}