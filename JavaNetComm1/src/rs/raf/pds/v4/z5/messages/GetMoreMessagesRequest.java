package rs.raf.pds.v4.z5.messages;

public class GetMoreMessagesRequest {
	private String roomName;
	
	public GetMoreMessagesRequest() {
		
	}
	public GetMoreMessagesRequest(String roomName) {
		this.roomName=roomName;
	}
	public String getRoomName() {
		return roomName;
	}

}
