package tatanpoker.com.iotframework;

import tatanpoker.com.frameworklib.framework.TreeDeviceManager;
import tatanpoker.com.iotframework.alarm.Alarm;
import tatanpoker.com.iotframework.camera.Camera;
import tatanpoker.com.tree.annotations.DeviceManager;

@DeviceManager()
public abstract class Devices extends TreeDeviceManager {
    abstract Camera getCamera();

    abstract Alarm getAlarm();
}
