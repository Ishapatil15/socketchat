package InputHelpers;

import SharedHelpers.MessageManager;

import java.util.function.BiConsumer;

public class GetMessages{

    String msg = "";
    GetAndStringify extracter;
    BiConsumer<String, Boolean> addMessage;

    public GetMessages(GetAndStringify extracter, BiConsumer<String, Boolean> addMessage){
    	this.extracter = extracter;
        this.addMessage = addMessage;
    }

    // Components Helper to get messages.
    public boolean getMessage(){
        String parsedMessage = extracter.getParsedMsg();
        if(extracter.checkIfStatus(parsedMessage)){
            // When parsedMessage has a status.
            (new HandleStatus(extracter)).handleStatus(parsedMessage);
        }
        else{
            // When parsedMessage has a message.
            msg = extracter.getMessage(parsedMessage);

            String from = extracter.extractFrom(parsedMessage);

            // If server sends back exit request for the Client.
            if(from.equalsIgnoreCase("server")){
                return true;
            }

            // Adding the chat in the chatBox.
            addMessage.accept(msg, false);

            // To store the message at the recieving side Client.
            MessageManager manageMessages = new MessageManager();
            manageMessages.storeMessage(parsedMessage, extracter.extractFrom(parsedMessage));
            
            String senderAndMsg = extracter.getSenderAndMessage(parsedMessage);
            System.out.println(senderAndMsg);
        }
        return false;
    }
}
