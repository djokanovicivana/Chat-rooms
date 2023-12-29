package rs.raf.pds.v4.z5.messages;

public class InviteUserRequest {
      private String userName;
      private String roomName;
      
      public InviteUserRequest() {
    	  
      }
      public InviteUserRequest(String roomName, String userName) {
    	  this.roomName=roomName;
    	  this.userName=userName;
      }
      public String getUser() {
    	  return userName;
      }
      public String getRoom() {
    	  return roomName;
      }
}
