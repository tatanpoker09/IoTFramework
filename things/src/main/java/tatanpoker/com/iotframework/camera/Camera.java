package tatanpoker.com.iotframework.camera;

import android.app.Activity;
import android.widget.TextView;

import tatanpoker.com.frameworklib.components.Vector3;
import tatanpoker.com.frameworklib.events.EventInfo;
import tatanpoker.com.frameworklib.events.EventPriority;
import tatanpoker.com.frameworklib.events.alarm.AlarmTriggerEvent;
import tatanpoker.com.frameworklib.events.camera.CameraMovementEvent;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.iotframework.R;
import tatanpoker.com.iotframework.alarm.Alarm;
import tatanpoker.com.tree.annotations.Device;

import static tatanpoker.com.frameworklib.framework.Framework.ALARM_ID;

@Device(id = Framework.CAMERA_ID, layout = R.layout.camera_layout)
public class Camera extends NetworkComponent {

    public Camera(int id, int layout) throws InvalidIDException {
        super(id, layout);
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

    public void changeText(String text) {
        ((Activity) context).runOnUiThread(() -> {
            TextView textView = ((Activity) context).findViewById(R.id.recievedText);
            String initialCameraTextValue = context.getResources().getString(R.string.cameraRecievedText);
            textView.setText(initialCameraTextValue + text);
        });
    }
}
