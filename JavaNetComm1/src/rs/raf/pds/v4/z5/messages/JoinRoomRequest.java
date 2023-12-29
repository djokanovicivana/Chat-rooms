package rs.raf.pds.v4.z5.messages;

public class JoinRoomRequest {
	String roomName;
	
	public JoinRoomRequest() {
		
	}
	public JoinRoomRequest(String roomName) {
		this.roomName=roomName;
	}
	public String getRoom() {
		return roomName;
	}

}
