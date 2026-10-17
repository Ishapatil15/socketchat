import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

public class HandleMessages{
	Socket socket;
    HashMap<String, Socket> hm;
    HashMap<String, String> users;
    String uniqueId = "";
    public HandleMessages(Socket socket, HashMap<String, Socket> hm, HashMap<String, String> users, String uniqueId){
    	this.socket = socket;
        this.hm = hm;
        this.uniqueId = uniqueId;
        this.users = users;
    }

    // public boolean checkIfExists(String id){
    //     return hm.containsKey(id);
    // }

    // public int checkAndMessage(String parsedMsg , String id){
    //     System.out.println("Checking Again!");
    //     if(checkIfExists(id)){
    //         try{
    //             Socket socket = hm.get(id);
    //             PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
    //             ParseAndSend senderObj = new ParseAndSend(pw);
    //             senderObj.send(parsedMsg);
    //             return 200;
    //         }catch(IOException e){
    //             e.printStackTrace();
    //         }
    //     }
    //     return 404;
    // }
    
    public boolean handle(){
    	try{
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output;
            GetAndStringify extracter = new GetAndStringify(input);

            String parsedMsg = extracter.getParsedMsg();
            String id = extracter.extractId(parsedMsg);
            ParseAndSend senderObj = new ParseAndSend(new PrintWriter(socket.getOutputStream()));

            if(id.equalsIgnoreCase("server")){
                System.out.println("Socket Exited : " + uniqueId );
                output = new PrintWriter(socket.getOutputStream(), true);
                senderObj = new ParseAndSend(output);
                senderObj.parseAndSend("Server", uniqueId, "message", "exit");
                Socket socket = hm.remove(uniqueId);
                System.out.println("Removed : " + uniqueId);
                return true;
            }
            else if(hm.containsKey(id) && hm.get(id) != null){
                System.out.println("Message Recieved For : " + id);
                // output = new PrintWriter(socket.getOutputStream(), true);
                // output.println("200");
                // String msg = input.readLine();
                // if(!msg.equalsIgnoreCase("exit")){
                // System.out.println("Message Recieved!");
                Socket reciever = hm.get(id);
                output = new PrintWriter(reciever.getOutputStream(), true);
                senderObj = new ParseAndSend(output);
                senderObj.send(parsedMsg);
                System.out.println("Message Sent!");
            }
            else if(users.containsKey(id)){
                System.out.println("Message Recieved For : " + id);
                // System.out.println("Message Recieved!");
                new Thread(){
                    public void run(){
                        MessageManager manageMessages = new MessageManager();
                        manageMessages.storeOfflineMessage(parsedMsg, id);
                        // synchronized(hm){
                        //     try{
                        //         UnsentMessageManager manageUnsentMessages = new UnsentMessageManager();
                        //         int status = checkAndMessage(parsedMsg, id);
                        //         while(status != 200){
                        //             hm.wait();
                        //             status = checkAndMessage(parsedMsg, id);
                        //         }
                        //         System.out.println("Mesaage Sent Succesfullly!");
                        //         Files.delete(Path.of("./" + id + ".xml"));
                        //     }catch(InterruptedException e){
                        //         e.printStackTrace();
                        //     }
                            
                        // }
                    }
                }.start();
             }
            else{
                // output = new PrintWriter(socket.getOutputStream(), true);
                // output.println("404");
                System.out.println("No contact saved for this id : " + id);
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return false;
    }
}
