package rs.raf.pds.v4.z5.messages;

import java.util.ArrayList;

import com.esotericsoftware.kryonet.Connection;

public class ChatRoom {
	private String roomName;
	private ArrayList<Connection> roomUsers;
	private ArrayList<ChatMessage> roomMessages;
	
	public ChatRoom() {
		
	}
	 public ChatRoom(String roomName) {
	        this.roomName = roomName;
	        this.roomUsers = new ArrayList<>();
	        this.roomMessages = new ArrayList<>();
	    }

	    public String getName() {
	        return roomName;
	    }

	    public ArrayList<Connection> getUsers() {
	        return roomUsers;
	    }

	    public ArrayList<ChatMessage> getMessages() {
	        return roomMessages;
	    }

	    public void addUser(Connection user) {
	    	roomUsers.add(user);
	    }

}
