package tatanpoker.com.iotframework;

import tatanpoker.com.frameworklib.framework.TreeDeviceManager;
import tatanpoker.com.iotframework.alarm.Alarm;
import tatanpoker.com.iotframework.camera.Camera;
import tatanpoker.com.tree.annotations.DeviceManager;
import tatanpoker.com.tree.annotations.Local;

@DeviceManager()
public abstract class Devices extends TreeDeviceManager {
    @Local
    public abstract Camera getCamera();


    public abstract Alarm getAlarm();
}