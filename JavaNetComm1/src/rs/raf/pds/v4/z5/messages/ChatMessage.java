package rs.raf.pds.v4.z5.messages;

public class ChatMessage {
	String user;
	String txt;
	String room; 
	
	protected ChatMessage() {
		
	}
	public ChatMessage(String user, String txt) {
		this.user = user;
		this.txt = txt;
	}
	public ChatMessage(String user, String txt, String room) {
		this.user = user;
		this.txt = txt;
		this.room=room;
	}

	public String getUser() {
		return user;
	}

	public String getTxt() {
		return txt;
	}
	public String getRoom() {
		return room;
	}
	public void setTxt(String txt) {
		this.txt=txt;
	}
	
	
}
