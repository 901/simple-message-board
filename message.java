/**
 * @authors Aditya Geria, Jeevana Lagisetty, Monisha Jain
 * @version 2/20/2016 1:25 rc1
 * message.java
 * message constructor which stores all the aspects of a message
 * A message is to be put inside a group with a group constructor's
 * group.addMessage(message m) method.
 * Stores the IP address of the poster, their computers hostname, the 
 * message date and the actual message.
 */

public class message {
	private String address;
	private String hostname;
	private String message;
	private String date;

	public message(String addr, String host, String msg, String dt) {
		address = addr;
		hostname = host;
		message = msg;
		date = dt;
	}
	
	public String getaddr() {
		return address;
	}
	
	public String gethostname() {
		return hostname;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setAddress (String ip) {
		address = ip;
		return;
	}
	
	public void setHostname (String host) {
		hostname = host;
		return;
	}
	
	public void setMessage (String msg) {
		message = msg;
		return;
	}
	
	public void setDate (String d) {
		date = d;
		return;
	}
	
	
}
