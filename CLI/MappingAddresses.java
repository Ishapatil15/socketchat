import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class MappingAddresses implements Runnable{
    Thread t;
    Socket socket;
    HashMap<String, Socket> hm;
   	String uniqueId = "";
   	String password = "";
   	HashMap<String, String> users;
    public MappingAddresses(Socket socket, HashMap<String, Socket> hm, HashMap<String, String> users){
    	this.socket = socket;
    	this.hm = hm;
    	t = new Thread(this);
    	t.start();
    	this.users = users;
    }
    public void run(){
		try{
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
			String tempUniqueId = input.readLine();
			Socket address = hm.putIfAbsent(tempUniqueId ,socket);
			while(address !=  null){
				output.println("Enter Different Username! This Username is Already Taken!(You Can Use Phone Number Instead!)");
				tempUniqueId = input.readLine();
				address = hm.putIfAbsent(tempUniqueId ,socket);
			}
			this.uniqueId = tempUniqueId;
			if(users.containsKey(uniqueId)){
				output.println("Enter Password :");
				while(!users.get(uniqueId).equalsIgnoreCase(password = input.readLine())){
					output.println("Wrong password!");
				}
				output.println("HandShaking Completed!");
			    output.println("Successfully Logged In As : " + uniqueId);
			    System.out.println("User Logged In As : " + uniqueId);
			    synchronized(hm){
			    	hm.notifyAll();
			    }
			}
			else{
				output.println("Create A Password : ");
				password = input.readLine();
				users.put(uniqueId, password);
				String confirmPassword = password;
				new Thread(){
					public void run(){
						(new StoreUsers()).storeUser(uniqueId, confirmPassword);
					}
				}.start();
				System.out.println(users);
				output.println("HandShaking Completed!");
			    output.println("Successfully Created Contact For : " + uniqueId);
			    System.out.println("Created New Contact For : " + uniqueId);
			}
			ManageContactsForClient manageContacts = new ManageContactsForClient(socket, users.keySet());
			new Thread(){
				public void run(){
					try{
						if(Files.exists(Path.of("./" + uniqueId + "Unread.xml"))){
							MessageManager manageMessages = new MessageManager();
							PrintWriter printer = new PrintWriter(socket.getOutputStream(), true);
							ParseAndSend parser = new ParseAndSend(printer);
							manageMessages.getUnreadMessages(uniqueId, parser);
						}
					}catch(IOException e){
						e.printStackTrace();
					}
				}
			}.start();
            HandleMessages handleMessagesObj = new HandleMessages(socket, hm, users ,uniqueId);
			while(true){
				boolean exitLoop = handleMessagesObj.handle();
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
