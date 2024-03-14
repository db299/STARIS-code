import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

public class QueryDB {
    public static void main(String[] args) throws SQLException {
        //findEveryoneFromDepartment("mydb.db", "Physics");
        //findEveryoneFromEngineeringPillar("mydb.db", "sustainable");
        //findEveryoneFromKeywords("mydb.db", "Sensors");
        //searchPeople("mydb.db", "", "Energy", "");
        searchEquipment("mydb.db", "centrifuge",  "centrifuge");
    }

    public static void searchEquipment(String pathToDatabase, String searchTerms, String type) throws SQLException{
        Set<String> idsSearchTerms = new HashSet<>();
        Set<String> idsType = new HashSet<>();
        

        ResultSet searchTermsResults = findEquipmentFromKeywords(pathToDatabase, searchTerms);
        ResultSet typeResults = findEqipmentFromType(pathToDatabase, type);

        // Extract the ids from the search terms results
        while (searchTermsResults.next()){
            String id = searchTermsResults.getString("id");
            idsSearchTerms.add(id);
            //System.out.println(email);
           
        }
        
        // Extract the ids from the type search results
        while (typeResults.next()){
            String id = typeResults.getString("id");
            idsType.add(id);
            //System.out.println(email);
        }

        idsSearchTerms.addAll(idsType);

        Set<String> ids = idsSearchTerms;

        if (!ids.isEmpty()){
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT * FROM Equipment WHERE id IN (");
            for (String id : ids){
                queryBuilder.append("'").append(id).append("', ");
            }
            queryBuilder.delete(queryBuilder.length()-2,queryBuilder.length());
            queryBuilder.append(")");
    
            System.out.println(queryBuilder.toString());
            // Execute the query
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + pathToDatabase);
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(queryBuilder.toString());
            while (results.next()) {
                String equipment_type = results.getString("type");
                String description = results.getString("description");
                String location = results.getString("location");
                String contact = results.getString("contact");
                String access = results.getString("access");
        
                System.out.println(equipment_type + " " + description + " at location " + location + " contact: " + contact + " with access restrictions " + access);
            }

        }




    }
    public static ResultSet findEquipmentFromKeywords(String pathToDatabase, String searchTerms) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + pathToDatabase);
    
        // Create a statement
        Statement statement = connection.createStatement();
    
        // Split the search terms into individual keywords
        String[] keywords = searchTerms.split("\\s*,\\s*|\\s+"); // Split by whitespace or comma
    
        // Construct the SQL query dynamically
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT * FROM Equipment WHERE ");
    
        for (int i = 0; i < keywords.length; i++) {
            if (i > 0) {
                queryBuilder.append("OR ");
            }
            queryBuilder.append("keywords LIKE '%" + keywords[i] + "%' ");
        }
    
        // Execute the query
        ResultSet resultSet = statement.executeQuery(queryBuilder.toString());
    

        
        return resultSet;
    }

    public static ResultSet findEqipmentFromType(String pathToDatabase, String type) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + pathToDatabase);
    
        // Create a statement
        Statement statement = connection.createStatement();
    
        // Split the search terms into individual keywords
        String[] keywords = type.split("\\s*,\\s*|\\s+"); // Split by whitespace or comma
    
        // Construct the SQL query dynamically
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT * FROM Equipment WHERE ");
    
        for (int i = 0; i < keywords.length; i++) {
            if (i > 0) {
                queryBuilder.append("OR ");
            }
            queryBuilder.append("type LIKE '%" + keywords[i] + "%' ");
        }
    
        // Execute the query
        ResultSet resultSet = statement.executeQuery(queryBuilder.toString());
    

        
        return resultSet;
    }
    /**
     * Method to search details about individual people. The way this is meant to be used is with the engineerinig pillar and department
     * entered automatically by ticking boxes/dropdown menu, and the search terms being actually input as text by the user.
     * @param pathToDatabase
     * @param searchTerms
     * @param engineeringPillar
     * @param department
     * @throws SQLException
     */
    public static void searchPeople(String pathToDatabase, String searchTerms, String engineeringPillar, String department) throws SQLException {
        Set<String> emailsSearchTerms = new HashSet<>();
        Set<String> emailsEngineeringPillars = new HashSet<>();
        Set<String> emailsDepartment = new HashSet<>();

        ResultSet searchTermsResults = findEveryoneFromKeywords(pathToDatabase, searchTerms);
        ResultSet engineeringPillarsResults = findEveryoneFromEngineeringPillar(pathToDatabase, engineeringPillar);
        ResultSet departmentResults = findEveryoneFromDepartment(pathToDatabase, department);
        

        // Extract the emails from the results for keywords search
        while (searchTermsResults.next()){
            String email = searchTermsResults.getString("email");
            emailsSearchTerms.add(email);
            //System.out.println(email);
           
        }
        
        // Extract the emails from the results for engineering pillars search
        while (engineeringPillarsResults.next()){
            String email = engineeringPillarsResults.getString("email");
            emailsEngineeringPillars.add(email);
            //System.out.println(email);
        }
        
        // Extract the emails from the results for department search
        while (departmentResults.next()){
            String email = departmentResults.getString("email");
            emailsDepartment.add(email);
            //System.out.println(email);
        }

        emailsSearchTerms.retainAll(emailsEngineeringPillars);
        emailsSearchTerms.retainAll(emailsDepartment);

        Set<String> emails = emailsSearchTerms;

        if (!emails.isEmpty()){
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT * FROM People WHERE email IN (");
            for (String email : emails){
                queryBuilder.append("'").append(email).append("', ");
            }
            queryBuilder.delete(queryBuilder.length()-2,queryBuilder.length());
            queryBuilder.append(")");
    
            System.out.println(queryBuilder.toString());
            // Execute the query
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + pathToDatabase);
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(queryBuilder.toString());
            while (results.next()) {
                String email = results.getString("email");
                String name = results.getString("name");
                String result = results.getString("research_keywords");
                String department1 = results.getString("department");
                String pillars = results.getString("engineering_pillars");
        
                System.out.println(email + " " + name + " " + result + " from department " + department1 + " with pillars " + pillars);
            }

        }
    
    }


    /**
     * Method for searching all people which have a specific keyword in their records
     * @param pathToDatabase
     * @param searchTerms
     * @return
     * @throws SQLException
     */
    public static ResultSet findEveryoneFromKeywords(String pathToDatabase, String searchTerms) throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + pathToDatabase);
    
        // Create a statement
        Statement statement = connection.createStatement();
    
        // Split the search terms into individual keywords
        String[] keywords = searchTerms.split("\\s*,\\s*|\\s+"); // Split by whitespace or comma
    
        // Construct the SQL query dynamically
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT * FROM People WHERE ");
    
        for (int i = 0; i < keywords.length; i++) {
            if (i > 0) {
                queryBuilder.append("OR ");
            }
            queryBuilder.append("research_keywords LIKE '%" + keywords[i] + "%' ");
        }
    
        // Execute the query
        ResultSet resultSet = statement.executeQuery(queryBuilder.toString());
    

        
        return resultSet;
    }
    
    /**
     * Method for finding all people within a specific engineering pillar
     * @param pathToDatabase
     * @param engineeringPillars
     * @return
     * @throws SQLException
     */
    public static ResultSet findEveryoneFromEngineeringPillar(String pathToDatabase, String engineeringPillars) throws SQLException{
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + pathToDatabase);
    
        // Create a statement
        Statement statement = connection.createStatement();
    
        // Split the search terms into individual keywords
        String[] keywords = engineeringPillars.split("\\s*,\\s*|\\s+"); // Split by whitespace or comma
    
        // Construct the SQL query dynamically
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT * FROM People WHERE ");
    
        for (int i = 0; i < keywords.length; i++) {
            if (i > 0) {
                queryBuilder.append("OR ");
            }
            queryBuilder.append("engineering_pillars LIKE '%" + keywords[i] + "%' ");
        }
    
        // Execute the query
        ResultSet resultSet = statement.executeQuery(queryBuilder.toString());
    

        
        return resultSet;
    }


    /**
     * Method for finding all people within a specific department
     * @param pathToDatabase
     * @param department
     * @return
     * @throws SQLException
     */
    public static ResultSet findEveryoneFromDepartment(String pathToDatabase, String department) throws SQLException {
        // Connect to the SQLite database
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + pathToDatabase);

        // Create a statement
        Statement statement = connection.createStatement();

        // Execute the query
        ResultSet resultSet = statement.executeQuery(
                "SELECT * FROM People " +
                        "WHERE department LIKE '%" + department + "%'");

 

        return resultSet;

    }

}
