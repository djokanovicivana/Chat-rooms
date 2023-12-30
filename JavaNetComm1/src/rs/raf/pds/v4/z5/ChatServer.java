package rs.raf.pds.v4.z5;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import rs.raf.pds.v4.z5.messages.ChatMessage;
import rs.raf.pds.v4.z5.messages.ChatRoom;
import rs.raf.pds.v4.z5.messages.InfoMessage;
import rs.raf.pds.v4.z5.messages.InviteUserRequest;
import rs.raf.pds.v4.z5.messages.JoinRoomRequest;
import rs.raf.pds.v4.z5.messages.KryoUtil;
import rs.raf.pds.v4.z5.messages.ListRooms;
import rs.raf.pds.v4.z5.messages.ListRoomsRequest;
import rs.raf.pds.v4.z5.messages.ListUsers;
import rs.raf.pds.v4.z5.messages.Login;
import rs.raf.pds.v4.z5.messages.PrivateMessage;
import rs.raf.pds.v4.z5.messages.WhoRequest;


public class ChatServer implements Runnable{

	private volatile Thread thread = null;
	
	volatile boolean running = false;
	final Server server;
	final int portNumber;
	ConcurrentMap<String, Connection> userConnectionMap = new ConcurrentHashMap<String, Connection>();
	ConcurrentMap<Connection, String> connectionUserMap = new ConcurrentHashMap<Connection, String>();
	private ArrayList<ChatRoom> chatRooms=new ArrayList<>();
	
	public ChatServer(int portNumber) {
		this.server = new Server();
		
		this.portNumber = portNumber;
		KryoUtil.registerKryoClasses(server.getKryo());
		registerListener();
	}
	private void registerListener() {
		server.addListener(new Listener() {
			public void received (Connection connection, Object object) {
				if (object instanceof Login) {
					Login login = (Login)object;
					newUserLogged(login, connection);
					connection.sendTCP(new InfoMessage("Hello "+login.getUserName()));
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return;
				}
				
				if (object instanceof ChatMessage) {
					ChatMessage chatMessage = (ChatMessage)object;
					System.out.println(chatMessage.getUser()+":"+chatMessage.getTxt());
					broadcastChatMessage(chatMessage, connection); 
					return;
				}

				if (object instanceof WhoRequest) {
					ListUsers listUsers = new ListUsers(getAllUsers());
					connection.sendTCP(listUsers);
					return;
				}
				if (object instanceof PrivateMessage) {
	                PrivateMessage privateMessage = (PrivateMessage) object;
	                System.out.println(privateMessage.getSender()+":"+privateMessage.getMessage());
	                newPrivateMessage(privateMessage, connection);
	                return;
	            }
				if(object instanceof ChatRoom) {
					ChatRoom chatRoom=(ChatRoom)object;
					System.out.println("Kreirana je soba "+chatRoom.getName());
					createChatRoom(chatRoom, connection);
					return;
				}
				if(object instanceof ListRoomsRequest) {
					ListRooms listRooms=new ListRooms(listAllRooms());
					connection.sendTCP(listRooms);
					return;
				}
				if(object instanceof InviteUserRequest) {
					InviteUserRequest inviteUserRequest=(InviteUserRequest) object;
					System.out.println("Dodat je korisnik "+inviteUserRequest.getUser()+ " u sobu "+inviteUserRequest.getRoom());
					addUser(inviteUserRequest.getRoom(),inviteUserRequest.getUser(), connection);
				}
				if(object instanceof JoinRoomRequest) {
					JoinRoomRequest joinRoomRequest=(JoinRoomRequest) object;
					joinRoom(joinRoomRequest.getRoom(), connection);
		            return;
				}
			}
			
			public void disconnected(Connection connection) {
				String user = connectionUserMap.get(connection);
				connectionUserMap.remove(connection);
				userConnectionMap.remove(user);
				showTextToAll(user+" has disconnected!", connection);
			}
		});
	}
	
	String[] getAllUsers() {
		String[] users = new String[userConnectionMap.size()];
		int i=0;
		for (String user: userConnectionMap.keySet()) {
			users[i] = user;
			i++;
		}
		
		return users;
	}
	ArrayList<String> listAllRooms() {
		ArrayList<String> roomList=new ArrayList<>();
		for(ChatRoom chatRoom:chatRooms) {
			roomList.add(chatRoom.getName());
		}
		return roomList;
		
	}
	void newUserLogged(Login loginMessage, Connection conn) {
		userConnectionMap.put(loginMessage.getUserName(), conn);
		connectionUserMap.put(conn, loginMessage.getUserName());
		showTextToAll("User "+loginMessage.getUserName()+" has connected!", conn);
	}
	private void broadcastChatMessage(ChatMessage message, Connection exception) {
		for (Connection conn: userConnectionMap.values()) {
			if (conn.isConnected() && conn != exception)
				conn.sendTCP(message);
		}
	}
	private void showTextToAll(String txt, Connection exception) {
		System.out.println(txt);
		for (Connection conn: userConnectionMap.values()) {
			if (conn.isConnected() && conn != exception)
				conn.sendTCP(new InfoMessage(txt));
		}
	}
	private void newPrivateMessage(PrivateMessage privateMessage, Connection senderConnection) {
        String senderUsername = connectionUserMap.get(senderConnection);
       
        String recipientUsername = privateMessage.getRecipient();
        String messageText = privateMessage.getMessage();

        Connection recipientConnection = userConnectionMap.get(recipientUsername);

        if (recipientConnection != null && recipientConnection.isConnected()) {
            recipientConnection.sendTCP(new PrivateMessage(senderUsername, recipientUsername, messageText));
            recipientConnection.sendTCP(new InfoMessage("Primili ste privatnu poruku od "+senderUsername +", sa sadrzajem "+" " +messageText));
            
 
            senderConnection.sendTCP(new InfoMessage("Vasa privatna poruka namenjena  " + recipientUsername + " je poslata."));
        } else {
            senderConnection.sendTCP(new InfoMessage("Korisnik " + recipientUsername + " nije trenutno online."));
        }
    }
	private void createChatRoom(ChatRoom chatRoom, Connection con) {
		for(ChatRoom room:chatRooms) {
			if(room.getName()==chatRoom.getName()) {
				con.sendTCP(new InfoMessage("Soba sa imenom " +chatRoom.getName()+ " vec postoji!"));
				return;
			}}
			chatRooms.add(chatRoom);
			con.sendTCP(new InfoMessage("Uspesno ste kreirali sobu sa imenom "+ chatRoom.getName()));
			
	}
	public void listAllRooms(Connection con) {
		String roomList="";
		for(ChatRoom chatRoom:chatRooms) {
			roomList+=chatRoom.getName()+", ";
		}
		con.sendTCP(new InfoMessage("Lista dostupnih soba: "+ roomList));
	}
	public void addUser(String roomName, String userName, Connection con) {
		ChatRoom chatRoom = null;
		Connection userConnection=userConnectionMap.get(userName);
		for(ChatRoom room:chatRooms) {
			if(room.getName().equals(roomName)) {
				chatRoom=room;
			}
		}
		if(chatRoom==null) {
			con.sendTCP(new InfoMessage("Zadata soba ne postoji!"));
		}
		else {
		  chatRoom.addUser(userConnection);
		  userConnection.sendTCP(new InfoMessage("Dodati ste u sobu "+chatRoom.getName()));
		  con.sendTCP(new InfoMessage("Uspesno ste dodali korisnika "+ userName+" u sobu "+roomName));
		}
	}
	public void joinRoom(String roomName, Connection con) {
	    ChatRoom chatRoom = null;
	    for (ChatRoom room : chatRooms) {
	        if (room.getName().equals(roomName)) {
	            chatRoom = room;
	        }
	    }

	    if (chatRoom == null) {
	        con.sendTCP(new InfoMessage("Soba sa imenom " + roomName + " ne postoji!"));
	    } else {
	        Connection userConnection = userConnectionMap.get(connectionUserMap.get(con));
	        
	        boolean userInRoom = false;
	        for (Connection user : chatRoom.getUsers()) {
	            if (user == userConnection) {
	                userInRoom = true;
	                break;
	            }
	        }

	        if (userInRoom) {
	            con.sendTCP(new InfoMessage("Vec ste u sobi " + roomName));
	            
	        } else {
	            chatRoom.addUser(userConnection);
	            con.sendTCP(new InfoMessage("Uspesno ste se pridruzili sobi " + roomName));
	        }
	    }
	}
private ArrayList<ChatMessage> getLastMessagesForRoom(String roomName) {	
	ChatRoom chatRoom = null;
    for (ChatRoom room : chatRooms) {
        if (room.getName().equals(roomName)) {
            chatRoom = room;
        }
    }
	    return chatRoom.getLastMessages(5);

	}

	public void start() throws IOException {
		server.start();
		server.bind(portNumber);
		
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
	@Override
	public void run() {
		running = true;
		
		while(running) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
public static void main(String[] args) {
		
		if (args.length != 1) {
	        System.err.println("Usage: java -jar chatServer.jar <port number>");
	        System.out.println("Recommended port number is 54555");
	        System.exit(1);
	   }
	    
	   int portNumber = Integer.parseInt(args[0]);
	   try { 
		   ChatServer chatServer = new ChatServer(portNumber);
	   	   chatServer.start();
	   
			chatServer.thread.join();
	   } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	   } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	   }
	}
	
	
   
   
}
