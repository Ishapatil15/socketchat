package SharedHelpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ManageContactsForClient {

	Socket socket ;
	HashSet<String> userIds;
	BufferedReader input;

    // For Client Side(Recieve all Contacts).
    public ManageContactsForClient(BufferedReader input){
    	this.input = input;
    	this.userIds = new HashSet<>();
    }

    // For Server Side(Send all Contacts).
    public ManageContactsForClient(Socket socket, Set<String> userIds){
    	this.socket = socket;
    	this.userIds = new HashSet<>(userIds);
    	sendContacts();
    }

    // Server's Helper Function.
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

    // Client's Helper Function.
    public HashSet<String> getContacts(){
    	String userId;
    	try{
            while(!("All Contacts Sent!").equalsIgnoreCase((userId = input.readLine()))){
                    userIds.add(userId);
            }
            System.out.println("Got all the users!");
            return userIds;
    	}
    	catch(IOException e){
            e.printStackTrace();
    	}
        return null;
    }
}
