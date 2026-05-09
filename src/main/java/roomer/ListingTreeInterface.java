/**
 * Terminal interface for the draw-time exchange feature.
 *
 * @author Evan Tran, Phu Vo, Ronnie Ho
 */
package roomer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import roomer.interfaces.Listing;
import roomer.interfaces.Price;
import roomer.interfaces.User;
import roomer.interfaces.Users;

public class ListingTreeInterface {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a",
            Locale.ENGLISH);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Users users = new Users();
        ListingTree tree = new ListingTree();

        seedUsers(users);

        System.out.println("Roomer - Draw Time Exchange");

        boolean active = true;

        while (active) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Create a listing");
            System.out.println("2. View all posted listings");
            System.out.println("3. Purchase a listing");
            System.out.println("4. Exchange times");
            System.out.println("5. Exit");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    postSlot(scanner, users, tree);
                    break;
                case "2":
                    viewSlots(tree);
                    break;
                case "3":
                    purchase(scanner, users, tree);
                    break;
                case "4":
                    executeTrade(scanner, users, tree);
                    break;
                case "5":
                    active = false;
                    break;
                default:
                    System.out.println("Invalid choice. Enter a number 1-5.");
            }
        }

        System.out.println("Goodbye!");
        scanner.close();
    }

    /** Seeds demo users so the feature can be tested immediately. */
    private static void seedUsers(Users users) {
        users.add(new User("alicia.park@pomona.edu", "pass",
                LocalDateTime.parse("Apr 8, 2025 5:03 PM", FORMATTER)));
        users.add(new User("bryson.young@pomona.edu", "pass",
                LocalDateTime.parse("Apr 8, 2025 6:42 PM", FORMATTER)));
        users.add(new User("carol.rivera@pomona.edu", "pass",
                LocalDateTime.parse("Apr 9, 2025 7:06 PM", FORMATTER)));
    }

    // Can implement login system if we build website GUI

    // This method serves as the frontend for getting user information for listing,
    // and passing it to drawexchange.java

    private static void postSlot(Scanner scanner, Users users, ListingTree tree) { // Testing please
        User user = new User();

        while (true) { // Checks if user is in system and therefore eligible to make a listing
            System.out.print("Enter your email: ");
            String email = scanner.nextLine();

            user = users.get(email);

            if (user == null) {
                System.out.println("User not found.");
            } else {
                break;
            }
        }

        Price priceInfo = new Price();

        while (true) { // Checks if user's listing is valid
            System.out.println("How would you like to list your time?");
            System.out.println("1. Auction");
            System.out.println("2. Buy-It-Now");
            System.out.println("3. Cancel");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    double minBid;
                    while (true) {
                        System.out.println("What should the minimum bid be?");
                        try {
                            minBid = Double.parseDouble(scanner.nextLine());
                            if (minBid <= 0) {
                                System.out.println("Warning: minimum bid must be greater than 0.");
                                continue;
                            }
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Warning: Please enter a valid number.");
                        }
                    }
                    priceInfo.setAuction(true);
                    priceInfo.setSeller(minBid);
                    break;

                case "2":
                    double binPrice;
                    while (true) {
                        System.out.println("What should the BIN price be?");
                        try {
                            binPrice = Double.parseDouble(scanner.nextLine());
                            if (binPrice <= 0) {
                                System.out.println("Warning: BIN price must be greater than 0.");
                                continue;
                            }
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Warning: Please enter a valid number.");
                        }
                    }
                    priceInfo.setAuction(false);
                    priceInfo.setSeller(binPrice);
                    break;

                case "3":
                    System.out.println("Cancelling listing.");
                    return;

                default:
                    System.out.println("Invalid selection. Please try again!");
                    continue;
            }
            break;
        }

        try {
            tree.add(new Listing(user.getEmail(), user.getDrawTime(), priceInfo));
        } catch (NullPointerException e) {
            System.out.println("Could not post: " + e);

        } catch (IllegalArgumentException e) {
            System.out.println("Could not post: " + e);
        }

        System.out.println("New listing posted");
    }

    // private static void removeSlot() for later

    // This method serves as the frontend for getting the listings
    // Maybe add sorting here?

    private static void viewSlots(ListingTree tree) {
        System.out.println("All Listings: "); // Sorted from earliest draw to latest
        List<Listing> listings = tree.allSorted();
        for (Listing listing : listings) {
            System.out.println(listing);
        }
    }

    // Method works, ok user design. Buyers should not have to create listings to
    // exchange

    private static void purchaseListing(Scanner scanner, Users users, ListingTree tree) { // Ability to cancel?
        User buyer = new User();
        Listing sellerListing;
        Price listingInfo;

        while (true) { // Checks if user is in system and therefore eligible to buy a listing
            System.out.print("Enter your email: ");
            String email = scanner.nextLine();

            buyer = users.get(email);

            if (buyer == null) {
                System.out.println("User not found.");
            } else {
                break;
            }
        }

        while (true) { // A little clunky, taking suggestions
            System.out.print("Enter seller's email: ");
            String email = scanner.nextLine();

            if (!tree.containsEmail(email)) {
                System.out.println("Seller's listing not found.");
            } else {
                sellerListing = tree.find(email);
                listingInfo = sellerListing.getPriceInfo();
                break;
            }
        }

        if (listingInfo.isAuction()) { // Check if user has enough money
            double minBid = listingInfo.getBuyerPrice() + 1;
            while (true) {
                System.out.println(
                        "To be elligible for " + sellerListing.getEmail()
                                + "'s time, you would need to submit a bid of at least "
                                + minBid + ". Please enter your bid:");
                double bid = Double.parseDouble(scanner.nextLine());
                if (bid < minBid) {
                    System.out.println("Warning: bid is not large enough.");
                } else {
                    System.out.println("Bid of " + bid + " successfully submitted!");
                    break;
                }
            }
        } else {
            while (true) {
                System.out.println(
                        "Purchase " + sellerListing.getEmail()
                                + "'s time for " + listingInfo.getSellerPrice() + "?");
                System.out.println("1. Yes");
                System.out.println("2. No");
                int buy = Integer.parseInt(scanner.nextLine());
                if (buy == 1) {
                    System.out.println("Purchase sucessfully submitted!");
                    break;
                }
                return;
            }
        }
    }

    private static void checkTrade(Scanner scanner, DrawExchange exchange) { // TODO modify to work with listing tree
        System.out.print("Enter first user's email: ");
        String emailA = scanner.nextLine();
        System.out.print("Enter second user's email: ");
        String emailB = scanner.nextLine();
        if (exchange.canTrade(emailA, emailB)) {
            Listing slotA = exchange.getSlotByUser(emailA);
            Listing slotB = exchange.getSlotByUser(emailB);
            System.out.println("Trade is valid!");
            System.out.println("  " + emailA + " would receive: " + slotB.getDrawTime());
            System.out.println("  " + emailB + " would receive: " + slotA.getDrawTime());
        } else {
            System.out.println("Trade not possible. One or both users have not posted a draw time.");
        }
    }

    private static void executeTrade(Scanner scanner, Users users, DrawExchange exchange) {
        System.out.print("Enter first user's email: ");
        String emailA = scanner.nextLine();
        System.out.print("Enter second user's email: ");
        String emailB = scanner.nextLine();
        boolean success = exchange.executeTrade(emailA, emailB, users);
        if (success) {
            System.out.println("Trade complete!");
            System.out.println(emailA + "'s new draw time: " + users.get(emailA).getDrawTime());
            System.out.println(emailB + "'s new draw time: " + users.get(emailB).getDrawTime());
        } else {
            System.out.println("Trade failed. Check that both users have posted draw times.");
        }
    }
}