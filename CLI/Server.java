import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class Server {
    public static void main(String[] args) {
    	try{
    		ServerSocket server = new ServerSocket(8765);
    		HashMap<String, Socket> hm = new HashMap<>(); 
            HashMap<String, String> users = new HashMap<>();
                // new Thread(){
                //     public void run(){                        
                        users = (new StoreUsers()).bringUsers();
                        System.out.println(users);
                //     }
                // }.start();
    		while(true){
    			Socket socket = server.accept();
    			System.out.println("Found a connection : " + socket.getLocalSocketAddress());
    			new MappingAddresses(socket, hm, users);
    		}
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    }
}
