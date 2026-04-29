/**
 * Documentation of methods explored from https://poi.apache.org/apidocs/dev/org/apache/poi/ss/usermodel/DataFormatter.html,
 * and use in several methods!
 */
package roomer;

import java.time.LocalDateTime;


import java.util.Iterator;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class RoomDataLoader {
   
    /**
     * 
     * @param filePath
     * @return 
     * @throws IOException
     */
    public static Rooms load(String filePath) throws IOException {

        Rooms rooms = new Rooms();
        DataFormatter formatter = new DataFormatter();

        try (FileInputStream stream = new FileInputStream(filePath); XSSFWorkbook workbook = new XSSFWorkbook(stream)) {

            XSSFSheet sheet = workbook.getSheet("Master"); // Takes data only from the master sheet in our excel file.
            
            if (sheet == null) { // Should only throw an exception if this is bugged, and our master sheet is missing. 

                throw new IllegalArgumentException("Worksheet 'Master' not found.");
            }

            Iterator<Row> rows = sheet.iterator();

            // This conditional skips the header row so we only take the data numbers.
            if(rows.hasNext()) {

                rows.next();
            }

            while(rows.hasNext()) {

                Row row = rows.next();

                String rawName = getString(row.getCell(0), formatter);

                // Skip if there's a blank room-name.
                if (rawName == null || rawName.isBlank()) {

                    continue; 
                }

                int sqft = getInt(row.getCell(1), formatter);
                
                LocalDateTime time2023 = getDateTime(row.getCell(5));
                LocalDateTime time2024 = getDateTime(row.getCell(8));
                LocalDateTime time2025 = getDateTime(row.getCell(11));

                int occupancy = getInt(row.getCell(12), formatter);

                boolean ac = getBoolean(row.getCell(13), formatter);

                Room room = new Room(rawName, sqft, time2023, time2024, time2025, occupancy, ac);
                
                rooms.add(room);
            }
        }
        
        return rooms;
    }
    
    /**
     *  Helper method for extracting strings from cell in excel.  
     * @param cell
     * @param formatter
     * @return
     */

    private static String getString(Cell cell, DataFormatter formatter) {
        
        if (cell == null) {
            
            return null;
        }
        return formatter.formatCellValue(cell).trim(); // .formatCellValue returns the value of the cell as a string regardless of the original type.
    }

    /**
     * Helper method to extract integers from a cell. 
     * @param cell
     * @param formatter
     * @return
     */
    private static int getInt(Cell cell, DataFormatter formatter) {
        
        if (cell == null) {
            
            return 0;
        }

        String text = formatter.formatCellValue(cell).trim();
        if (text.isEmpty()) {
            
            return 0;
        }

        try {
            
            return Integer.parseInt(text);
        
        } catch (NumberFormatException e) {
            
            return 0;
        }
    }

    /**
     * Helper method to see if the room of choice has AC from a cell. 
     * @param cell
     * @param formatter
     * @return
     */
    private static boolean getBoolean(Cell cell, DataFormatter formatter) {
      
        if (cell == null) {
           
            return false;
        }

        String text = formatter.formatCellValue(cell).trim();

        return text.equals("TRUE");
    }

    /**
     * Helper method to get the time of room draw from a cell. 
     * @param cell
     * @return
     */
    private static LocalDateTime getDateTime(Cell cell) {
        if (cell == null) {
            return null;
        }

        try {

            return cell.getLocalDateTimeCellValue();
        
        } catch (Exception e) {
            
            return null;
        }
    }

    public static void main(String[] args) {

        try {
            Rooms rooms = load("CS62 Final Project Data.xlsx");
            System.out.println("Loaded " + rooms.size() + " rooms.");

            if (rooms.size() > 0) {
                System.out.println("First room loaded!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
}