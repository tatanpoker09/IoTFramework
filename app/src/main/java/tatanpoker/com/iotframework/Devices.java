package tatanpoker.com.iotframework;

import tatanpoker.com.frameworklib.framework.TreeDeviceManager;
import tatanpoker.com.iotframework.devices.CustomServer;
import tatanpoker.com.iotframework.devices.Microphone;
import tatanpoker.com.iotframework.devices.Speaker;
import tatanpoker.com.tree.annotations.DeviceManager;
import tatanpoker.com.tree.annotations.Local;

@DeviceManager
public abstract class Devices extends TreeDeviceManager {

    public abstract Microphone getMicrophone();

    @Local
    public abstract Speaker getSpeaker();

    public abstract CustomServer getServer();
}