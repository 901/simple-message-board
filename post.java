import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * @authors Aditya Geria, Jeevana Lagisetty, Monisha Jain
 * @version 2/20/2016 1:24 rc2
 * post.java
 * Acts as a POST function for server.java (same package)
 * Posts a user-inputted message to a user-specified groupname 
 * on the server
 */
public class post {
	
	public static void main (String [] args) throws NumberFormatException, UnknownHostException, IOException {
		
		String host = "localhost";
		String port = "7979";
		String postclient = "post";
		String groupname = null;
		String message;
		String back = null;
		
		Socket clientsocket = null;
		
		// ONLY FOR COMMAND LINE USE - use command line directives if given
		for(int i = 0; i < args.length; i++) {
			if(args[i].trim().startsWith("-h")) {
				host = args[i+1];
				i++;
			}
			if(args[i].trim().startsWith("-p")) {
				port = args[i+1];
				i++;
			}
			else
				groupname = args[i];
		}
		
		Scanner in = new Scanner(System.in);
		
		System.out.println("Started POST client with port: " + port + " attempting to connect to host: " + host);
		
		try {
			clientsocket = new Socket(host, Integer.parseInt(port));
		}
		catch (Exception e) {
			System.out.println("Could not initialize socket.");
			System.exit(1);
			//e.printStackTrace();
		}
		
		DataOutputStream outToServer = new DataOutputStream(clientsocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
		
		try {
			outToServer.writeBytes(postclient + '\n'); //tell the server what kind of client you are
		}
		catch (Exception e) {
			System.out.println("Write to server failed on line " + e.getStackTrace()[e.getStackTrace().length-1].getLineNumber());
			System.exit(1);
		}
		System.out.println("Please enter your message:");
		message = in.nextLine();
		
		//additional check - message cannot be empty (hit enter)
		if(message.length() == 0) {
			System.out.println("error: invalid message length (empty)");
			System.exit(1);
		}
		
		//if the groupname is not included in the command line
		//manually enter it
		if(groupname == null) {
			do {
				System.out.println("Where should this message go? (Must not contain spaces) (Group name)");
				groupname = in.nextLine();
				if(groupname.contains(" ") || Character.isISOControl(groupname.charAt(0))) {
					System.out.println("error: invalid name.");
					System.out.println("Try again."); //given leniency
				}
			} while(groupname.contains(" ") || Character.isISOControl(groupname.charAt(0)));
		}
		
		//check for invalid groupnames and for if its printable text
		if(groupname.contains(" ") || Character.isISOControl(groupname.charAt(0))) {
			System.out.println("error: invalid name.");
			System.exit(1);
		}
		
		//passes necessary info to the server
		try {
			outToServer.writeBytes(System.getProperty("user.name")+ '\n');
		}
		catch (Exception e) {
			System.out.println("Write to server failed on line " + e.getStackTrace()[e.getStackTrace().length-1].getLineNumber());
			System.exit(1);
		}
		
		try {
			outToServer.writeBytes(groupname + '\n');
		}
		catch (Exception e) {
			System.out.println("Write to server failed on line " + e.getStackTrace()[e.getStackTrace().length-1].getLineNumber());
			System.exit(1);
		}
		
		try {
			outToServer.writeBytes(message + '\n');
		}
		catch (Exception e) {
			System.out.println("Write to server failed on line " + e.getStackTrace()[e.getStackTrace().length-1].getLineNumber());
			System.exit(1);
		}
		
		try {
			back = inFromServer.readLine(); //reads back from the server for anything
		}
		catch (Exception e) {
			System.out.println("Read from server failed on line " + e.getStackTrace()[e.getStackTrace().length-1].getLineNumber());
			System.exit(1);
		}
		
		System.out.println("Server returned: " + back);
		
		clientsocket.close();
		in.close();
		
		
	}
	
}
