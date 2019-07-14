package tatanpoker.com.frameworklib.framework.network.packets;

import org.json.JSONObject;

import java.io.Serializable;
import java.net.Socket;

import tatanpoker.com.frameworklib.framework.network.ConnectionThread;

public interface IPacket extends Serializable{
    JSONObject toJson();

    /**
     * Called whenever a packet is recieved from a socket
     * @param socket
     */
    void recieve(Socket socket, ConnectionThread clientThread);
}
