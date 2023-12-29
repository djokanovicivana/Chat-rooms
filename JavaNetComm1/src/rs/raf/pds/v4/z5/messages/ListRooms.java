package rs.raf.pds.v4.z5.messages;

import java.util.ArrayList;

public class ListRooms {
     ArrayList<String> rooms;
     protected ListRooms() {
    	 
     }
     public ListRooms(ArrayList<String> rooms) {
    	 this.rooms=rooms;
     }
     public ArrayList<String> getRooms() {
    	 return rooms;
     }
}
