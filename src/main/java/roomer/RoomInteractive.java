/**
 * User interface (currently just in the terminal) for Roomer. 
 * This class creates a command-line menu where users can view all rooms,
 * filter them, and search for specific rooms.
 * 
 * Room data was loaded from the master csv file (see RoomDataLoader.java).
 * @author Evan Tran, Phu Vo, Ronnie Ho
 *
 */

package roomer;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import roomer.interfaces.Building;
import roomer.interfaces.Room;
import roomer.interfaces.Rooms;

public class RoomInteractive {

    /**
     * Main method that runs the Roomer terminal interface.
     * @param args
     */
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        Rooms rooms;

        try {

            rooms = RoomDataLoader.load("master.csv");

        } catch (IOException e) { // If the CSV file cannot be found or read, stop the program.

            System.out.println("Couldn't load the data.");
            e.printStackTrace();
            scanner.close();
            return;
        }

        System.out.println("Roomer Demo.");

        boolean active = true; // Keeps the menu running until the user chooses to exit the program.

        while (active) {
            System.out.println("\nChoose from the following options! Please enter the number of your choice.");
            System.out.println("1. View all rooms");
            System.out.println("2. Search for a room");
            System.out.println("3. End");

            String choice = scanner.nextLine();

            switch (choice) {

                case "1": // Lets the user either print every room or filter rooms.
                    viewRooms(scanner, rooms);
                    break;

                case "2": // Lets the user search for one specific room
                    searchForRoom(scanner, rooms);
                    break;

                case "3": // Ends the menu loop.
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
     * Displays the room-viewing menu.
     * 
     * @param scanner
     * @param rooms
     */
    private static void viewRooms(Scanner scanner, Rooms rooms) {

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

    /**
     * Lets the user filter rooms by building, occupancy, AC status, and square footage.
     * 
     * Each filter is optional. If the user presses enter without typing anything, the filter is skipped.
     * @param scanner (Object) used to read user input
     * @param rooms (Object) Collection of rooms being filtered. 
     */
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

        // Size Filters
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
     * Normalizes a room-name search so user input matches the stored HashMap key.
     * 
     * Rooms are stored with spaces around the dash, such as "Walker - 719".
     * @param input room name typed by the user
     * @return normalized room name in the format "Building - RoomNumber"
     */
    private static String normalizeRoomName(String input) {

    input = input.trim();

    if (input.contains(" - ")) {
        return input;
    }

    // If the user typed something like "Walker-729", convert it.
    if (input.contains("-")) {

        String[] parts = input.split("-", 2);

        if (parts.length == 2) {
            return parts[0].trim() + " - " + parts[1].trim();
        }
    }

    return input;
    }

    /**
     * Searches for one room by name.
     * 
     * The user's input is normalized before searching so both "Blaisdell-2"
     * and "Blaisdell - 2" end up working for the same stored room. 
     * 
     * @param scanner (Object) used to read user input
     * @param rooms (Object) Collection of rooms being filtered. 
     */
    private static void searchForRoom(Scanner scanner, Rooms rooms) {

        System.out.print("Enter room name as [Dorm Building]-[Room Number]: ");

        String name = scanner.nextLine();

        name = normalizeRoomName(name); // Convert user input into the same format used by Room.getRoomName().

        Room room = rooms.get(name);

        if (room != null) {

            System.out.println(room);
            System.out.println(room.completeDrawHistory());

        } else {

            System.out.println("Room not found.");
        }
    }
}
