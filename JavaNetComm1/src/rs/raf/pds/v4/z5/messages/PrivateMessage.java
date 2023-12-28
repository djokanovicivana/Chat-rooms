package rs.raf.pds.v4.z5.messages;

public class PrivateMessage {
	
	 private String recipient;
	 private String sender;
	 private String message;
	 
	  public PrivateMessage() {
	      
	    }

	    public PrivateMessage(String sender, String recipient, String message) {
	        this.sender=sender;
	        this.message=message;
	        this.recipient = recipient;
	    }

	    public String getRecipient() {
	        return recipient;
	    }
	    public String getSender() {
	    	return sender;
	    }
	    public String getMessage() {
	    	return message;
	    }

}
