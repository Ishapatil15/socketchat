package OutputHelpers;

import SharedHelpers.MessageManager;

import java.io.PrintWriter;

// For any Component to parse any message or send any message. 
public class ParseAndCreateMsg {

	PrintWriter output;
    MessageManager manageMessages;

    // For only parsing the messages.
    public ParseAndCreateMsg(){ }

    public String createMsg(String senderId, String recieverId,  String type, String msg){
    	String text = "<to " + recieverId + 
    					">\n\t" + "<from " + senderId + 
    					">\n\t\t<"+ type + ">" + msg + "</" + type + 
    					">\n\t</from>"+
    					"\n</to>"; 
    	return text;
    }

    // Add another variable in the parsedMessage such as 'Status'.
    public String addVariableInMsg(String parsedMessage, String variable, String value){
        int index = parsedMessage.indexOf("</from>");
        String newString = parsedMessage.substring(0, index) + "\t<" + variable + ">" + value + "</" + variable + ">\n\t" + parsedMessage.substring(index); 
        return newString;
    }

    public String removeVariablesFromMsg(String parsedMessage){
        int messageIndex = parsedMessage.indexOf("</message>");
        int fromIndex = parsedMessage.indexOf("</from>");
        String newString = parsedMessage.substring(0, messageIndex + 10) + "\n\t" + parsedMessage.substring(fromIndex);
        return newString;
    }
}
