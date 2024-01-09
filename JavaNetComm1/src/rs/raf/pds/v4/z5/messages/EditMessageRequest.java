package rs.raf.pds.v4.z5.messages;

public class EditMessageRequest {
	private String roomName;
    private int messageId;
    private String newContent;
    
    public EditMessageRequest() {}
    public EditMessageRequest(String roomName, int messageId, String newContent) {
    	this.roomName=roomName;
    	this.messageId=messageId;
    	this.newContent=newContent;
    }
    public String getRoomName() {
    	return roomName;
    }
    public int getMessageId() {
    	return messageId;
    }
    public String getNewContent() {
    	return newContent;
    }

}
