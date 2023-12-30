package rs.raf.pds.v4.z5.messages;

import java.util.ArrayList;

public class ListMessages {
	private ArrayList<ChatMessage> messages;
	
	public ListMessages() {}
	
	public ListMessages(ArrayList<ChatMessage> messages) {
		this.messages=messages;
	}
	
	public ArrayList<ChatMessage> getMessages() {
		return messages;
	}

}
