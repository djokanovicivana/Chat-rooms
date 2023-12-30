package rs.raf.pds.v4.z5.messages;

import com.esotericsoftware.kryo.Kryo;

public class KryoUtil {
	public static void registerKryoClasses(Kryo kryo) {
		kryo.register(String.class);
		kryo.register(String[].class);
		kryo.register(Login.class);
		kryo.register(ChatMessage.class);
		kryo.register(WhoRequest.class);
		kryo.register(ListUsers.class);
		kryo.register(InfoMessage.class);
		kryo.register(PrivateMessage.class);
		kryo.register(ChatRoom.class);
		kryo.register(java.util.ArrayList.class);
		kryo.register(ListRoomsRequest.class);
		kryo.register(ListRooms.class);
		kryo.register(InviteUserRequest.class);
		kryo.register(JoinRoomRequest.class);
		kryo.register(GetMoreMessagesRequest.class);
		kryo.register(ListMessages.class);
	}
}
