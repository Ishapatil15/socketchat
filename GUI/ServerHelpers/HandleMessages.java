package ServerHelpers;

import InputHelpers.GetAndStringify;
import InputHelpers.HandleStatus;
import OutputHelpers.ParseAndCreateMsg;
import OutputHelpers.SendMessages;
import SharedHelpers.MessageManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class HandleMessages{
	Socket socket;
    String uniqueId = "";
    
    ConcurrentHashMap<String, Socket> hm;
    ConcurrentHashMap<String, String> users;

    BufferedReader input;
    PrintWriter output;

    // Server's Component to handle messages for each Client.
    public HandleMessages(BufferedReader input, PrintWriter output, ConcurrentHashMap<String, Socket> hm, ConcurrentHashMap<String, String> users, String uniqueId){
    	this.input = input;
        this.output = output;
        this.hm = hm;
        this.uniqueId = uniqueId;
        this.users = users;
    }
    
    public boolean handle(){
    	try{
            // Creating an extracter that will extract data required from the XML format.
            GetAndStringify extracter = new GetAndStringify(input);

            // Status Codes Handler.
            HandleStatus handleStatus = new HandleStatus(extracter);

            MessageManager manageMessages = new MessageManager();

            // Extracting data from the message recieved.
            String parsedMsg = extracter.getParsedMsg();
            String id = extracter.extractId(parsedMsg);
            String msg = "";
            if(!extracter.checkIfStatus(parsedMsg))
                msg = extracter.getMessage(parsedMsg);

            SendMessages senderObj;

            // If Client wants to log out / exit the system.
            if(id.equalsIgnoreCase("server")){
                // Creating a Sender that can parse into XML and can send the data.
                senderObj = new SendMessages(output, "server");
                System.out.println("Socket Exiting : " + uniqueId );

                // Keep receiving messages until all messages are recieved from the Client before Client goes offline.
                while(true){
                    parsedMsg = extracter.getParsedMsg();
                    // Check if it's a status or a message.
                    if(extracter.checkIfStatus(parsedMsg)){
                        if(handleStatus.handleExitStatus(parsedMsg)){
                            // Returns true only when all messages are sent.
                            break;
                        }
                    }else{
                        manageMessages.storeOfflineMessage(parsedMsg, extracter.extractId(parsedMsg));
                    }
                }

                // Server reverts to the Client as acknowledgement of exit message. 
                senderObj.sendMessage(uniqueId, "exit");


                // Remove Client from the ConcurrentHashMap(Stores online users!).
                Socket socket = hm.remove(uniqueId);
                System.out.println("Removed : " + uniqueId);

                // Return true for exitLoop in MappingAddresses.
                return true;
            }
            else if(hm.containsKey(id) && hm.get(id) != null){
                // Creating a Sender that can parse into XML and can send the data.
                ParseAndCreateMsg parseAndCreateMsg = new ParseAndCreateMsg();
                // If the user with uniqueId : id , is online.
                System.out.println("Message Received For : " + id);

                // Changing the socket to be of Receiver Client and sending the message.
                Socket reciever = hm.get(id);
                output = new PrintWriter(reciever.getOutputStream(), true);
                senderObj = new SendMessages(output, uniqueId);
                senderObj.sendMessage(id, parsedMsg);
                System.out.println("Message Sent!");


                // Sending the success status back to the Client.
                Socket sender = hm.get(uniqueId);
                output = new PrintWriter(reciever.getOutputStream(), true);
                senderObj = new SendMessages(output, uniqueId);
                String parsedStatusAndMessage = parseAndCreateMsg.addVariableInMsg(parsedMsg, "status", "200");
                senderObj.sendMessage(uniqueId, parsedStatusAndMessage);

            }
            else if(users.containsKey(id)){
                // If the user with uniqueId : id , is registered but not online.
                System.out.println("Message Recieved For : " + id);

                // Sending the fallback status back to the Client.
                senderObj = new SendMessages(output, users.get(id));
                ParseAndCreateMsg parseAndCreateMsg = new ParseAndCreateMsg();
                String parsedStatusAndMessage = parseAndCreateMsg.addVariableInMsg(parsedMsg, "status", "300");
                senderObj.sendMessage(uniqueId, parsedStatusAndMessage);
            }
            else{
                // In GUI , this block will never occur.
                
                // If no such user / Client exist.
                System.out.println("No contact saved for this id : " + id);
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        // Returning false to keep looping for more messages.
        return false;
    }
}
