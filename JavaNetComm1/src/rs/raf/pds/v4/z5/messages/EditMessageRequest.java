package rs.raf.pds.v4.z5.messages;

public class EditMessageRequest {
	public String roomName;
    public int messageId;
    public String newContent;
    
    public EditMessageRequest() {}
    public EditMessageRequest(String roomName, int messageId, String newContent) {
    	this.roomName=roomName;
    	this.messageId=messageId;
    	this.newContent=newContent;
    }

}
