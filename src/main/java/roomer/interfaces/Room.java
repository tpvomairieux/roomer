/**
 * Object that represents a room. We use Apache POI to build a rooms directory given the .xlsx file in the project.
 * See more details in RoomData.java. See tutorial and information credit detail in pom.xml.
 *
 * @author Evan Tran, Phu Vo, Ronnie Ho
 *
 */
package roomer.interfaces;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Room {

    final String rawName; // Raw name of dorms, from the excel file. 
    final int sqft; // Renamed to sqft from size
    final LocalDateTime time2023; // 2023 Draw time, from HRL
    final LocalDateTime time2024; // 2024 Draw time, from HRL
    final LocalDateTime time2025; // 2025 Draw time, from HRL
    int occupancy; // How many students can be in a room: single, double, triple. 
    String roomName; // name of the dorm room
    String suite = "";
    Building building;

    // Not storing value - value of dorm depends on method user wants for valuation,
    // such as average time, latest time, etc. Also need 2 account for different
    // dates year-to-year for estimating valuation
    public Room(String rawName, int sqft, LocalDateTime time2023, LocalDateTime time2024, LocalDateTime time2025, int occupancy, boolean ac) {

        this.rawName = rawName;
        this.sqft = sqft;
        this.time2023 = time2023;
        this.time2024 = time2024;
        this.time2025 = time2025;
        this.occupancy = occupancy;

        processRawName();
    }

    /**
     * This method processes the raw dorm name from the Excel file.
     *
     * All dorm-style names to cover include: normal rooms, suited rooms, rooms with letter endings, and suites with internal hyphens. 
     * Blaisdell - 111
     * Blaisdell - BLSD 341-343 - 341
     * Clark I - CL-I 101 - 101A
     * Clark V - CL-V 441-442 - 441
     */
    public void processRawName() {

        // Split only on the separator between parts, not on every hyphen.
        String[] splitName = this.rawName.split(" - ");

        // The first part is always the building name.
        String building = splitName[0].trim();
        setBuilding(building);

        // The last part is always the actual room number.
        String roomNumber = splitName[splitName.length - 1].trim();

        // Store a cleaned room name like "Clark V - 441," from "Clark V - CL-V 441-442 - 441."
        this.roomName = building + " - " + roomNumber;

        // If there is something between the building and the room number, treat it as suite information.
        if (splitName.length >= 3) {

            StringBuilder suiteBuilder = new StringBuilder();

            for (int i = 1; i < splitName.length - 1; i++) {

                if (suiteBuilder.length() > 0) {
                    suiteBuilder.append(" - ");
                }

                suiteBuilder.append(splitName[i].trim());
            }

            this.suite = suiteBuilder.toString();

        } else {

            // No suite information for normal dorm rooms like "Blaisdell - 111".
            this.suite = "";
        }
    }
    
    /**
     * Assigns a building and it's information to a room.
     * @param building
     */
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

    /**
     * This method gets the clean room name of each dorm. 
     * @return (String) a cleaned up name of a dorm, ex: Clark V -441.
     */
    public String getRoomName() {

        return roomName;
    }

    /**
     * This method gets the occupancy count of the dorm room. 
     * @return (Int) the occupancy of a dorm as an integer: 1, 2, or 3. 
     */
    public int getOccupancy() {

        return occupancy;
    }

    /**
     * This method gets the building informatino of the dorm. 
     * @return (Enum) the building information. 
     */
    public Building getBuilding() {

        return building;
    }

    /**
     * This method gets the sqft of a dorm. 
     * @return (Int) the size of a room. 
     */
    public int getSquareFeet() {

        return sqft;
    }

    /**
     * This method gets the AC status of the building.
     * @return (boolean) If the dorm has ac or not.
     */
    public boolean hasAC() {

        return building.hasAC();
    }

    /**
     * This method gets the 2023 draw-selection time.
     * @return (Local Date Time) the 2023 dorm selection time.
     */
    public LocalDateTime getTime2023() {

        return time2023;
    }

    /**
     * This method gets the 2024 draw-selection time.
     * @return (Local Date Time) the 2024 dorm selection time.
     */
    public LocalDateTime getTime2024() {

        return time2024;
    }

    /**
     * This method gets the 2025 draw-selection time.
     * @return (Local Date Time) the 2025 dorm selection time.
     */
    public LocalDateTime getTime2025() {

        return time2025;
    }

    /**
     * This method returns a string of all the draw times.
     * @return (String) A string representation of all the draw times from 2023, 2024, and 2025, if available. 
     */
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

        if (hasAC()) {

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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a", Locale.ENGLISH);

        Room test = new Room(
                "Dialynas - DLNS 100 - 101",
                137,
                LocalDateTime.parse("Apr 11, 2023 8:15 PM", formatter),
                LocalDateTime.parse("Apr 9, 2024 6:42 PM", formatter),
                LocalDateTime.parse("Apr 8, 2025 5:27 PM", formatter),
                1,
                true);

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
