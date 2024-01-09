package rs.raf.pds.v4.z5.messages;

public class ReplyMessageRequest {
	
	public String roomName;
    public int messageId;
    public String replyContent;
    
    public ReplyMessageRequest() {
    	
    }
    public ReplyMessageRequest(String roomName, int messageId,String replyContent) {
    	this.roomName=roomName;
    	this.messageId=messageId;
    	this.replyContent=replyContent;
    }

}
