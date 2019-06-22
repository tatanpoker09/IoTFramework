package tatanpoker.com.iotframework;
import tatanpoker.com.frameworklib.components.Alarm;
import tatanpoker.com.frameworklib.components.Vector3;
import tatanpoker.com.frameworklib.components.camera.CameraMovementEvent;
import tatanpoker.com.frameworklib.events.EventInfo;
import tatanpoker.com.frameworklib.events.EventPriority;
import tatanpoker.com.frameworklib.events.EventTrigger;


public class CameraEvents implements EventTrigger {
    private Alarm alarm;

    public CameraEvents(MainActivity main){
        this.alarm = main.getAlarm();
    }

    @EventInfo(priority= EventPriority.HIGH, id=MainActivity.CAMERA_ID)
    public void onCameraTrigger(CameraMovementEvent event){
        //EventType figured from the parameter object.
        Vector3 movement = event.getMovement(); //Vector3
        //Send this movement info to another device.
        alarm.printOnScreen(movement);
    }
}