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

public class DrawExchangeInteractive {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a",
            Locale.ENGLISH);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Users users = new Users();
        DrawExchange exchange = new DrawExchange();

        seedUsers(users);

        System.out.println("Roomer - Draw Time Exchange");

        boolean active = true;

        while (active) {
            System.out.println("\nChoose an option:"); // Edit draws to include pricing
            System.out.println("1. Create a listing");
            System.out.println("2. View all posted listings");
            System.out.println("3. Check if a swap is possible");
            System.out.println("4. Exchange times");
            System.out.println("5. Exit");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    postSlot(scanner, users, exchange);
                    break;
                case "2":
                    viewSlots(exchange);
                    break;
                case "3":
                    checkTrade(scanner, exchange);
                    break;
                case "4":
                    executeTrade(scanner, users, exchange);
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

    // Can implement login system if we build website GUI,
    private static void postSlot(Scanner scanner, Users users, DrawExchange exchange) {
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        User user = users.get(email);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.print("How would you like to list your time? ");
        System.out.println("1. Auction");
        System.out.println("2. Buy-It-Now");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1":
                System.out.print("What should the minimum bid be? ");
                double minBid = Double.parseDouble(scanner.nextLine());
                Price auctionInfo = new Price(true, minBid);

            case "2":
                System.out.print("What should the BIN price be? ");
                double binPrice = Double.parseDouble(scanner.nextLine());
                Price binInfoPrice = new Price(false, binPrice);
        }

        Listing slot = exchange.postSlot(user); // What do

        if (slot == null) { // Surely != null?
            System.out.println("Could not post. You may already have an active posting.");
        } else {
            System.out.println("Posted: " + slot);
        }
    }

    private static void viewSlots(DrawExchange exchange) {
        List<Listing> slots = exchange.getAllSlots();
        if (slots.isEmpty()) {
            System.out.println("No draw times posted for exchange.");
            return;
        }
        System.out.println("\nAll posted draw times:");
        for (Listing slot : slots) {
            System.out.println("  " + slot);
        }
    }

    // Method works, ok user design. Buyers should not have to create listings to
    // exchange
    private static void checkTrade(Scanner scanner, DrawExchange exchange) {
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