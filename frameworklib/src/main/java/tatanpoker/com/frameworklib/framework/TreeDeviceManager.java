package tatanpoker.com.frameworklib.framework;


import java.util.List;

public abstract class TreeDeviceManager {
    public List<NetworkComponent> devices;
    public NetworkComponent local;

    public abstract void init();
}
