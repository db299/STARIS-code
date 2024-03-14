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
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter the path to the database");
        String pathToDatabase = sc.nextLine();
        System.out.println("Please enter the path to the Excel file");
        String pathToExcelFile = sc.nextLine();
        sc.close();

        try{
            populatePeople(pathToDatabase, pathToExcelFile);
        }
        catch(IOException e){
            System.out.println("Fail, IO exception");
        }
        catch(SQLException e){
            System.out.println("Fail, SQL exception");
        }
    }


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

    private static void populateEquipment(String pathToDatabase, String pathToExcelFile) throws SQLException, IOException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:"+pathToDatabase);
        FileInputStream excelFile = new FileInputStream(pathToExcelFile);

        Workbook workbook = WorkbookFactory.create(excelFile);
        
        for (int i = 1; i < workbook.getNumberOfSheets(); i++){
            Sheet sheet = workbook.getSheetAt(i);

            PreparedStatement pstmt = conn.prepareStatement("INSERT OR IGNORE INTO Equipment (type, description, details, location, contact, manager, access, terms, keywords, cost, owner) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
            
            for(Row row : sheet){
                if(row.getRowNum() == 0){
                    continue;
                }
            pstmt.setString(1, getStringValue(row.getCell(1)));
            pstmt.setString(2, getStringValue(row.getCell(2)));
            pstmt.setString(3, getStringValue(row.getCell(3)));
            pstmt.setString(4, getStringValue(row.getCell(4)));
            pstmt.setString(5, getStringValue(row.getCell(5)));
            pstmt.setString(6, getStringValue(row.getCell(6)));
            pstmt.setString(7, getStringValue(row.getCell(7)));
            pstmt.setString(8, getStringValue(row.getCell(8)));
            pstmt.setString(10, getStringValue(row.getCell(10)));
            pstmt.setString(11, getStringValue(row.getCell(11)));
            pstmt.setString(12, getStringValue(row.getCell(12)));

            pstmt.executeUpdate();
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