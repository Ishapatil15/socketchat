package ClientHelpers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiConsumer;

import InputHelpers.GetAndStringify;
import SharedHelpers.MessageManager;

// Component to read past chat between users.
public class ReadOlderMessages {
    MessageManager manageMessages = new MessageManager();
    // Brings and puts the messages in the chat section. 
	public void bringMessages(String userId, String clientID, BiConsumer<String, Boolean> addMessage, Runnable addDivider){
    	try{
            String line = "";
            StringBuffer parsedMsg = new StringBuffer("");
            
            // For older Chats that have been read.
            if(Files.exists(Path.of("./" + userId + ".xml"))){
                BufferedReader br = new BufferedReader(new FileReader("./"+userId+".xml"));
                // Make a message in the while loop, from the xml file, and add it in chat section.
        		while((line = br.readLine()) != null){
        			if(!line.equalsIgnoreCase("</to>")){
        				parsedMsg.append(line + "\n");
        			}else{
        				parsedMsg.append("</to>" + "\n");
        				GetAndStringify deParser = new GetAndStringify();
        				addMessage.accept(deParser.getMessage(parsedMsg.toString()), !deParser.extractFrom(parsedMsg.toString()).equalsIgnoreCase(userId));
        				parsedMsg = new StringBuffer("");
        			}
        		}
            }

            // For Chats that are unread by the Client.
    	    if(Files.exists(Path.of("./" + clientID + "Unread.xml"))){
                // Divider to distinguish between read and unread messages.
                addDivider.run();
                BufferedReader br = new BufferedReader(new FileReader("./" + clientID + "Unread.xml"));
                // Make a message in the while loop, from the unread xml file, and add it in chat section.
        		while((line = br.readLine()) != null){
        			if(!line.equalsIgnoreCase("</to>")){
        				parsedMsg.append(line + "\n");
        			}else{
        				parsedMsg.append("</to>" + "\n");
        				GetAndStringify deParser = new GetAndStringify();
        				addMessage.accept(deParser.getMessage(parsedMsg.toString()), !deParser.extractFrom(parsedMsg.toString()).equalsIgnoreCase(userId));
        				manageMessages.storeMessage(parsedMsg.toString(), userId);
                        parsedMsg = new StringBuffer("");
        			}
        		}
                br.close();
                Files.delete(Path.of("./" + clientID + "Unread.xml"));
            }
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    	
    }
}
