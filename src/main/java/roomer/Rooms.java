/**
 * Object that represents a list of all rooms
 *
 * @author Evan Tran, Phu Vo, Ronnie Ho
 *
 */
package roomer;

import java.util.HashMap;

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

}
