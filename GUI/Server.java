import DbManager.UsersManagement;
import ServerHelpers.MappingAddresses;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    public static void main(String[] args) {
    	try(ServerSocket server = new ServerSocket(8765)){
    		ConcurrentHashMap<String, Socket> hm = new ConcurrentHashMap<>();
            ConcurrentHashMap<String, String> users;

            // Brings all the users from the database to the RAM.
            users = (new UsersManagement()).bringUsers();

        	while(true){
                // Loops FOREVER for other clients to join the system.
    			Socket socket = server.accept();
    			System.out.println("Found a connection : " + socket.getLocalSocketAddress());

                // A new Thread starts for EVERY client and hence starts the process of mapping it's address.
    			new MappingAddresses(socket, hm, users);
    		}
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    }
}
