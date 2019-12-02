package tatanpoker.com.frameworklib.framework;


import java.util.List;

import tatanpoker.com.frameworklib.framework.network.server.Server;

public abstract class TreeDeviceManager {
    public List<NetworkComponent> devices;
    public NetworkComponent local;
    public Server server;

    public abstract void init();

    public void callByID(int id, Object... objects) {
    }
}
