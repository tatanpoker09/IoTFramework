package tatanpoker.com.frameworklib.framework.network.packets;

import org.json.JSONObject;

import java.net.Socket;

import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;

public class ServerReadyPacket extends Packet {
    @Override
    public JSONObject toJson() {
        return null;
    }

    @Override
    public void process(Socket socket, ConnectionThread clientThread) {
        process();
    }

    @Override
    public void process(String endpointId) {
        process();
    }

    private void process() {
        Framework.getLogger().info("NearbyServer is ready response! We can start now.");
        Framework.getNetwork().getLocal().onServerReady();
    }
}
