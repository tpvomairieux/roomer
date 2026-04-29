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


public class Room {

    final String rawName;
    final int sqft; //Renamed to sqft from size
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

    public Room(String rawName, int sqft, LocalDateTime time2023, LocalDateTime time2024, LocalDateTime time2025,
            int occupancy, boolean ac) {
        this.rawName = rawName;
        this.sqft = sqft;
        this.time2023 = time2023;
        this.time2024 = time2024;
        this.time2025 = time2025;
        this.occupancy = occupancy;

        processRawName();
    }

    public void processRawName() {
        String[] splitName = this.rawName.split(" - ");
        String building = splitName[0].trim();
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

    public int getSquareFeet() {
        
        return sqft;
    }

    public boolean hasAC() {
        
        return building.hasAC();
    }

    public LocalDateTime getTime2023() {
       
        return time2023;
    }

    public LocalDateTime getTime2024() {
        
        return time2024;
    }

    public LocalDateTime getTime2025() {
       
        return time2025;
    }

    public String completeDrawHistory() {

        StringBuilder sb = new StringBuilder();

        sb.append("Historical draw times for ").append(roomName).append(" include:\n");

        sb.append("2023: ");

        if (time2023 != null) {
            
            sb.append(time2023);
        } else {

            sb.append("No data available for 2023.");
        }
        sb.append("\n");

        sb.append("2024: ");

        if (time2024 != null) {

            sb.append(time2024);
        } else {

            sb.append("No data available for 2024.");
        }
        sb.append("\n");

        sb.append("2025: ");

        if (time2025 != null) {

            sb.append(time2025);
        } else {

            sb.append("No data available for 2025.");
        }
        sb.append("\n");

        return sb.toString();
    }

    @Override
    public String toString() {

        String acStatus;

        if(hasAC()) {
            
            acStatus = "AC is available.";
        } else {

            acStatus = "No AC available.";
        }

        return roomName + " | " +
            building + " | " +
            occupancy + " ppl | " +
            sqft + " sqft | " +
            acStatus;
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
    System.out.println(test.sqft);
    System.out.println(test.time2023);
    System.out.println(test.time2024);
    System.out.println(test.time2025);
    System.out.println(test.occupancy);
    System.out.println(test.roomName);
    System.out.println(test.suite);
    System.out.println(test.building);

    }
}
