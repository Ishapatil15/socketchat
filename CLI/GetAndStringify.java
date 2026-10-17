import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetAndStringify {
	BufferedReader input;
    public GetAndStringify(BufferedReader input){
    	this.input = input;
    }
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

    public String extractId(String parsedMessage){
		String idRegex = "<to ([a-zA-Z0-9]+)>";
		Pattern pattern = Pattern.compile(idRegex);
		Matcher matcher = pattern.matcher(parsedMessage);
		matcher.find();
		return matcher.group(1);
    }

    public String extractFrom(String parsedMessage){
		String idRegex = "<from ([a-zA-Z0-9]+)>";
		Pattern pattern = Pattern.compile(idRegex);
		Matcher matcher = pattern.matcher(parsedMessage);
		matcher.find();
		return matcher.group(1);
    }

    public String getMessage(String parsedMessage){
    	String idRegex = "<message>([\\S ]+)<\\/message>";
		Pattern pattern = Pattern.compile(idRegex);
		Matcher matcher = pattern.matcher(parsedMessage);
		matcher.find();
		return matcher.group(1);
    }

    public String getSenderAndMessage(String parsedMessage){
		String idRegex = "<from ([a-zA-Z0-9]+)>\\s+<([a-z]+)>([\\S ]+)<\\/\\2>\\s+<\\/from>";
		Pattern pattern = Pattern.compile(idRegex);
		Matcher matcher = pattern.matcher(parsedMessage);
		matcher.find();
		return "From " + matcher.group(1) + ", Msg : " + matcher.group(3);
    }
}
