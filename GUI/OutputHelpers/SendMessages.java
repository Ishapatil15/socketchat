package OutputHelpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

// For Any Component to Send Messages.
public class SendMessages{

    PrintWriter output;
    String sender = "";
    ParseAndCreateMsg obj;

    public SendMessages(PrintWriter output, String uniqueId){
    	this.output = output;
        this.sender = uniqueId;
        this.obj = new ParseAndCreateMsg();
    }

    public void sendMessage(String reciever, String msg){
        new Thread(() -> {
            if(msg.equalsIgnoreCase("exit")){
                // If Client wants to log out.
                String parsedMsg = obj.createMsg(sender, "server", "message", "exit");
                output.println(parsedMsg);
                return;
            }
            String parsedMsg = obj.createMsg(sender, reciever, "message", msg);
            output.println(parsedMsg);

        }).start();
    }

    public void sendParsedMsg(String msg){
        new Thread(() -> {
            output.println(msg);
        });
    }

}