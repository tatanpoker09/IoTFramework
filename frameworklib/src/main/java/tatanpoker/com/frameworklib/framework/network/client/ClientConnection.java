package tatanpoker.com.frameworklib.framework.network.client;

import tatanpoker.com.frameworklib.framework.network.packets.Packet;

public abstract class ClientConnection {
    public abstract void connect();

    public abstract void sendPacket(Packet packet, boolean urgent);
}
