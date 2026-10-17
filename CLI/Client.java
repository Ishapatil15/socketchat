

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
    	try{
    		Socket socket = new Socket("127.0.0.1", 8765);
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
    		String uniqueId = new UniqueIdManagement().configureId(br, input, output);
            new ManageContactsForClient(input);
            ParseAndSend parser = new ParseAndSend(output);
            new SendMessages(input ,parser, br, uniqueId);
            GetAndStringify extracter = new GetAndStringify(input);
            GetMessages getMessagesObj = new GetMessages(extracter);
            while(true){
                boolean exitLoop = getMessagesObj.getMessage();
                if(exitLoop){
                    break;
                }
            }
            System.exit(0);
    	}catch(ConnectException e){
            System.out.println("Server is not running yet!");
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
