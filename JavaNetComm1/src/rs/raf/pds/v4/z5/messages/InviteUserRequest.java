package rs.raf.pds.v4.z5.messages;

public class InviteUserRequest {
      private String userName;
      private String roomName;
      
      public InviteUserRequest(String userName, String roomName) {
    	  this.userName=userName;
    	  this.roomName=roomName;
      }
      public String getUser() {
    	  return userName;
      }
      public String getRoom() {
    	  return roomName;
      }
}
