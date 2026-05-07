/**
 * Object that represents a list of all rooms
 *
 * @author Evan Tran, Phu Vo, Ronnie Ho
 *
 */
package roomer.interfaces;

import java.util.HashMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import roomer.RoomDataLoader;

public class Rooms {

    private HashMap<String, Room> rooms = new HashMap<>();

    public Rooms() {
        this.rooms = new HashMap<>();
    }

    public void add(Room room) {
        rooms.put(room.getRoomName(), room);
    }

    public Room get(String roomName) {
        return rooms.get(roomName);
    }

    public int size() {

        return rooms.size();
    }

    public void remove(String roomName) {
        rooms.remove(roomName);
    }

    public List<Room> getAllRooms() {

        return new ArrayList<>(rooms.values());
    }

    /**
     * 
     * @param building
     * @return
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
     * 
     * @param occupancy
     * @return
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
     * 
     * @param AC
     * @return
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
     * 
     * @param min
     * @param max
     * @return
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
     * 
     * @param building
     * @param occupancy
     * @param hasAC
     * @param minSize
     * @param maxSize
     * @return
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

            result.add(room);
        }

        return result;
    }

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
