import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class StoreUsers {
	private static final String DB_PATH ="users.db";

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

    public HashMap<String, String> bringUsers(){
    	String connectionString = "jdbc:sqlite:" + DB_PATH;
    	try(Connection connection = DriverManager.getConnection(connectionString)){
            System.out.println("Bringing Users!");
    		Statement stmt = connection.createStatement();
    		String sql = "CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY AUTOINCREMENT , uniqueId TEXT, password TEXT);";
    		stmt.execute(sql);
    		sql = "SELECT * FROM users;";
    		HashMap<String, String> users = new HashMap<>();
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

    // public String getId(String msg){
    // 	String idRegex = "<uniqueId>([A-Za-z0-9]+)<\\/uniqueId>";
    // 	Pattern pattern = Pattern.compile(idRegex);
    // 	Matcher matcher = pattern.matcher(msg);
    // 	matcher.find();
    // 	return matcher.group(1);
    // }

    // public String getPassword(String msg){
    // 	String idRegex = "<password>([A-Za-z0-9]+)<\\/password>";
    // 	Pattern pattern = Pattern.compile(idRegex);
    // 	Matcher matcher = pattern.matcher(msg);
    // 	matcher.find();
    // 	return matcher.group(1);
    // }
}
