import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class SendMessages implements Runnable{
	Thread t;
	PrintWriter output;
	BufferedReader br;
    BufferedReader input;
    ParseAndSend obj;
	String msg = "";
	String id = "";
    String sender = "";
    public SendMessages(BufferedReader input, ParseAndSend obj, BufferedReader br, String uniqueId){
    	this.obj = obj;
    	this.br = br;
        this.input = input;
        this.sender = uniqueId;
    	t = new Thread(this);
    	t.start();
    }

    public void run(){
        try{
            id = br.readLine();
            if(id.equalsIgnoreCase("exit")){
                obj.parseAndSend(sender, "server", "message", "exit");
                return;
            }
            msg = br.readLine();
            obj.parseAndSend(sender, id, "message", msg);
            // String status = input.readLine();
            // if(status.equalsIgnoreCase("404")){
            // }
            // else{
                // System.out.println("No Person Found For this id : " + id );
            // }
            new SendMessages(input, obj, br, sender);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}