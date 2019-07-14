package iotframework.components.camera;

import iotframework.components.alarm.Alarm;
import tatanpoker.com.frameworklib.components.Device;
import tatanpoker.com.frameworklib.components.Vector3;
import tatanpoker.com.frameworklib.events.EventInfo;
import tatanpoker.com.frameworklib.events.EventPriority;
import tatanpoker.com.frameworklib.events.EventTrigger;
import tatanpoker.com.frameworklib.events.alarm.AlarmTriggerEvent;
import tatanpoker.com.frameworklib.events.camera.CameraMovementEvent;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;

import static tatanpoker.com.frameworklib.framework.Framework.ALARM_ID;

@Device(id=Framework.CAMERA_ID)
public class Camera extends NetworkComponent {
    public Camera(int id, int layout) throws InvalidIDException {
        super(id, layout);
    }

    public void cameraTest(){
        System.out.println("Camera is working locally");
    }


    @EventInfo(priority= EventPriority.HIGH, id=Framework.CAMERA_ID)
    public void onCameraTrigger(CameraMovementEvent event){
        //EventType figured from the parameter object.
        Vector3 movement = event.getMovement(); //Vector3
        //Send this movement info to another device.
        try {
            Alarm alarm = (Alarm) Framework.getNetwork().getComponent(ALARM_ID);
            alarm.printOnScreen(new AlarmTriggerEvent(movement.toString()));
        } catch (InvalidIDException e) {
            e.printStackTrace();
        }
    }
}
