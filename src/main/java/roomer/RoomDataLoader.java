/**
 * Loads room data from a CSV file and creates Room objects.
 *
 * The CSV file should come from the cleaned master sheet and includes:
 * Square Footage of Unique Dorms, Unique Dorms, 2023 Selection Time,
 * 2024 Selection Time, 2025 Selection Time, Occupancy, and AC status. 
 *
 * @author Evan Tran, Phu Vo, Ronnie Ho
 *
 */
package roomer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import roomer.interfaces.Room;
import roomer.interfaces.Rooms;

public class RoomDataLoader {

    // Column indexes from the cleaned CSV. Helps avoid later "magic variables" by defining them here. 
    private static final int SQFT_COL = 0;
    private static final int RAW_NAME_COL = 1;
    private static final int TIME_2023_COL = 4;
    private static final int TIME_2024_COL = 7;
    private static final int TIME_2025_COL = 10;
    private static final int OCCUPANCY_COL = 11;
    private static final int AC_COL = 12;

    /**
     * Loads room data from a CSV file.
     *
     * Each row in the CSV becomes one Room object. The Room object then processes
     * its raw name into a cleaned room name, building, and suite if applicable.
     *
     * @param filePath the path to the CSV file
     * @return a Rooms object containing all loaded rooms
     * @throws IOException if the file cannot be opened or read
     */
    public static Rooms load(String filePath) throws IOException {

        Rooms rooms = new Rooms();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            // Skip the header row.
            reader.readLine();

            String line;

            // Read the CSV one row at a time until there are no more rows.
            while ((line = reader.readLine()) != null) {

                String[] parts = splitCSVLine(line); // Split the current CSV row into individual column values.

                // Skip rows that do not have enough columns.
                if (parts.length < 13) {
                    continue;
                }

                int sqft = getInt(parts[SQFT_COL]);

                String rawName = getString(parts[RAW_NAME_COL]);

                LocalDateTime time2023 = getDateTime(parts[TIME_2023_COL]);
                LocalDateTime time2024 = getDateTime(parts[TIME_2024_COL]);
                LocalDateTime time2025 = getDateTime(parts[TIME_2025_COL]);

                int occupancy = getInt(parts[OCCUPANCY_COL]);

                boolean ac = getBoolean(parts[AC_COL]);

                // Skip blank or incomplete rows.
                if (rawName == null || rawName.isBlank()) {
                    continue;
                }

                Room room = new Room(rawName, sqft, time2023, time2024, time2025, occupancy, ac); // Create a Room object using the cleaned CSV values.

                rooms.add(room);
            }
        }

        return rooms;
    }

    /**
     * Splits a CSV line into columns.
     *
     * This handles normal comma-separated CSV lines and quoted cells.
     *
     * @param line one line from the CSV file
     * @return an array of column values
     */
    private static String[] splitCSVLine(String line) {

        return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1); // Splits on commas only when the comma is not inside quotation marks, keeps trailing blank cells.
    }

    /**
     * Cleans a string value from the CSV.
     *
     * @param value the raw CSV value
     * @return a trimmed string with surrounding quotes removed
     */
    private static String getString(String value) {

        // If the value does not exist, return null.
        if (value == null) {
            return null;
        }

        value = value.trim(); 

         // If the value is surrounded by quotation marks, remove those outside quotes.
        if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
            value = value.substring(1, value.length() - 1);
        }

        return value.trim(); // Trim again in case removing quotes exposed extra whitespace.
    }

    /**
     * Converts a CSV value into an integer.
     *
     * @param value the raw CSV value
     * @return the integer value, or 0 if the value is blank or invalid
     */
    private static int getInt(String value) {

        value = getString(value);

        if (value == null || value.isBlank()) {
            return 0;
        }

        value = value.replaceAll("[^0-9]", "");

        if (value.isBlank()) {
            return 0;
        }

        return Integer.parseInt(value);
    }

    /**
     * Converts a CSV value into a boolean.
     *
     * @param value the raw CSV value
     * @return true if the value is TRUE, false otherwise
     */
    private static boolean getBoolean(String value) {

        value = getString(value);

        if (value == null) {
            return false;
        }

        return value.equalsIgnoreCase("TRUE");
    }

    /**
     * Converts a CSV value into a LocalDateTime.
     *
     * The CSV currently stores dates like:
     * 4/13/2023 18:54
     *
     * Blank cells and "No Selection Time Issued" return null.
     *
     * @param value the raw CSV value
     * @return the parsed LocalDateTime, or null if no valid time exists
     */
    private static LocalDateTime getDateTime(String value) {

        value = getString(value);

        if (value == null || value.isBlank()) {
            return null;
        }

        if (value.equalsIgnoreCase("No Selection Time Issued")) {
            return null;
        }

        try {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy H:mm");

            return LocalDateTime.parse(value, formatter);

        } catch (Exception e) {

            return null;
        }
    }

    /**
     * Main method for testing the CSV loader.
     *
     * @param args
     */
    public static void main(String[] args) {

        try {

            // Test load(String filePath)
            System.out.println("Testing load(String filePath):");

            Rooms rooms = load("master.csv");

            System.out.println("Loaded " + rooms.size() + " rooms.");
            System.out.println();

            // Test that loaded rooms can be accessed
            System.out.println("Testing loaded Rooms object:");

            int count = 0;

            for (Room r : rooms.getAllRooms()) {

                System.out.println(r);

                count++;

                if (count == 5) {
                    break;
                }
            }

            System.out.println();

            // Test splitCSVLine(String line)
            System.out.println("Testing splitCSVLine(String line):");

            String testCSVLine = "137,\"Dialynas - DLNS 100 - 101\",unused,unused,4/11/2023 20:15,unused,unused,4/9/2024 18:42,unused,unused,4/8/2025 17:27,1,TRUE";

            String[] parts = splitCSVLine(testCSVLine);

            System.out.println("Number of columns found: " + parts.length);
            System.out.println("Column 0, square footage: " + parts[SQFT_COL]);
            System.out.println("Column 1, raw room name: " + parts[RAW_NAME_COL]);
            System.out.println("Column 4, 2023 time: " + parts[TIME_2023_COL]);
            System.out.println("Column 12, AC status: " + parts[AC_COL]);
            System.out.println();

            // Test getString(String value)
            System.out.println("Testing getString(String value):");

            String quotedString = "\"Dialynas - DLNS 100 - 101\"";
            String cleanedString = getString(quotedString);

            System.out.println("Original string: " + quotedString);
            System.out.println("Cleaned string: " + cleanedString);
            System.out.println();

            // Test getInt(String value)
            System.out.println("Testing getInt(String value):");

            int sqft = getInt("137 sqft");
            int blankInt = getInt("");

            System.out.println("getInt(\"137 sqft\") returned: " + sqft);
            System.out.println("getInt(\"\") returned: " + blankInt);
            System.out.println();

            // Test getBoolean(String value)
            System.out.println("Testing getBoolean(String value):");

            boolean trueValue = getBoolean("TRUE");
            boolean falseValue = getBoolean("FALSE");
            boolean blankBoolean = getBoolean("");

            System.out.println("getBoolean(\"TRUE\") returned: " + trueValue);
            System.out.println("getBoolean(\"FALSE\") returned: " + falseValue);
            System.out.println("getBoolean(\"\") returned: " + blankBoolean);
            System.out.println();

            // Test getDateTime(String value)
            System.out.println("Testing getDateTime(String value):");

            LocalDateTime validDateTime = getDateTime("4/13/2023 18:54");
            LocalDateTime noSelectionTime = getDateTime("No Selection Time Issued");
            LocalDateTime blankDateTime = getDateTime("");

            System.out.println("getDateTime(\"4/13/2023 18:54\") returned: " + validDateTime);
            System.out.println("getDateTime(\"No Selection Time Issued\") returned: " + noSelectionTime);
            System.out.println("getDateTime(\"\") returned: " + blankDateTime);
            System.out.println();

            // Test creating a Room from parsed values
            System.out.println("Testing parsed values by creating one Room:");

            int parsedSqft = getInt(parts[SQFT_COL]);
            String parsedRawName = getString(parts[RAW_NAME_COL]);
            LocalDateTime parsedTime2023 = getDateTime(parts[TIME_2023_COL]);
            LocalDateTime parsedTime2024 = getDateTime(parts[TIME_2024_COL]);
            LocalDateTime parsedTime2025 = getDateTime(parts[TIME_2025_COL]);
            int parsedOccupancy = getInt(parts[OCCUPANCY_COL]);
            boolean parsedAC = getBoolean(parts[AC_COL]);

            Room testRoom = new Room(
                    parsedRawName,
                    parsedSqft,
                    parsedTime2023,
                    parsedTime2024,
                    parsedTime2025,
                    parsedOccupancy,
                    parsedAC
            );

            System.out.println("Created test room: " + testRoom);
            System.out.println(testRoom.completeDrawHistory());

            System.out.println("All RoomDataLoader tests completed.");

        } catch (IOException e) {

            System.out.println("Could not load room data.");
            e.printStackTrace();
        }
    }
}