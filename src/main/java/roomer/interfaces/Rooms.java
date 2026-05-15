/**
 * Object that represents a list of all rooms. We store indviidual room objects into a HashMap with each room's 
 * cleaned name as the key. There are methods to get rooms, search for rooms via filters, and look-up rooms 
 * by draw times. 
 *
 * @author Evan Tran, Phu Vo, Ronnie Ho
 *
 */
package roomer.interfaces;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import roomer.RoomDataLoader;

public class Rooms {

    // Stores all rooms with its clean room name: ex) "Blaisdell - 2."
    private HashMap<String, Room> rooms = new HashMap<>();

    /**
     * Constructs an empty room object. Rooms are then added via loading them through the Master Excel spreadsheet. 
     */
    public Rooms() {

        this.rooms = new HashMap<>();
    }

    /**
     * Adds a room into the rooms collection.
     * 
     * By using a hashmap, we will later by able to quickly look up the room by its name, giving us an average case of constant search time.
     * @param room (Object) the room to add. 
     */
    public void add(Room room) {

        rooms.put(room.getRoomName(), room);
    }

    /**
     * Gets a specific room by its cleaned room name. 
     * @param roomName (String) the cleaned name of the room. 
     * @return (String) the room object associated to the cleaned name, or null, if the room doesn't exist. 
     */
    public Room get(String roomName) {

        return rooms.get(roomName);
    }

    /**
     * Gets the total number of rooms stored in the hashmap.
     * @return (Int) the number of rooms in the data collection. 
     */
    public int size() {

        return rooms.size();
    }

    /**
     * Gets all rooms in the collection as a List. 
     * 
     * When filtering, we're able to loop through every room easily, or print out a collection. 
     * @return (List) a list containing every room object currently stored.
     */
    public List<Room> getAllRooms() {

        return new ArrayList<>(rooms.values());
    }

    /**
     * Filters rooms by building.
     * 
     * @param building (Enum) The building's enum value to filter by. 
     * @return (List) a list of rooms in the given building. 
     */
    public List<Room> filterByBuilding(Building building) {

        List<Room> result = new ArrayList<>();

        for (Room room : rooms.values()) {

            if (room.getBuilding() == building) {

                result.add(room);
            }
        }
        return result;
    }

    /**
     * Filter rooms by occupancy status.
     * @param occupancy (Int) the number of students a room can hold
     * @return (List) a list of rooms with the given occupancy. 
     */
    public List<Room> filterByOccupancy(int occupancy) {

        List<Room> result = new ArrayList<>();

        for (Room room : rooms.values()) {

            if (room.getOccupancy() == occupancy) {

                result.add(room);
            }
        }
        return result;
    }

    /**
     * Filters room by AC status.
     * @param AC (Boolean) A true/false ac status based off the building information of the room.
     * @return (List) a list of rooms matching the requested AC status
     */
    public List<Room> filterByAC(boolean hasAC) {

        List<Room> result = new ArrayList<>();

        for (Room room : rooms.values()) {

            if (room.hasAC() == hasAC) {

                result.add(room);
            }
        }
        return result;
    }

    /**
     * Filters rooms by square footage range.
     * @param min (Int) the minimum square footage allowed
     * @param max (Int) the max square footage allowed.
     * @return (List) a list of rooms within the given square footage range
     */
    public List<Room> filterBySquareFootage(int min, int max) {

        List<Room> result = new ArrayList<>();

        for (Room room : rooms.values()) {

            int sqft = room.getSquareFeet();

            if (sqft >= min && sqft <= max) {

                result.add(room);
            }
        }
        return result;
    }

    /**
     * Filters rooms using multiple optional criteria. 
     * 
     * All parameters can be null. If parameters are null, the filter is ignored.
     * 
     * Users are able to filter through multiple categories with any combination they want,
     * 
     * or a single category. Parameter information documented above. 
     * 
     * @param building
     * @param occupancy
     * @param hasAC
     * @param minSize
     * @param maxSize
     * @return (List) a list of rooms matching all selected filters. 
     */
    public List<Room> filter(Building building, Integer occupancy, Boolean hasAC, Integer minSize, Integer maxSize) {

        List<Room> result = new ArrayList<>();

        for (Room room : rooms.values()) {

            if (building != null && room.getBuilding() != building) {
                continue;
            }

            if (occupancy != null && room.getOccupancy() != occupancy) {
                continue;
            }

            if (hasAC != null && room.hasAC() != hasAC) {
                continue;
            }

            if (minSize != null && room.getSquareFeet() < minSize) {
                continue;
            }

            if (maxSize != null && room.getSquareFeet() > maxSize) {
                continue;
            }

            result.add(room); // If a room passes the above selected filters, add to the resulting list.
        }

        return result;
    }

    /**
     * Prints every room in the given list.
     * @param list (List) the list of rooms to print. 
     */
    public void printRooms(List<Room> list) {

        for (Room room : list) {

            System.out.println(room);
        }
    }

    public static void main(String[] args) {

        try {

            // Load rooms from the master CSV file.
            Rooms rooms = RoomDataLoader.load("master.csv");

            // Test size()
            System.out.println("Testing size():");
            System.out.println("Loaded " + rooms.size() + " rooms.");
            System.out.println();

            // Test getAllRooms()
            System.out.println("Testing getAllRooms():");
            List<Room> allRooms = rooms.getAllRooms();
            System.out.println("getAllRooms() returned " + allRooms.size() + " rooms.");

            if (!allRooms.isEmpty()) {
                System.out.println("First room in list: " + allRooms.get(0));
            }
            System.out.println();

            // Test printRooms()
            System.out.println("Testing printRooms() with first 5 rooms:");
            List<Room> firstFiveRooms = new ArrayList<>();

            for (int i = 0; i < allRooms.size() && i < 5; i++) {
                firstFiveRooms.add(allRooms.get(i));
            }

            rooms.printRooms(firstFiveRooms);
            System.out.println();

            // Test get(String roomName)
            System.out.println("Testing get(String roomName):");

            if (!allRooms.isEmpty()) {

                String testRoomName = allRooms.get(0).getRoomName();
                Room foundRoom = rooms.get(testRoomName);

                System.out.println("Searching for: " + testRoomName);

                if (foundRoom != null) {
                    System.out.println("Found room: " + foundRoom);
                } else {
                    System.out.println("Room was not found.");
                }
            }
            System.out.println();

            // Test add(Room room)
            System.out.println("Testing add(Room room):");

            Room testRoom = new Room(
                    "Smiley - 999",
                    150,
                    null,
                    null,
                    null,
                    1,
                    false
            );

            rooms.add(testRoom);

            System.out.println("Added test room: " + testRoom.getRoomName());
            System.out.println("New size after add(): " + rooms.size());

            Room addedRoom = rooms.get("Smiley - 999");

            if (addedRoom != null) {
                System.out.println("Successfully found added room: " + addedRoom);
            } else {
                System.out.println("Added room was not found.");
            }
            System.out.println();

            // Test filterByBuilding(Building building)
            System.out.println("Testing filterByBuilding(Building.Smiley):");

            List<Room> smileyRooms = rooms.filterByBuilding(Building.Smiley);

            System.out.println("Number of Smiley rooms: " + smileyRooms.size());

            if (!smileyRooms.isEmpty()) {
                System.out.println("Example Smiley room: " + smileyRooms.get(0));
            }
            System.out.println();

            // Test filterByOccupancy(int occupancy)
            System.out.println("Testing filterByOccupancy(1):");

            List<Room> singles = rooms.filterByOccupancy(1);

            System.out.println("Number of single rooms: " + singles.size());

            if (!singles.isEmpty()) {
                System.out.println("Example single room: " + singles.get(0));
            }
            System.out.println();

            // Test filterByAC(boolean hasAC)
            System.out.println("Testing filterByAC(true):");

            List<Room> acRooms = rooms.filterByAC(true);

            System.out.println("Number of rooms with AC: " + acRooms.size());

            if (!acRooms.isEmpty()) {
                System.out.println("Example AC room: " + acRooms.get(0));
            }
            System.out.println();

            // Test filterBySquareFootage(int min, int max)
            System.out.println("Testing filterBySquareFootage(100, 200):");

            List<Room> mediumRooms = rooms.filterBySquareFootage(100, 200);

            System.out.println("Number of rooms between 100 and 200 sqft: " + mediumRooms.size());

            if (!mediumRooms.isEmpty()) {
                System.out.println("Example room between 100 and 200 sqft: " + mediumRooms.get(0));
            }
            System.out.println();

            // Test filter(Building building, Integer occupancy, Boolean hasAC, Integer minSize, Integer maxSize)
            System.out.println("Testing combined filter:");

            List<Room> filteredRooms = rooms.filter(
                    Building.Smiley,
                    1,
                    false,
                    100,
                    200
            );

            System.out.println("Number of Smiley single rooms without AC between 100 and 200 sqft: " + filteredRooms.size());

            if (!filteredRooms.isEmpty()) {
                System.out.println("Example combined-filter result: " + filteredRooms.get(0));
            }
            System.out.println();

            // Test get() on a room that should not exist.
            System.out.println("Testing get() with invalid room name:");

            Room missingRoom = rooms.get("Fake Dorm - 000");

            if (missingRoom == null) {
                System.out.println("Correctly returned null for a room that does not exist.");
            } else {
                System.out.println("Unexpectedly found room: " + missingRoom);
            }

            System.out.println();
            System.out.println("All Rooms tests completed.");

        } catch (IOException e) {

            System.out.println("Could not load room data.");
            e.printStackTrace();
        }
    }

}
