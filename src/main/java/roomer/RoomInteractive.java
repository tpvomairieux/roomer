/**
 * User interface (currently just in the terminal) for Roomer. 
 *
 * @author Evan Tran, Phu Vo, Ronnie Ho
 *
 */

package roomer;


import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class RoomInteractive {

    public static void main(String[] args) {
        
        Scanner scanner = new Scanner(System.in);

        Rooms rooms;

        try {

            rooms = RoomDataLoader.load("CS62 Final Project Data.xlsx");

        } catch (IOException e) {

            System.out.println("Couldn't load the data.");
            e.printStackTrace();
            scanner.close();
            return;
        }

        System.out.println("Roomer Demo.");

        boolean active = true;


        while (active) {

            System.out.println("\nChoose from the following options! Please enter the number of your choice.");
            System.out.println("1. View all listings");
            System.out.println("2. Search for a listing");
            System.out.println("3. Add a listing"); // We would need to figure out how to write to the excel file. Need to watch more YT vids. For now just store new listings in memory.
            System.out.println("4. Remove your listing");
            System.out.println("5. End");

            String choice = scanner.nextLine();

            switch (choice) {

                case "1":
                    viewListings(scanner, rooms);
                    break;
                
                case "2":
                    searchForListing(scanner, rooms);
                    break;

                case "3":
                    addListing(scanner, rooms);
                    break;

                case "4":
                    removeListing(scanner, rooms);
                    break;

                case "5":
                    active = false;
                    break;

                default:
                    System.out.println("Invalid input! Must be an eligible number.");

            }
        }

        System.out.println("See you again!");
        scanner.close();
    }
    
    /**
     * 
     * @param scanner
     * @param rooms
     */
    private static void viewListings(Scanner scanner, Rooms rooms) {

        System.out.println("\nView options:");
        System.out.println("1. Show all available rooms.");
        System.out.println("2. Filter rooms.");

        String choice = scanner.nextLine();

        if (choice.equals("1")) {

            List<Room> all = rooms.getAllRooms();
            rooms.printRooms(all);

        } else if (choice.equals("2")) {

            filter(scanner, rooms);
        }
    }

    private static void filter(Scanner scanner, Rooms rooms) {

        Building building = null;
        Integer occupancy = null;
        Boolean hasAC = null;
        Integer minSize = null;
        Integer maxSize = null;

        System.out.println("\nEnter filters for each category. Press the enter key to skip a category:");

        // Building Filter
        System.out.print("Building (e.g., Smiley): ");

        String b = scanner.nextLine();

        if (!b.isBlank()) {
            try {

                building = Building.valueOf(b);
            } catch (Exception e) {

                System.out.println("Invalid building. Skipping to next filter.");
            }
        }

        // Occupancy Filter
        System.out.print("Occupancy (1, 2, 3): ");

        String inhabitants = scanner.nextLine();

        if (!inhabitants.isBlank()) {
            occupancy = Integer.parseInt(inhabitants);
        }

        // AC Filter
        System.out.print("Looking for air conditioning? (yes/no): ");
        String acInput = scanner.nextLine();

        if (acInput.equalsIgnoreCase("yes")) {

            hasAC = true;
        } else if (acInput.equalsIgnoreCase("no")) {

            hasAC = false;
        }

        // Size Filter
        System.out.print("Min sqft: ");
        String min = scanner.nextLine();

        if (!min.isBlank()) {

            minSize = Integer.parseInt(min);
        }

        System.out.print("Max sqft: ");
        String max = scanner.nextLine();

        if (!max.isBlank()) {

            maxSize = Integer.parseInt(max);
        }

        List<Room> filtered = rooms.filter(building, occupancy, hasAC, minSize, maxSize);

        System.out.println("\nFiltered Results:");
        rooms.printRooms(filtered);
    }

    /**
     * 
     * @param scanner
     * @param rooms
     */
    private static void searchForListing(Scanner scanner, Rooms rooms) {

        System.out.print("Enter room name as [Dorm Building]-[Room Number]: ");

        String name = scanner.nextLine();

        Room room = rooms.get(name);

        if (room != null) {

            System.out.println(room);
            System.out.println(room.completeDrawHistory());
        } else {

            System.out.println("Room not found.");
        }
    }

    /**
     * 
     * @param scanner
     * @param rooms
     */
    private static void addListing(Scanner scanner, Rooms rooms) {

        System.out.println("\nAdd New Listing-- please include room details!");

        System.out.print("Enter raw room name (e.g., Blaisdell - 2): ");
        String rawName = scanner.nextLine();

        System.out.print("Square footage: ");
        int sqft = Integer.parseInt(scanner.nextLine());

        System.out.print("Occupancy: ");
        int occupancy = Integer.parseInt(scanner.nextLine());

        System.out.print("Has AC? (yes/no): ");
        boolean hasAC = scanner.nextLine().equalsIgnoreCase("yes");

        // Students would never know this lol
        Room newRoom = new Room(rawName,sqft,null,null, null, occupancy, hasAC);

        rooms.add(newRoom);

        System.out.println("Listing added successfully!");
    }

    /**
     * 
     * @param scanner
     * @param rooms
     */
    private static void removeListing(Scanner scanner, Rooms rooms) {

        System.out.print("Enter room name to remove: ");
        String name = scanner.nextLine();

        rooms.remove(name);
        System.out.println("Listing successfully removed from roomer.");
    }

}
