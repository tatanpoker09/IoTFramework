package tatanpoker.com.frameworklib.framework.network.packets;

import org.json.JSONObject;

import java.net.Socket;

import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;

public class ServerReadyPacket implements IPacket {
    @Override
    public JSONObject toJson() {
        return null;
    }

    @Override
    public void recieve(Socket socket, ConnectionThread clientThread) {
        Framework.getLogger().info("Server is ready response! We can start now.");
        Framework.getNetwork().getSemaphore().release();
    }
}
