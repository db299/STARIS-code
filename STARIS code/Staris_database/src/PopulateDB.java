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