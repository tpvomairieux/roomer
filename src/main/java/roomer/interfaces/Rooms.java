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
            Rooms rooms = RoomDataLoader.load("CS62 Final Project Data.xlsx");

            System.out.println("Loaded " + rooms.size() + " rooms.");

            for (Room r : rooms.getAllRooms()) {
                System.out.println("Room: " + r.getRoomName() + " | Occupancy: " + r.getOccupancy());
            }

            List<Room> singles = rooms.filterByOccupancy(1);
            System.out.println("Single rooms: " + singles.size());

            List<Room> acRooms = rooms.filterByAC(true);
            System.out.println("Rooms with AC: " + acRooms.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
