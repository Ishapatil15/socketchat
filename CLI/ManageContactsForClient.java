import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;
import java.util.HashSet;

public class ManageContactsForClient {
	Socket socket ;
	HashSet<String> userIds;
	BufferedReader input;
    public ManageContactsForClient(BufferedReader input){
    	this.input = input;
    	this.userIds = new HashSet<>();
    	getContacts();
    }
    public ManageContactsForClient(Socket socket, Set<String> userIds){
    	this.socket = socket;
    	this.userIds = new HashSet(userIds);
    	sendContacts();
    }
    public void sendContacts(){
    	try{
    		PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
    		for(String userId : userIds){
		    	output.println(userId);
		    }
		    output.println("All Contacts Sent!");
		    System.out.println("All users Sent!");
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    }

    public void getContacts(){
    	String userId;
    	try{
    		while(!("All Contacts Sent!").equalsIgnoreCase((userId = input.readLine()))){
    			userIds.add(userId);
    		}
    		System.out.println("Got all the users!");
    	}
    	catch(IOException e){
    		e.printStackTrace();
    	}
    }
}
