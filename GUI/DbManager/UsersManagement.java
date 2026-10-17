package DbManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;

// For manipulation in the Database of users.
public class UsersManagement {

	private static final String DB_PATH ="users.db";

    // Store a new Client.
    public void storeUser(String uniqueId, String password){
    	String connectionString = "jdbc:sqlite:" + DB_PATH;

    	try(Connection connection = DriverManager.getConnection(connectionString)){
    		Statement stmt = connection.createStatement();
    		String sql = "CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY AUTOINCREMENT , uniqueId TEXT, password TEXT);";
    		stmt.execute(sql);

    		sql = "INSERT INTO users(uniqueId, password) VALUES('" + uniqueId + "','" +  password + "');";
    		stmt.execute(sql);
    	}catch(SQLException e){
    		e.printStackTrace();
    	}
    }

    // Retrieve all Clients from the Database.
    public ConcurrentHashMap<String, String> bringUsers(){
    	String connectionString = "jdbc:sqlite:" + DB_PATH;
    	try(Connection connection = DriverManager.getConnection(connectionString)){
            System.out.println("Bringing Users!");
    		Statement stmt = connection.createStatement();
    		String sql = "CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY AUTOINCREMENT , uniqueId TEXT, password TEXT);";
    		stmt.execute(sql);
    		sql = "SELECT * FROM users;";
    		ConcurrentHashMap<String, String> users = new ConcurrentHashMap<>();
	    	ResultSet rs = stmt.executeQuery(sql);

    		while(rs.next()){
    			users.put(rs.getString("uniqueId").toString(),rs.getString("password").toString());	
                System.out.println("Added !");
			}
	    	return users;
    	}
    	catch(SQLException e){
    		e.printStackTrace();
    	}
    	return null;
    }
}
