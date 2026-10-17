

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class MessageManager {
 	
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
 	public String getMessages(String id, ParseAndSend parser){
 		try{
 			if(Files.exists(Path.of("./" + id + ".xml"))){
	 			BufferedReader br = new BufferedReader(new FileReader("./" + id + ".xml"));
	 			String msg = "";
	 			StringBuffer messages = new StringBuffer();
	 			while((msg = br.readLine())!= null){
	 				messages.append(msg);
	 			}
	 			br.close();
	 			return messages.toString();
	 		}
 		}
 		catch(IOException e){
 			e.printStackTrace();
 		}
 		return null;
 	}
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
 	public void getUnreadMessages(String id, ParseAndSend parser) {
 		try{
 			if(Files.exists(Path.of("./" + id + "Unread.xml"))){
	 			BufferedReader br = new BufferedReader(new FileReader("./" + id + "Unread.xml"));
	 			String msg = "";
	 			while((msg = br.readLine())!= null){
	 				parser.send(msg);
	 			}
	 			br.close();
	 			Files.delete(Path.of("./" + id + "Unread.xml"));
	 		}
 		}
 		catch(IOException e){
 			e.printStackTrace();
 		}
 	}
}