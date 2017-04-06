import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * @authors Aditya Geria, Jeevana Lagisetty, Monisha Jain
 * @Version 2/20/2016 1:23 rc2
 * get.java
 * Retrieves all messages from a specified group from the server
 */
public class get {

	public static void main (String [] args) throws NumberFormatException, UnknownHostException, IOException {
		
		String host = "localhost";
		String port = "7979";
		String getclient = "get";
		String groupname = null;
		String status = null;
		String num = null;
		String message = null;
		
		Socket clientsocket = null;
		
		//parses the command line arguments for host, port and groupname
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
		
		System.out.println("Started GET client with port: " + port + " attempting to connect to host: " + host);
		try {
			clientsocket = new Socket(host, Integer.parseInt(port));
		}
		catch (Exception e) {
			System.out.println("Failed to initialize socket.");
			System.exit(1);
		}
		
		Scanner in = new Scanner(System.in);
		DataOutputStream outToServer = new DataOutputStream(clientsocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
		
		try {
			outToServer.writeBytes(getclient + '\n'); //let server know what type of client you are
		}
		catch (Exception e) {
			System.out.println("Write to server failed on line " + e.getStackTrace()[e.getStackTrace().length-1].getLineNumber());
			System.exit(1);
		}
		
		//System.out.println("Which group to retrieve messages from? ");
		//groupname = in.nextLine();

		if(groupname == null) {
			System.out.println("error: invalid group name."); //if groupname doesnt exist
		}
		else {
			//check if the groupname contains unprintable text or spaces
			if(groupname.contains(" ") || Character.isISOControl(groupname.charAt(0))) {
				System.out.println("error: invalid name.");
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
				status = inFromServer.readLine();
			}
			catch (Exception e) {
				System.out.println("Read from server failed on line " + e.getStackTrace()[e.getStackTrace().length-1].getLineNumber());
				System.exit(1);
			}
			
			System.out.println("Status: " + status); //read back from the server regarding status of group search
			if(status.contains("error")) { //if failed, exit the client
				System.exit(1);
			}
			
			try {
				num = inFromServer.readLine();
			}
			catch (Exception e) {
				System.out.println("Read from server failed on line " + e.getStackTrace()[e.getStackTrace().length-1].getLineNumber());
				System.exit(1);
			}
			
			System.out.println("Number: " + num);
			
			for(int i = 0; i < Integer.parseInt(num); i++) {
				try {
					message = inFromServer.readLine() + '\n' + inFromServer.readLine();
				}
				catch (Exception e) {
					System.out.println("Read from server failed on line " + e.getStackTrace()[e.getStackTrace().length-1].getLineNumber());
					System.exit(1);
				}
				System.out.println(message + '\n');
				//System.out.println();
			}
		}
			
		in.close();
		//clientsocket.close();
		
		
		
	}
	
	
}
