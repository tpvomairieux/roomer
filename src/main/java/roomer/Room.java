/**
 * Object that represents a room. We use Apache POI to build a rooms directory given the .xlsx file in the project.
 * See more details in RoomData.java. See tutorial and information credit detail in pom.xml.
 *
 * @author Evan Tran, Phu Vo, Ronnie Ho
 *
 */
package roomer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import java.io.FileInputStream;
import java.io.IOException;


public class Room {

    final String rawName;
    final int size;
    final LocalDateTime time2023;
    final LocalDateTime time2024;
    final LocalDateTime time2025;
    int occupancy;
    String roomName;
    String suite = "";
    Building building;

    // Not storing value - value of dorm depends on method user wants for valuation,
    // such as average time, latest time, etc. Also need 2 account for different
    // dates year-to-year for estimating valuation

    public Room(String rawName, int size, LocalDateTime time2023, LocalDateTime time2024, LocalDateTime time2025,
            int occupancy, boolean ac) {
        this.rawName = rawName;
        this.size = size;
        this.time2023 = time2023;
        this.time2024 = time2024;
        this.time2025 = time2025;
        this.occupancy = occupancy;

        processRawName();
    }

    public void processRawName() {
        String[] splitName = this.rawName.split(" - ");
        String building = splitName[0];
        setBuilding(building);


        this.roomName = splitName[0].trim() + "-" + splitName[splitName.length - 1].trim(); // prevents white-spacing issue
        

        if (splitName.length == 3) {

            this.suite = splitName[1].trim();
        }
    }

    public void setBuilding(String building) {
        switch (building) {

            case "Lyon":
                this.building = Building.Lyon;
                break;

            case "Blaisdell":
                this.building = Building.Blaisdell;
                break;

            case "Mudd":
                this.building = Building.Mudd;
                break;

            case "Wig":
                this.building = Building.Wig;
                break;

            case "Gibson":
                this.building = Building.Gibson;
                break;

            case "Harwood":
                this.building = Building.Harwood;
                break;

            case "Oldenborg":
                this.building = Building.Oldenborg;
                break;

            case "Smiley":
                this.building = Building.Smiley;
                break;

            case "Clark I":
                this.building = Building.ClarkI;
                break;

            case "Clark III":
                this.building = Building.ClarkIII;
                break;

            case "Clark V":
                this.building = Building.ClarkV;
                break;

            case "Lawry":
                this.building = Building.Lawry;
                break;

            case "Norton":
                this.building = Building.Norton;
                break;

            case "Dialynas":
                this.building = Building.Dialynas;
                break;

            case "Sontag":
                this.building = Building.Sontag;
                break;

            case "Walker":
                this.building = Building.Walker;
                break;

            default:
                throw new IllegalArgumentException("Unknown building: " + building);
        }
    }

    public String getRoomName() {

        return roomName;
    }

    public int getOccupancy() {

        return occupancy;
    }

    public Building getBuilding() {
        
        return building;
    }

    public static void main(String args[]) {

    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a", Locale.ENGLISH);

    Room test = new Room(
        "Dialynas - DLNS 100 - 101",
        137,
        LocalDateTime.parse("Apr 11, 2023 8:15 PM", formatter),
        LocalDateTime.parse("Apr 9, 2024 6:42 PM", formatter),
        LocalDateTime.parse("Apr 8, 2025 5:27 PM", formatter),
        1,
        true
    );

    System.out.println(test.rawName);
    System.out.println(test.size);
    System.out.println(test.time2023);
    System.out.println(test.time2024);
    System.out.println(test.time2025);
    System.out.println(test.occupancy);
    System.out.println(test.roomName);
    System.out.println(test.suite);
    System.out.println(test.building);

    }
}
