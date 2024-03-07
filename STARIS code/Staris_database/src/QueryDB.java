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
        searchPeople("mydb.db", "", "Energy", "");
    }


    private static void searchPeople(String pathToDatabase, String searchTerms, String engineeringPillar, String department) throws SQLException {
        Set<String> emailsSearchTerms = new HashSet<>();
        Set<String> emailsEngineeringPillars = new HashSet<>();
        Set<String> emailsDepartment = new HashSet<>();

        ResultSet searchTermsResults = findEveryoneFromKeywords(pathToDatabase, searchTerms);
        ResultSet engineeringPillarsResults = findEveryoneFromEngineeringPillar(pathToDatabase, engineeringPillar);
        ResultSet departmentResults = findEveryoneFromDepartment(pathToDatabase, department);
    
        while (searchTermsResults.next()){
            String email = searchTermsResults.getString("email");
            emailsSearchTerms.add(email);
            //System.out.println(email);
           
        }
        System.out.println();
        while (engineeringPillarsResults.next()){
            String email = engineeringPillarsResults.getString("email");
            emailsEngineeringPillars.add(email);
            //System.out.println(email);
        }
        System.out.println();
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
