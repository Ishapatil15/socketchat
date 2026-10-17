package SharedHelpers;

import OutputHelpers.SendMessages;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class MessageManager {
 	
 	// Store for Client to read these messages later.
 	public void storeMessage(String msg, String id){
 		try{
 			if(Files.exists(Path.of("./" + id + ".xml"))){
	 			Files.writeString(Path.of("./" + id + ".xml"), "\n" + msg, StandardOpenOption.APPEND);
	 		}else{
	 			Files.createFile(Path.of("./" + id + ".xml"));
	 			Files.writeString(Path.of("./" + id + ".xml"), msg);
	 		}
 		}catch(IOException e){
 			e.printStackTrace();
 		}
 	}

 	// Send messages that are unread by the Client to Server / Server to Client.
 	public void sendUnreadMessages(String id, SendMessages sender) {
 		try{
 			if(Files.exists(Path.of("./" + id + "Unread.xml"))){
	 			BufferedReader br = new BufferedReader(new FileReader("./" + id + "Unread.xml"));
	 			String msg = "";
	 			StringBuilder parsedMsg = new StringBuilder();
	 			while((msg = br.readLine())!= null){

	 				parsedMsg.append(msg + "\n");
	 				if(msg.equalsIgnoreCase("</to>")){
	 					sender.sendParsedMsg(parsedMsg.toString());
	 					parsedMsg = new StringBuilder();
	 				}
	 			}
	 			br.close();
	 			Files.delete(Path.of("./" + id + "Unread.xml"));
	 		}
 		}
 		catch(IOException e){
 			e.printStackTrace();
 		}
 	}

 	// Store Messages while user is offline.
 	public void storeOfflineMessage(String msg, String id){
 		try{
 			if(Files.exists(Path.of("./" + id + "Unread.xml"))){
	 			Files.writeString(Path.of("./" + id + "Unread.xml"),"\n" + msg, StandardOpenOption.APPEND);
	 		}else{
	 			Files.createFile(Path.of("./" + id + "Unread.xml"));
	 			Files.writeString(Path.of("./" + id + "Unread.xml"), msg);
	 		}
 		}catch(IOException e){
 			e.printStackTrace();
 		}
 	}
}