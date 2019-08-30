package tatanpoker.com.frameworklib.framework.network.client;

import tatanpoker.com.frameworklib.framework.network.packets.IPacket;

public abstract class ClientConnection {
    public abstract void connect();

    public abstract void sendPacket(IPacket packet);
}
