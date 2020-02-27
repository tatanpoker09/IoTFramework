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

    public abstract Speaker getSpeaker();

    @Local
    public abstract CustomServer getServer();
}