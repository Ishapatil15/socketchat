import java.io.IOException;
import java.io.PrintWriter;

public class ParseAndSend {
	PrintWriter output;
    MessageManager manageMessages;
    public ParseAndSend(PrintWriter output){
    	this.output = output;
        manageMessages = new MessageManager();
    }
    public ParseAndSend(){ }

    public String parse(String senderId, String recieverId,  String type, String msg){
    	String text = "<to " + recieverId + 
    					">\n\t" + "<from " + senderId + 
    					">\n\t\t<"+ type + ">" + msg + "</" + type + 
    					">\n\t</from>"+
    					"\n</to>"; 
    	return text;
    }

    public void parseAndSend(String senderId, String recieverId, String type, String msg){
		String parsedMsg = parse(senderId, recieverId, type, msg);
		send(parsedMsg);
        if(!msg.equalsIgnoreCase("exit")) manageMessages.storeMessage(parsedMsg, senderId);
    }
    public void send(String parsedMsg){
        output.println(parsedMsg);
    }
}
