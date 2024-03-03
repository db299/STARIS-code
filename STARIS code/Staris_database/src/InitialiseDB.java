
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

import ExtraExceptions.DataBaseInitialisationException;

public class InitialiseDB {
    public static void main(String[] args) {
        try {
            initialiseDatabase();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Ensuring that all tables in the database have been initialised
     * 
     * @param conn
     * @throws SQLException
     */
    private static void checkAllTablesInitialised(Connection conn) throws SQLException {
        // Ensure all tables exist
        if (checkTablesExist(conn)) {
            System.out.println("All tables initialised!");
        } else {
            throw new DataBaseInitialisationException(
                    "There was an error in creating the database. Please check the schema file and ensure all syntax is correct");
        }
    }
    /**
     * Initialises the database
     * @throws SQLException
     * @throws IOException
     */
    public static void initialiseDatabase() throws SQLException, IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter the path to the database you wish to initialise.");
        String dbPath = sc.next();
        System.out.println(
                "Please enter the path to the DDL Schema (pre-written text file containing SQL commands) that should be used to initialise the database.");
        String schemaPath = sc.next();
        sc.close();
        checkIfDbExists(dbPath);
        executeDdlSchema(dbPath, schemaPath);
        System.out.println("Reached here");
    }
    /**
     * Checks if the database at the specified path exists.
     * If it does not, it is created
     * @param dbPath
     */
    private static void checkIfDbExists(String dbPath) {
        File file = new File(dbPath);
        if (file.exists()) {
            System.out.println("Database exists at specified path.");
            System.out.println("Deleting existing database...");
            file.delete();
        }
    }
    /**
     * Checks if all tables in the schema exist
     * @param conn
     * @return
     * @throws SQLException
     */
    private static Boolean checkTablesExist(Connection conn) throws SQLException {
        String[] tableNames = { "People", "Equipment"};
        DatabaseMetaData meta = conn.getMetaData();
        boolean allTablesExist = true;
        for (String tableName : tableNames) {
            ResultSet table = meta.getTables(null, null, tableName, null);
            boolean tableExists = table.next();
            table.close();

            if (!tableExists) {
                allTablesExist = false;
                break;
            }
        }
        return allTablesExist;
    }
    /**
     * Executes the Sql code from the schema and performs some 
     * basic checks.
     * @param dbPath
     * @param schemaPath
     * @throws SQLException
     * @throws IOException
     */
    private static void executeDdlSchema(String dbPath, String schemaPath) throws SQLException, IOException {
        // SQLite database URL
        String url = "jdbc:sqlite:" + dbPath;
        String ddlSchemaString = new String(Files.readAllBytes(Paths.get(schemaPath)), StandardCharsets.UTF_8);
        System.out.println("Initialising the new database...");
        // Create a connection to the database
        Connection conn = DriverManager.getConnection(url);

        // Create a statement object to execute SQL commands
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(ddlSchemaString);
        System.out.println("Checking validity...");
        // Checking that all tables have been initialised
        checkAllTablesInitialised(conn);
        stmt.close();
        conn.close();
    }
}
