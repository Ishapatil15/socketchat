package InputHelpers;

import OutputHelpers.ParseAndCreateMsg;
import SharedHelpers.MessageManager;

// To store the data at the sender side Client.
public class HandleStatus {

	GetAndStringify extracter;
    
    public HandleStatus(GetAndStringify extracter){
    	this.extracter = extracter;
    }

    public void handleStatus(String parsedMessage){
    	MessageManager manageMessages = new MessageManager();
		ParseAndCreateMsg parser = new ParseAndCreateMsg();
    	
    	String status = extracter.getStatus(parsedMessage);
        String onlyParsedMessage = parser.removeVariablesFromMsg(parsedMessage);

    	if(status.equalsIgnoreCase("200")){
        	manageMessages.storeMessage(onlyParsedMessage, extracter.extractId(onlyParsedMessage));
    	}else{
    		manageMessages.storeOfflineMessage(onlyParsedMessage, extracter.extractId(onlyParsedMessage));
    	}
    }

    public boolean handleExitStatus(String parsedMessage){
        MessageManager manageMessages = new MessageManager();
        String status = extracter.getStatus(parsedMessage);
        if(status.equalsIgnoreCase("101")){
            return false;
        }
        return true;
    }
}