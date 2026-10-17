package ServerHelpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import OutputHelpers.ParseAndCreateMsg;
import OutputHelpers.SendMessages;
import SharedHelpers.ManageContactsForClient;
import SharedHelpers.MessageManager;
import DbManager.UsersManagement;

public class MappingAddresses implements Runnable{
    Thread t;
    Socket socket;

   	String uniqueId = "";
   	String password = "";

    ConcurrentHashMap<String, Socket> hm;
   	ConcurrentHashMap<String, String> users;

    public MappingAddresses(Socket socket, ConcurrentHashMap<String, Socket> hm, ConcurrentHashMap<String, String> users){
    	this.socket = socket;
    	this.hm = hm;
    	this.users = users;

    	// Initializing new Thread for the Client's Connection with Server.
    	t = new Thread(this);
    	t.start();
    }

    public void run(){
		try{
			// Connection where Server will be the sender, and Client will be the reciever.
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			// Connection where Server can receive status and messages from Client.
			PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

			// Accept UniqueId and check for availability of it.
			String tempUniqueId = input.readLine();
			Socket address = hm.putIfAbsent(tempUniqueId ,socket);

			// Loop until a certain uniqueId entered by the Client is available.
			while(address !=  null){
				output.println("Enter Different Username! This Username is Already Taken!(You Can Use Phone Number as unique ID's!)");
				tempUniqueId = input.readLine();
				address = hm.putIfAbsent(tempUniqueId ,socket);
			}

			this.uniqueId = tempUniqueId;

			// If any user with that uniqueId ever registered in the system.
			if(users.containsKey(uniqueId)){
				output.println("Enter Password :");
				while(!users.get(uniqueId).equalsIgnoreCase(password = input.readLine())){
					output.println("Wrong password!");
				}

				// Send status for successful login. 
				output.println("HandShaking Completed!");
			    output.println("Successfully Logged In As : " + uniqueId);
			    
			    System.out.println("User Logged In As : " + uniqueId);
			    synchronized(hm){
			    	hm.notifyAll();
			    }
			}
			else{
				// Create / Register a new Client.
				output.println("Create A Password : ");
				password = input.readLine();
				users.put(uniqueId, password);
				String confirmPassword = password;
				new Thread(){
					public void run(){
						(new UsersManagement()).storeUser(uniqueId, confirmPassword);
					}
				}.start();

				// Send status for successful register.
				output.println("HandShaking Completed!");
			    output.println("Successfully Created Contact For : " + uniqueId);

			    System.out.println("Created New Contact For : " + uniqueId);
			}

			// Send the unique ID for every other user to this client.
			ManageContactsForClient manageContacts = new ManageContactsForClient(socket, users.keySet());

			// Send if any unsent Messages are there for the joined Client on the server.
			MessageManager manageMessages = new MessageManager();
            SendMessages senderObj = new SendMessages(output, "server");
			manageMessages.sendUnreadMessages(uniqueId, senderObj);

			while(true){
				// Now this Thread on the server will handle all message transmission for this Client.
	            HandleMessages handleMessagesObj = new HandleMessages(input, output, hm, users ,uniqueId);

				// Loop FOREVER to handle as much messages as the Client sends.
				boolean exitLoop = handleMessagesObj.handle();

				// If Client logs out.
				if(exitLoop){
					break;
				}
            }
		}
		catch(IOException e){
			e.printStackTrace();
		}
    }
}
