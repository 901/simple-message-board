Internet Technology
Assignment 3 
Aditya Geria, Jeevana Lagisetty, Monisha Jain

============================================
Section 1: Instructions for Execution     ||
============================================

-----------
SERVER    |
-----------
To execute the server, simply run the following command line code
	java server 
The default port used is 7979

Or, if you decide to add an optional port number on which clients can 
connect to the server, use:
	java server -p [PORT]

From here, the server will be waiting for clients to connect to it, and will
state which type of client has connected

-----------
POST      |
-----------
The post client can be ran simply by doing:
	java post [groupname]
WARNING: groupnames cannot contain spaces or non-printable text

You can also use the following optional parameters in any order
	java post -h [hostname] -p [port] [groupname]
WARNING: you must use the -h and -p to distinguish the host and the port, previous warnings apply

-----------
GET       |
-----------
The get client can be ran simply by doing:
	java get [groupname]
WARNING: groupnames cannot contain spaces or non-printable text

You can also use the following optional parameters in any order
	java get -h [hostname] -p [port] [groupname]
WARNING: you must use the -h and -p to distinguish the host and the port, previous warnings apply

============================================
Section 2: Algorithms and Data Structures ||
============================================

---------------
Data structure|
---------------
To store the groups and messages, and for purposes of simplicity, we have used an Arraylist 
data structure. Groups are stored in a global arraylist. A group constructor also contains an arraylist
of all the messages in that group (along with their relevant information). This way, data can be modified
with relative ease - such as add()/get() methods built in. However, this sacrifices the efficiency of the 
program (O(n^2) at worst case - when the arraylist has to resize)

---------------
Algorithm     |
---------------
The code runs by creating a new thread for each client that has connected to the server. From there, it retrieves info
from the client regarding the client's type - post/get/unknown - and goes to the appropriate method.
In the case of post, it reads the groupname from the client, and the clients hostname/IP. It then reads the message the 
client wishes to send. Then, it generates the current date/time instance, packages it into a message constructor and adds 
it to the group.
For get, a similar procedure is used. The groupname is read from the client, then the number of messages in the group is retrieved.
That number is sent to the client, which then reads a number of messages equal to that number from that group.

============================================
Section 3: Bugs, features and peculiarities|
============================================

- When the server does not have any information on any groups, and a get client attemps to retrieve messages from a group that does not exist,
the server will think the groupname is null, and the write call to the client will fail. We cannot explain why this is happening because the groupname is written fine if groups exist in the Arraylist.
- Because the doPost() and doGet() methods, which handle the backend of the client's request, are synchronized - when two of the same type of clients attempt to connect to the same server, the requests will be handled one at a time. This is beacuse the methods require a type of mutex
protection. If a new message is posted while another message is trying to be posted, it might cause a race condition. 
- checkIfExists() is also synchronized - this is because we want to avoid changes to the arraylist while we are checking its contents.
- if a client is run with the following sample command line arguments:
	java post -p 52000 hello world
  the program will accept it as valid, and treat the groupname as "world". However, if no groupname is provided in POST client, the 
  client will ask the user to input a valid groupname without spaces.
- if the server exits or crashes while the client is running, it will cause an exception in the clients. Exception/signal handlers in java do 
  not work as expected and we would need further testing with them.
- small blocks of code have been left in for debugging purposes. Most are commented out, but any extraneous information shown is strictly for   
  debugging. For example, entering a groupname manually - because eclipse command line is hard to use.
- if the client(s) cannot provide a valid computer name, the server sets the default name to be "Anonymous" and continues. We feel that instead of breaking the client connection
simply because they cannot provide a name is ineffective - and a default name is much more preferable

============================================
Section 4: Test Cases		          ||
============================================
For testing purposes, all tests are carried out with default port and host
Test case 1: Simple test case 
	Scenario: 1 server, 1 POST client, 1 GET client
	Execution: Server is running, post client connects - requesting to post to group "cs352". POST client posts the message: "Did anyone 			   start studying?". Server creates the group and posts the message. GET client connects, and requests messages from group 
		   "cs352". The message is accurately shown along with the hostname/IP/date of the poster.
	
Test case 2: Invalid cases
	Scenario: 1 server, unknown client
	Execution: Server is running, unknown client connects to server. Server accepts connection, states "error: invalid command" to client.
	
	Scenario: 1 POST client/GET client
	Execution: client attempts to connect to server that is not running. client states "could not initialize socket" and exits.
	
	Scenario: 1 server, 1 POST client
	Execution: Server is running, post client does not include group name in command line. Post client is prompted to enter a valid 
		       groupname in a do-while until a valid name is entered. That name is carried to server and the doPost() protocol is carried 
                   out.

	Scenario: 1 server, 1 POST client
	Execution: Server is running, post client attempts to send a message with length 0. Message is rejected and the client exits. (This is 	
		   because buffered reader accepts hitting enter as a blank string of size 0).
	
	Scenario: 1 server, 1 GET client
	Execution: Server is running, GET client requests messages from a group that does not exist. Server writes "error: invalid group name" 
                   to client and the client exits upon being returned such a status.

	
Test case 3: Multiple clients
	Scenario: 1 server, 2 POST clients
	Execution: Server is running, first post client attempts to post a message. Second post client also attempts to post a message while 
		   while the first is posting. The second post client is forced to wait until the message from the first client is posted. Then 		   the message from the second client is posted successfully.

	Scenario: 1 server, 1 POST client, 1 GET client
	Execution: Server is running, a POST client attempts to post a message while a GET client attempts to request messages from the same
                   group at the same time the POST protocol would happen. (Assuming other groups exist). The request which reached the server
                   first would execute first while the latter would hold until the first protocol finished execution. This is due to the 
                   synchronized nature of doGet() and doPost().

---------------------------
Runtime example           |
---------------------------

Case: 1 server, 1 POST client, 1 GET client - post client posts a message and the get client retrieves it.

Server: 
	bash-4.1$ java server -p 52000 
	Waiting....
	Waiting....
	Client type: post
	Post client identified
	Waiting....
	Client type: get
	Looking for group: test

	(still running)

POST:
	bash-4.1$ java post -p 52000 test
	Started POST client with port: 52000 attempting to connect to host: localhost
	Please enter your message:
	hello, how are you?
	Server returned: Group not found, creating group
	(finish)	

GET:
	bash-4.1$ java get -p 52000 test
	Started GET client with port: 52000 attempting to connect to host: localhost
	Status: Group found. Dumping messages.
	From aag177 /127.0.0.1:39364  Friday, February 19, 2016 5:04:02 PM EST
	hello, how are you?

	(finish)

	
