package tatanpoker.com.frameworklib.framework;


import java.util.List;

public abstract class TreeDeviceManager {
    public List<NetworkComponent> devices;
    public NetworkComponent local;

    /*
    TODO CREATE INSTANCES OF THE CAMERA AND ALARM (? I HAVE TO ANALYZE THIS.
    POSSIBLE HAVE AN OWN GENERATED SUB-INIT WHERE ITS CUSTOM GENERATED AND IT INITIALIZES THEM.
     */
    public abstract void init();
}
