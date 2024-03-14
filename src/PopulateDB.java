import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;
import org.apache.poi.ss.usermodel.*;
import org.xml.sax.SAXException;

import ExtraExceptions.*;

public class PopulateDB {
    public static void main(String[] args){
        String pathToDatabase = "mydb.db";
        String pathToExcelFileEquipment = "Data/equipment_excel.xlsx";
        String pathToExcelFilePeople = "Data/people_excel.xlsx";

        try{
            populatePeople(pathToDatabase, pathToExcelFilePeople);
            populateEquipment(pathToDatabase, pathToExcelFileEquipment);
        }
        catch(IOException e){
            System.out.println("Fail, IO exception");
        }
        catch(SQLException e){
            System.out.println("Fail, SQL exception"); 
            e.printStackTrace();
        }
    }

    /**
     * Method to populate the people table in the database. Generally, the excel sheet with the people data is quite well populated,
     * as an online form is used in the data collection.
     * @param pathToDatabase
     * @param pathToExcelFile
     * @throws SQLException
     * @throws IOException
     */
    private static void populatePeople(String pathToDatabase, String pathToExcelFile) throws SQLException, IOException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:"+pathToDatabase);
        FileInputStream excelFile = new FileInputStream(pathToExcelFile);

        Workbook workbook = WorkbookFactory.create(excelFile);
        Sheet sheet = workbook.getSheetAt(0);

        PreparedStatement pstmt = conn.prepareStatement("INSERT OR IGNORE INTO People (email, name, department, job_title, engineering_pillars, research_keywords) VALUES (?,?,?,?,?,?)");

        for (Row row : sheet) {
            if (row.getRowNum() == 0) { // Skip header
                continue;
            }
            // Iterate over specific columns
            pstmt.setString(1, getStringValue(row.getCell(3))); // Column 3 Email
            pstmt.setString(2, getStringValue(row.getCell(4))); // Column 4 Name
            pstmt.setString(3, getStringValue(row.getCell(6))); // Column 6 Department
            pstmt.setString(4, getStringValue(row.getCell(7))); // Column 7 Job title
            pstmt.setString(5, getStringValue(row.getCell(9))); // Column 9 Research affiliation
            pstmt.setString(6, getStringValue(row.getCell(10))); // Column 10 Engineering pillars
            
            pstmt.executeUpdate();
        }
    }

    /**
     * Method to populate the equipment table in the database. The equipment excel sheets contain incomplete data,
     * with many null values, as well as repeated values, and therefore use an auto-incremented key, and some basic
     * checks are performed. Any equipment with nothing entered for "type" is excluded, as that makes the entry 
     * impossible to actually identify - and after inspection, almost all of the entries missing "type" were entirely null.
     * @param pathToDatabase
     * @param pathToExcelFile
     * @throws SQLException
     * @throws IOException
     */
    private static void populateEquipment(String pathToDatabase, String pathToExcelFile) throws SQLException, IOException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:"+pathToDatabase);
        FileInputStream excelFile = new FileInputStream(pathToExcelFile);

        Workbook workbook = WorkbookFactory.create(excelFile);
        
        for (int i = 1; i < workbook.getNumberOfSheets(); i++){
            Sheet sheet = workbook.getSheetAt(i);

            PreparedStatement pstmt = conn.prepareStatement("INSERT OR IGNORE INTO Equipment (type, description, details, location, contact, manager, access, terms, keywords, cost, owner) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
            for(Row row : sheet){
                if(row.getRowNum() == 0 || getStringValue(row.getCell(0)) == null || getStringValue(row.getCell(0)) == "Type"){
                    continue;
                }
                // the PandA spreadsheet has a different format
                if (sheet.getSheetName().equalsIgnoreCase("panda")) {
                    pstmt.setString(1, getStringValue(row.getCell(0)) +" "+ getStringValue(row.getCell(1))); // type (make + model from the spreadsheet)
                    pstmt.setString(2, getStringValue(row.getCell(2))); // description
                    //pstmt.setString(3, getStringValue(row.getCell(2)));
                    pstmt.setString(4, getStringValue(row.getCell(4))); // location
                    pstmt.setString(5, getStringValue(row.getCell(5))); // contact
                    //pstmt.setString(6, getStringValue(row.getCell(5)));
                    pstmt.setString(11, getStringValue(row.getCell(6))); // owner
                    pstmt.setString(7, getStringValue(row.getCell(7))); // access
                    pstmt.setString(9, getStringValue(row.getCell(3))); // keywords
                    pstmt.setString(8, getStringValue(row.getCell(10))); // terms
                    pstmt.setString(10, getStringValue(row.getCell(9))); // cost

                    pstmt.executeUpdate();
                }
                else{
                    pstmt.setString(1, getStringValue(row.getCell(0)));
                    pstmt.setString(2, getStringValue(row.getCell(1)));
                    pstmt.setString(3, getStringValue(row.getCell(2)));
                    pstmt.setString(4, getStringValue(row.getCell(3)));
                    pstmt.setString(5, getStringValue(row.getCell(4)));
                    pstmt.setString(6, getStringValue(row.getCell(5)));
                    pstmt.setString(7, getStringValue(row.getCell(6)));
                    pstmt.setString(8, getStringValue(row.getCell(7)));
                    pstmt.setString(9, getStringValue(row.getCell(9)));
                    pstmt.setString(10, getStringValue(row.getCell(10)));
                    pstmt.setString(11, getStringValue(row.getCell(11)));
    
                    pstmt.executeUpdate();

                }
            }
        }
    }


    private static String getStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            default:
                return null;
        }
    }


}