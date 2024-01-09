package rs.raf.pds.v4.z5.messages;

public class ReplyMessageRequest {
	
	public String roomName;
    public int messageId;
    public String response;
    
    public ReplyMessageRequest() {
    	
    }
    public ReplyMessageRequest(String roomName, int messageId,String response) {
    	this.roomName=roomName;
    	this.messageId=messageId;
    	this.response=response;
    }
    public String getRoomName() {
    	return roomName;
    }
    public int getMessageId() {
    	return messageId;
    }
    public String getResponse() {
    	return response;
    }

}
