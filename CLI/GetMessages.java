public class GetMessages{
    GetAndStringify extracter;
    String msg = "";
    public GetMessages(GetAndStringify extracter){
    	this.extracter = extracter;
    }
    public boolean getMessage(){
        String parsedMessage = extracter.getParsedMsg();
        msg = extracter.getMessage(parsedMessage);
        String senderAndMsg = extracter.getSenderAndMessage(parsedMessage);
        if(msg.equalsIgnoreCase("exit")){
            return true;
        }
        // if(!msg.equalsIgnoreCase("404") && !msg.equalsIgnoreCase("200")){

        MessageManager manageMessages = new MessageManager();
        manageMessages.storeMessage(parsedMessage, extracter.extractId(parsedMessage));
        System.out.println(senderAndMsg);
        // }
        return false;
    }

}
