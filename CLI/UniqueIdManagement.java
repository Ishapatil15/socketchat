

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class UniqueIdManagement {
    public String uniqueId;
    public UniqueIdManagement(){
    	uniqueId = "";
    }
    public String configureId(BufferedReader br, BufferedReader input, PrintWriter output){
    	try{
    		System.out.println("Enter a Unqiue Id For your Contact: (Enter Phone Number To reduce System work!)");
            String response = "";
            uniqueId = br.readLine();
            output.println(uniqueId);
    		while(!(response = input.readLine()).equalsIgnoreCase("Handshaking Completed!")){
                System.out.println(response);
                uniqueId = br.readLine();
                output.println(uniqueId);
            }
            System.out.println("Response : " + (response = input.readLine()));
		    return uniqueId;
        }
        catch(IOException e){
        	e.printStackTrace();
        }
        return null;
    }
}
