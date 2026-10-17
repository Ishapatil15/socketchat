package InputHelpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetAndStringify {

	BufferedReader input;
    
    // For any component that wants to read msgs and return extracted data.
    public GetAndStringify(BufferedReader input){
    	this.input = input;
    }
    
    // For any component that just wants to extract data from messages.
    public GetAndStringify(){ }

    public String getParsedMsg(){
    	StringBuffer createParsedMsg = new StringBuffer();
    	String msg;
    	try{
    		while(!(msg = input.readLine()).equalsIgnoreCase("</to>")){
    			createParsedMsg.append(msg+"\n");
    		}
    		createParsedMsg.append(msg);
    		return createParsedMsg.toString();
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    	return null;
    }

    // Extract reciever.
    public String extractId(String parsedMessage){
		String idRegex = "<to ([a-zA-Z0-9]+)>";
		Pattern pattern = Pattern.compile(idRegex);
		Matcher matcher = pattern.matcher(parsedMessage);
		matcher.find();
		return matcher.group(1);
    }

    // Extract Sender.
    public String extractFrom(String parsedMessage){
		String idRegex = "<from ([a-zA-Z0-9]+)>";
		Pattern pattern = Pattern.compile(idRegex);
		Matcher matcher = pattern.matcher(parsedMessage);
		matcher.find();
		return matcher.group(1);
    }

    // Extract Main Message.
    public String getMessage(String parsedMessage){
    	String idRegex = "<message>([\\S ]+)<\\/message>";
		Pattern pattern = Pattern.compile(idRegex);
		Matcher matcher = pattern.matcher(parsedMessage);
		matcher.find();
		return matcher.group(1);
    }

    // Extract sender and message.
    public String getSenderAndMessage(String parsedMessage){
		String idRegex = "<from ([a-zA-Z0-9]+)>\\s+<([a-z]+)>([\\S ]+)";
		Pattern pattern = Pattern.compile(idRegex);
		Matcher matcher = pattern.matcher(parsedMessage);
		matcher.find();
		return "From " + matcher.group(1) + ", Msg : " + matcher.group(3);
    }

    // Extract status from the message.
    public String getStatus(String parsedMessage){
    	String statusRegex = "<status>([0-9]+)<\\/status>";
    	Pattern pattern = Pattern.compile(statusRegex);
    	Matcher matcher = pattern.matcher(parsedMessage);
    	matcher.find();
    	return matcher.group(1);
    }

    // Check if the given parsedMessage includes status.
    public boolean checkIfStatus(String parsedMessage){
    	String statusRegex = "<status>";
    	Pattern pattern = Pattern.compile(statusRegex);
    	Matcher matcher = pattern.matcher(parsedMessage);
    	return matcher.find();
    }
}
