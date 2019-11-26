package tatanpoker.com.frameworklib.framework.network.packets;

import java.net.Socket;

import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.types.SimplePacket;

public class ServerReadyPacket extends SimplePacket {

    public ServerReadyPacket() {
        super(EncryptionType.NONE);
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
