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
            System.out.println("4. Exit");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    postSlot(scanner, users, tree);
                    break;
                case "2":
                    viewSlots(tree);
                    break;
                case "3":
                    purchaseListing(scanner, users, tree);
                    break;
                case "4":
                    active = false;
                    break;
                default:
                    System.out.println("Invalid choice. Enter a number 1-4.");
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
    // and passing it to Listing Tree

    private static void postSlot(Scanner scanner, Users users, ListingTree tree) { // Testing please
        User user = new User();
        double binPrice;

        while (true) { // Checks if user is in system and therefore eligible to make a listing
            System.out.println("Enter your Pomona email: ");
            String email = scanner.nextLine();

            if (email.endsWith("@mymail.pomona.edu") || email.endsWith("@pomona.edu")) {
                user = users.get(email);
                if (user == null) {
                    System.out.println("Warning: User not found.");
                    return;
                } else {
                    break;
                }
            } else {
                System.out.println("Warning: not a valid Pomona email.");
            }
        }

        while (true) { // Checks if user's listing is valid
            System.out.println("Would you like to list your time?");
            System.out.println("1. Confirm");
            System.out.println("2. Cancel");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
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
                    break;

                case "2":
                    System.out.println("Cancelling listing.");
                    return;

                default:
                    System.out.println("Invalid selection. Please try again!");
                    continue;
            }
            break;
        }

        try {
            tree.add(new Listing(user.getEmail(), user.getDrawTime(), binPrice));
        } catch (NullPointerException e) {
            System.out.println("Could not post: " + e);

        } catch (IllegalArgumentException e) {
            System.out.println("Could not post: " + e);
        }

        System.out.println("New listing posted");
    }

    private static void removeSlot(Scanner scanner, Users users, ListingTree tree) {
        User user = new User();
        while (true) { // Checks if user is in system and therefore eligible to make a listing
            System.out.println("Enter your Pomona email: ");
            String email = scanner.nextLine();

            if (email.endsWith("@mymail.pomona.edu") || email.endsWith("@pomona.edu")) {
                user = users.get(email);
                if (user == null) {
                    System.out.println("Warning: User not found.");
                    return;
                } else {
                    break;
                }
            } else {
                System.out.println("Warning: not a valid Pomona email.");
            }
        }

        while (true) { // Checks if user wants to remove listing
            System.out.println("Would you like to remove your time?");
            System.out.println("1. Confirm");
            System.out.println("2. Cancel");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    tree.remove(user.getEmail());
                    return;

                case "2":
                    System.out.println("Cancelling removal.");
                    return;

                default:
                    System.out.println("Invalid selection. Please try again!");
            }
        }
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

    private static void purchaseListing(Scanner scanner, Users users, ListingTree tree) { // Ability to cancel?
        User buyer = new User();
        User seller = new User();
        Listing sellerListing;
        double binPrice;

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

        while (true) {
            System.out.print("Enter seller's email: ");
            String email = scanner.nextLine();

            seller = users.get(email);

            if (seller == null) {
                System.out.println("User not found.");
            } else {
                if (!tree.containsEmail(email)) {
                    System.out.println("Seller's listing not found.");
                } else {
                    sellerListing = tree.find(email);
                    binPrice = sellerListing.getPrice();
                    break;
                }
            }
        }

        while (true) {
            System.out.println("Purchase " + sellerListing.getEmail() + "'s time for " + binPrice + "?");
            System.out.println("1. Yes");
            System.out.println("2. No");
            int buy = Integer.parseInt(scanner.nextLine());
            if (!checkBalance(buyer, binPrice)) {
                System.out.println(
                        "Warning: User does not have enough money in account balance. Please add more and try again");
                return;
            }
            if (buy == 1) {
                executePurchase(buyer, seller, binPrice, tree);
                System.out.println("Purchase successfully submitted!");
                break;
            }
            return;
        }
    }

    private static boolean checkBalance(User user, double bid) {
        return (user.getBalance() > bid);
    }

    private static void executePurchase(User buyer, User seller, double bid, ListingTree tree) {
        LocalDateTime temp = buyer.getDrawTime();
        buyer.setTime(seller.getDrawTime());
        seller.setTime(temp);
        tree.remove(seller.getEmail());
        seller.addMoney(bid);
        buyer.subtractMoney(bid);
    }
}