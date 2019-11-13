package tatanpoker.com.iotframework;

import tatanpoker.com.frameworklib.framework.TreeDeviceManager;
import tatanpoker.com.iotframework.devices.Alarm;
import tatanpoker.com.iotframework.devices.Camera;
import tatanpoker.com.tree.annotations.DeviceManager;
import tatanpoker.com.tree.annotations.Local;

@DeviceManager()
public abstract class Devices extends TreeDeviceManager {

    public abstract Camera getCamera();

    @Local
    public abstract Alarm getAlarm();

}
