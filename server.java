import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.ArrayList;

/**
 * @authors Aditya Geria, Jeevana Lagisetty, Monisha Jain
 * @version 2/20/2016 1:24 rc2
 * server.java
 * Hosts a multithreaded server, using an optional command line argument for the port
 * GET and POST clients can connect to this server to post messages to a board,
 * create a new board, or retrieve all messages from a board.
 */
public class server {
	
	//an arraylist of groups is used to keep the size of the groups dynamic
	private static ArrayList<group> groups = new ArrayList<group>(); //arraylist of all groups
	
	public static void main (String [] args) throws IOException, InterruptedException {
		
		int port = 7979; //default port if not passed in via command line

		ServerSocket serversocket = null;
		
		for(int i = 0; i < args.length; i++) {
			if(args[0] != null) {
				port = Integer.parseInt(args[1]);
			}
		}
		
		//attempt to create a socket
		try {
			serversocket = new ServerSocket(port);
		}
		catch (Exception e) {
			System.out.println("Could not initialize socket for server.");
			serversocket.close();
			System.exit(1);
			e.printStackTrace();
		}
		server s = new server();
		
		while(true) {
			System.out.println("Waiting....");
			Socket clientsocket = serversocket.accept();
			//spawns a new thread for each new connected client
			ClientRunnable clientservice = s.new ClientRunnable(clientsocket);
			new Thread(clientservice).start();
			//Thread.sleep(1000);
		}
		
		
	}
	
	/** 
	 * @Method dumpGroup posts all messages from all groups
	 * ONLY USED FOR DEBUGGING - IGNORE IF USED OUTSIDE
	 * @param g - arraylist of groups to dump
	 */
	@SuppressWarnings("unused") 
	private static void dumpGroup(ArrayList<group> g) {
		
		int i;
		System.out.println("Dumping...");
		System.out.println("size of groups: " + g.size());
		
		for(i = 0; i < g.size(); i++) {
			ArrayList<message> temp = g.get(i).getMessages();
			System.out.println("Size of messages: " +  temp.size());
			for(int j = 0; j < temp.size(); j++) {
				System.out.println("From " + temp.get(j).gethostname() + 
						" " + temp.get(j).getaddr() + " " + 
						" " + temp.get(j).getDate() + '\n' + 
						temp.get(j).getMessage() + '\n');
			}
			
		}
		
	}
	
	/**
	 * @Method checkIfExists 
	 * Checks if a particular group (name) exists in an arraylist of groups
	 * returns -1 on unsuccessful search, index of group otherwise
	 * @param g - arraylist of groups to check from
	 * @param check - groupname to check for
	 */
	private synchronized static int checkIfExists (ArrayList<group> g, String check) {
		
		int i = 0;
		if(g.isEmpty()) return -1;
		
		for(i = 0; i < g.size(); i++) {
			group temp = g.get(i);
			if(temp.getgroupName().equals(check)) {
				return g.indexOf(temp);
			}
		}
		
		return -1;
	}
	
	//helper method to create a group with some abstraction
	private static group createGroup(String name) {
		
		ArrayList<message> msgsInGroup = new ArrayList<message>();
		group g = new group(name, 0, msgsInGroup);
		return g;
	}
	
	//helper method to create a message constructor with abstraction
	public static message createmessage(String address, String hostname, String msg, String date) {
		
		message m = new message(address, hostname, msg, date);
		
		return m;
	}
	
	/**
	 * @Method doPost posts a message from a POST client to a specified group
	 * If the group does not exist, then make the group and post it there.
	 * @param clientsocket socket used to establish connection to client
	 */
	public synchronized static void doPost(final Socket clientsocket) throws IOException {
		
		String clientMessage = null;
		String toGroup = null; //use with POST
		String address;
		String hostname = null;
		String date;
		
		BufferedReader inFromClient = null;
		try {
			inFromClient = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
		} catch (IOException e2) {
			System.out.println("BufferedReader initialization failed.");
			e2.printStackTrace();
			System.exit(1);
		}
		DataOutputStream outToClient = null;
		try {
			outToClient = new DataOutputStream(clientsocket.getOutputStream());
		} catch (IOException e2) {
			System.out.println("DataOutputStream initialization failed.");
			e2.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Post client identified"); 
		try {
			hostname = inFromClient.readLine();
		}
		catch (Exception e) {
			System.out.println("Read from client failed on line " + e.getStackTrace()[e.getStackTrace().length-1].getLineNumber());
			System.exit(1);
		}
		if(hostname.length() == 0 || Character.isISOControl(hostname.charAt(0))) {
			hostname = "Anonymous" + '\n'; //if hostname is missing or invalid, use default "Anonymous" name
		}
		
		try {
			toGroup = inFromClient.readLine();
		}
		catch (Exception e) {
			System.out.println("Read from client failed on line " + e.getStackTrace()[e.getStackTrace().length-1].getLineNumber());
			System.exit(1);
		}
		
		try {
			clientMessage = inFromClient.readLine();
		}
		catch (Exception e) {
			System.out.println("Read from client failed on line " + e.getStackTrace()[e.getStackTrace().length-1].getLineNumber());
			System.exit(1);
		}
		
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
		Date now = new Date();
		
		/*used for debugging purposes
		//ignore if used otherwise
		System.out.println("Read: " + clientMessage + " | To group: " + toGroup);
		System.out.println("From: " + clientsocket.getRemoteSocketAddress().toString());
		System.out.println("Hostname: " + hostname);
		System.out.println("On date: " + df.format(now));
		*/
		
		address = clientsocket.getRemoteSocketAddress().toString();
		//hostname = InetAddress.getLocalHost().getHostName();
		date = df.format(now);
		
		message m = createmessage(address, hostname, clientMessage, date);
		
		//attempt to fnid group in arraylist of groups
		int rval = checkIfExists(groups, toGroup);
		
		if((rval == -1)) {
			try {
				outToClient.writeBytes("Group not found, creating group\n");
			}
			catch (Exception e) {
				System.out.println("Write to client failed on line " + e.getStackTrace()[e.getStackTrace().length-1].getLineNumber());
				System.exit(1);
			}
			
			group t = createGroup(toGroup);
			groups.add(t);
			rval = groups.indexOf(t);
			groups.get(rval).addMessage(m);
		}
		//now we have the index of where the group exists
		else {
			try {
				outToClient.writeBytes("Group was found. Posting messaging." + '\n');
			}
			catch (Exception e) {
				System.out.println("Write to client failed on line " + e.getStackTrace()[e.getStackTrace().length-1].getLineNumber());
				System.exit(1);
			}
			//System.out.println("rval is: " + rval);
			groups.get(rval).addMessage(m);
		}
		//dumpGroup(groups);
	}
	
	/**
	 * @Method doGet performs the GET protocol from a GET client
	 * Retrieves all messages from a specified group by a GET client 
	 * and sends them to the client	
	 * @param clientsocket - used to establish connection to client to and send/read bytes
	 */
	public synchronized static void doGet (final Socket clientsocket) throws IOException {
		String fromGroup = null; //use with GET
		
		BufferedReader inFromClient = null;
		try {
			inFromClient = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
		} catch (IOException e2) {
			System.out.println("BufferedReader initialization failed.");
			System.exit(1);
			e2.printStackTrace();
		}
		DataOutputStream outToClient = new DataOutputStream(clientsocket.getOutputStream());
		
		try {
			fromGroup = inFromClient.readLine();
		}
		catch (Exception e) {
			System.out.println("Read from client failed on line " + e.getStackTrace()[e.getStackTrace().length-1].getLineNumber());
			System.exit(1);
		}
	
		System.out.println("Looking for group: " + fromGroup); //debugging
		
		//rval will become either -1 (doesnt exist) or the index of the group in the Arraylist
		int rval = checkIfExists(groups, fromGroup);
		
		if(rval == -1) {
			try {
				outToClient.writeBytes("error: invalid group name." + '\n');
			}
			catch (Exception e) {
				System.out.println("Write to client failed on line " + e.getStackTrace()[e.getStackTrace().length-1].getLineNumber());
				System.exit(1);
			}
		}
		//we have the index at this point, similar to doPost()
		else {
			try {
				outToClient.writeBytes("Group found. Dumping messages." + '\n');
			} 
			catch (Exception e) {
				System.out.println("Write to client failed on line " + e.getStackTrace()[e.getStackTrace().length-1].getLineNumber());
				System.exit(1);
			}

			int i = 0;
			ArrayList<message> temp = groups.get(rval).getMessages();
			try {
				//System.out.println("Size: " + String.valueOf(temp.size()));
				//let the client know how many messages there are in the group
				outToClient.writeBytes(String.valueOf(temp.size()) + '\n');
			}
			catch (Exception e) {
				System.out.println("Write to client failed on line " + e.getStackTrace()[e.getStackTrace().length-1].getLineNumber());
				System.exit(1);
			}
			
			//send all messages to client
			for(i = 0; i < temp.size(); i++) {
				try {
					outToClient.writeBytes("From " + temp.get(i).gethostname() + 
						" " + temp.get(i).getaddr() + " " + 
						" " + temp.get(i).getDate() + '\n' + 
						temp.get(i).getMessage() + '\n');
				}
				catch (Exception e) {
					System.out.println("Write to client failed on line " + e.getStackTrace()[e.getStackTrace().length-1].getLineNumber());
					System.exit(1);
				}
						
			}
		}
	}
	
	//thread function
	class ClientRunnable implements Runnable {

		private Socket clientsocket;
		public ClientRunnable (final Socket socket) {
			clientsocket = socket;
		}

		public void run() {
			String clienttype = null;
			BufferedReader inFromClient = null;
			try {
				inFromClient = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
			} catch (IOException e2) {
				System.out.println("BufferedReader initialization failed.");
				System.exit(1);
				e2.printStackTrace();
			}
			
			
			DataOutputStream outToClient = null;
			try {
				outToClient = new DataOutputStream(clientsocket.getOutputStream());
			} catch (IOException e2) {
				System.out.println("DataOutputStream initialization failed.");
				System.exit(1);
				e2.printStackTrace();
			}
			
			try {
				clienttype = inFromClient.readLine();
			} 
			catch (Exception e) {
				System.out.println("Read from client failed on line " + e.getStackTrace()[e.getStackTrace().length-1].getLineNumber());
				System.exit(1);
			}
			
			/*     checks the type of client that is connecting to it
			the first thing the client will write to the server is
			either "get" or "post"      */
			System.out.println("Client type: " + clienttype); //show which type of client connected
			if(clienttype.equals("post")){
				try {
					doPost(clientsocket);
				} catch (IOException e) {
					System.exit(1);
					e.printStackTrace();
				}
			}
			else if(clienttype.equals("get")) {
				try {
					doGet(clientsocket);
				} catch (IOException e) {
					System.exit(1);
					e.printStackTrace();
				}
			}
			else {
				try {
					outToClient.writeBytes("error: invalid command." + '\n');
				} 
				catch (Exception e) {
					System.out.println("Write to client failed on line " + e.getStackTrace()[e.getStackTrace().length-1].getLineNumber());
					System.exit(1);
				}
			}
		
		}
	}
	
}
