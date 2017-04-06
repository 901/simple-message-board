import java.util.ArrayList;

/**
 * @authors Aditya Geria, Jeevana Lagisetty, Monisha Jain
 * @version 2/20/2016 1:25 rc1
 * group.java
 * Group constructor which stores the number of messages in a group,
 * all the messages in the group (in an Arraylist) as well the groupname
 */
public class group {
	
	private String groupname;
	private int numMessages;
	private ArrayList<message> msgs;
	
	public group(String name, int n, ArrayList<message> messages) {
		groupname = name;
		numMessages = n;
		msgs = messages;
	}
	
	public int getnumMessages() {
		return numMessages;
	}
	
	public String getgroupName () {
		return groupname;
	}
	
	public ArrayList<message> getMessages() {
		return msgs;
	}
	
	public void setGroupName (String name) {
		groupname = name;
		return;
	}
	
	public void addMessage(message msg) {
		msgs.add(msg);
		numMessages++;
		return;
	}
	
	
	
}
