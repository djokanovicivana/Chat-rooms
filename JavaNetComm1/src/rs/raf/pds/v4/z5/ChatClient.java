package rs.raf.pds.v4.z5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.function.Consumer;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import rs.raf.pds.v4.z5.messages.ChatMessage;
import rs.raf.pds.v4.z5.messages.ChatRoom;
import rs.raf.pds.v4.z5.messages.EditMessageRequest;
import rs.raf.pds.v4.z5.messages.GetMoreMessagesRequest;
import rs.raf.pds.v4.z5.messages.InfoMessage;
import rs.raf.pds.v4.z5.messages.InviteUserRequest;
import rs.raf.pds.v4.z5.messages.JoinRoomRequest;
import rs.raf.pds.v4.z5.messages.KryoUtil;
import rs.raf.pds.v4.z5.messages.ListMessages;
import rs.raf.pds.v4.z5.messages.ListRooms;
import rs.raf.pds.v4.z5.messages.ListRoomsRequest;
import rs.raf.pds.v4.z5.messages.ListUsers;
import rs.raf.pds.v4.z5.messages.Login;
import rs.raf.pds.v4.z5.messages.PrivateMessage;
import rs.raf.pds.v4.z5.messages.WhoRequest;

public class ChatClient implements Runnable{

	public static int DEFAULT_CLIENT_READ_BUFFER_SIZE = 1000000;
	public static int DEFAULT_CLIENT_WRITE_BUFFER_SIZE = 1000000;
	
	private volatile Thread thread = null;
	
	volatile boolean running = false;
	
	final Client client;
	final String hostName;
	final int portNumber;
	final String userName;
	private String currentRoom;
	//private Consumer<String> messageListener;

	
	public ChatClient(String hostName, int portNumber, String userName) {
		this.client = new Client(DEFAULT_CLIENT_WRITE_BUFFER_SIZE, DEFAULT_CLIENT_READ_BUFFER_SIZE);
		
		this.hostName = hostName;
		this.portNumber = portNumber;
		this.userName = userName;
		KryoUtil.registerKryoClasses(client.getKryo());
		registerListener();
	}
	private void registerListener() {
		client.addListener(new Listener() {
			public void connected (Connection connection) {
				Login loginMessage = new Login(userName);
				client.sendTCP(loginMessage);
			}
			
			public void received (Connection connection, Object object) {
				if (object instanceof ChatMessage) {
					ChatMessage chatMessage = (ChatMessage)object;
					showChatMessage(chatMessage);
					return;
				}

				if (object instanceof ListUsers) {
					ListUsers listUsers = (ListUsers)object;
					showOnlineUsers(listUsers.getUsers());
					return;
				}
				
				if (object instanceof InfoMessage) {
					InfoMessage message = (InfoMessage)object;
					showMessage("Server:"+message.getTxt());
					return;
				}
				
				if (object instanceof ChatMessage) {
					ChatMessage message = (ChatMessage)object;
					showMessage(message.getUser()+"r:"+message.getTxt());
					return;
				}
				if (object instanceof PrivateMessage) {
	                PrivateMessage privateMessage = (PrivateMessage) object;
	               if(privateMessage.getRecipient()==userName) {
	                showPrivateMessage(privateMessage);
	                }
	                return;
	            }
				if (object instanceof ChatRoom) {
					ChatRoom chatRoom=(ChatRoom) object;
					createChatRoom(chatRoom.getName());
					return;
					
				}
				if(object instanceof ListRooms) {
					ListRooms listRooms=(ListRooms)object;
					listAllRooms(listRooms.getRooms());
					return;
				}
				if(object instanceof ListMessages) {
					ListMessages listMessages=(ListMessages)object;
					listMoreMessages(listMessages.getMessages());
					return;
				}
				
				
			}
			
			public void disconnected(Connection connection) {
				
			}
		});
	}
	private void showChatMessage(ChatMessage chatMessage) {
		System.out.println(chatMessage.getUser()+":"+chatMessage.getTxt());
	}
	private void showMessage(String txt) {
		System.out.println(txt);
	}
	private void showPrivateMessage(PrivateMessage privateMessage) {
		System.out.println("Primiliste privatnu poruku od  "+privateMessage.getSender()+ ", sa sadrzajem: "+ privateMessage.getMessage());
		
	}
	private void sendPrivateMessage(String recipient, String txt) {
	    PrivateMessage privateMessage = new PrivateMessage(userName, recipient, txt);
	    client.sendTCP(privateMessage);
	}
	private void createChatRoom(String roomName) {
		ChatRoom chatRoom=new ChatRoom(roomName);
		client.sendTCP(chatRoom);
	}
	private void showOnlineUsers(String[] users) {
		System.out.print("Server:");
		for (int i=0; i<users.length; i++) {
			String user = users[i];
			System.out.print(user);
			System.out.printf((i==users.length-1?"\n":", "));
		}
	}
	private void listAllRooms(ArrayList<String> rooms) {
		System.out.print("Server:");
		for(String room:rooms) {
			System.out.println(room);
		}
	}
	private void listMoreMessages(ArrayList<ChatMessage> messages) {
		System.out.print("Server:");
		for(ChatMessage message:messages) {
			System.out.println(message.getTxt());
		}
	}
	private void inviteUser(String room, String user) {
		 InviteUserRequest inviteUserRequest = new InviteUserRequest(room, user);
		 client.sendTCP(inviteUserRequest);
	}
	private void joinRoom(String room) {
		JoinRoomRequest joinRoomRequest=new JoinRoomRequest(room);
		client.sendTCP(joinRoomRequest);
		 currentRoom = room;
	}
	private void editMessage(String roomName, int id, String newContent) {
		EditMessageRequest editMessageRequest=new EditMessageRequest(roomName, id,newContent);
		client.sendTCP(editMessageRequest);
	}
	
	public void start() throws IOException {
		client.start();
		connect();
		
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}
	public void stop() {
		Thread stopThread = thread;
		thread = null;
		running = false;
		if (stopThread != null)
			stopThread.interrupt();
	}
	
	public void connect() throws IOException {
		client.connect(1000, hostName, portNumber);
	}
	/*public void run() {
		
		try (
				BufferedReader stdIn = new BufferedReader(
	                    new InputStreamReader(System.in))	// Za čitanje sa standardnog ulaza - tastature!
	        ) {
					            
				String userInput;
				running = true;
				
	            while (running) {
	            	userInput = stdIn.readLine();
	            	if (userInput == null || "BYE".equalsIgnoreCase(userInput)) // userInput - tekst koji je unet sa tastature!
	            	{
	            		running = false;
	            	}
	            	else if ("WHO".equalsIgnoreCase(userInput)){
	            		client.sendTCP(new WhoRequest());
	            	}
	            	else if (userInput.startsWith("PRIVATE")) {
	                    // Format: /PRIVATE @recipient_username @message
	                    String[] text = userInput.split(" ", 3);
	                    if (text.length == 3) {
	                        sendPrivateMessage(text[1], text[2]);
	                    } else {
	                        System.out.println("Format za slanje privatne poruke nije ispravan!");
	                    }}
	            	else if(userInput.startsWith("CREATE")) {
	            		String[] text=userInput.split(" ",2);
	            		if(text.length==2) {
	            			createChatRoom(text[1]);
	            		}else {
	            			System.out.println("Format za kreiranje nove sobe nije ispravan!");
	            		}
	            		
	            	}
	            	else if(userInput.startsWith("INVITE")) {
	            		String[] text=userInput.split(" ",3);
	            		if(text.length==3) {
	            			inviteUser(text[1], text[2]);
	            		}else {
	            			System.out.println("Format za dodavanje korisnika u sobu nije ispravan!");
	            		}
	            		
	            	}
	            	else if(userInput.startsWith("JOIN")) {
	            		String[] text=userInput.split(" ",2);
	            		if(text.length==2) {
	            			joinRoom(text[1]);
	            		}else {
	            			System.out.println("Format za pridruzivanje sobi nije ispravan!");
	            		}
	            		
	            	}
	            	else if("LIST ROOMS".equalsIgnoreCase(userInput)) {
	            		client.sendTCP(new ListRoomsRequest());
	            	}
	            	else if("GET MORE MESSAGES".equalsIgnoreCase(userInput)) {
	            		if(currentRoom==null) {
		            		ChatMessage message = new ChatMessage(userName, userInput);
		            		client.sendTCP(message);
		            }else {
	            		client.sendTCP(new GetMoreMessagesRequest());}
	            	}
	            	else {
	            		if(currentRoom==null) {
	            		ChatMessage message = new ChatMessage(userName, userInput);
	            		client.sendTCP(message);
	            		}
	            		else {
	            			ChatMessage message = new ChatMessage(userName, userInput, currentRoom);
	            			client.sendTCP(message);
	            		}
	            		
	            	}
	            	
	            	if (!client.isConnected() && running)
	            		connect();
	            	
	           }
	            
	    } catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			running = false;
			System.out.println("CLIENT SE DISCONNECTUJE");
			client.close();;
		}
	}*/
	
	public void run() {
	    try (BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {
	        String userInput;
	        running = true;

	        while (running) {
	            userInput = stdIn.readLine();
	            if (userInput == null || "BYE".equalsIgnoreCase(userInput)) {
	                running = false;
	            } else {
	                handleUserInput(userInput);
	            }

	            if (!client.isConnected() && running)
	                connect();
	        }

	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        running = false;
	        System.out.println("CLIENT SE DISCONNECTUJE");
	        client.close();
	    }
	}

	public void handleUserInput(String userInput) {
		 if ("WHO".equalsIgnoreCase(userInput)){
     		client.sendTCP(new WhoRequest());
     	}
     	else if (userInput.startsWith("PRIVATE")) {
             // Format: /PRIVATE @recipient_username @message
             String[] text = userInput.split(" ", 3);
             if (text.length == 3) {
                 sendPrivateMessage(text[1], text[2]);
             } else {
                 System.out.println("Format za slanje privatne poruke nije ispravan!");
             }}
     	else if(userInput.startsWith("CREATE")) {
     		String[] text=userInput.split(" ",2);
     		if(text.length==2) {
     			createChatRoom(text[1]);
     		}else {
     			System.out.println("Format za kreiranje nove sobe nije ispravan!");
     		}
     		
     	}
     	else if(userInput.startsWith("INVITE")) {
     		String[] text=userInput.split(" ",3);
     		if(text.length==3) {
     			inviteUser(text[1], text[2]);
     		}else {
     			System.out.println("Format za dodavanje korisnika u sobu nije ispravan!");
     		}
     		
     	}
     	else if(userInput.startsWith("JOIN")) {
     		String[] text=userInput.split(" ",2);
     		if(text.length==2) {
     			joinRoom(text[1]);
     		}else {
     			System.out.println("Format za pridruzivanje sobi nije ispravan!");
     		}
     		
     	}
    	else if(userInput.startsWith("EDIT")) {
     		String[] text=userInput.split(" ",3);
     		if(text.length==2) {
     			editMessage(currentRoom,Integer.parseInt(text[1]),text[2]);
     		}else {
     			System.out.println("Format za editovanje poruke nije ispravan!");
     		}
     		
     	}
  
     	else if("LIST ROOMS".equalsIgnoreCase(userInput)) {
     		client.sendTCP(new ListRoomsRequest());
     	}
     	else if("GET MORE MESSAGES".equalsIgnoreCase(userInput)) {
     		if(currentRoom==null) {
         		ChatMessage message = new ChatMessage(userName, userInput);
         		client.sendTCP(message);
         }else {
     		client.sendTCP(new GetMoreMessagesRequest(currentRoom));}     	}
     	else {
     		if(currentRoom==null) {
     		ChatMessage message = new ChatMessage(userName, userInput);
     		client.sendTCP(message);
     		}
     		else {
     			ChatMessage message = new ChatMessage(userName, userInput, currentRoom);
     			client.sendTCP(message);
     		}
     		
     	}
	}

	public static void main(String[] args) {
		if (args.length != 3) {
		
            System.err.println(
                "Usage: java -jar chatClient.jar <host name> <port number> <username>");
            System.out.println("Recommended port number is 54555");
            System.exit(1);
        }
 
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        String userName = args[2];
        
        try{
        	ChatClient chatClient = new ChatClient(hostName, portNumber, userName);
        	chatClient.start();
        }catch(IOException e) {
        	e.printStackTrace();
        	System.err.println("Error:"+e.getMessage());
        	System.exit(-1);
        }
	}
}
