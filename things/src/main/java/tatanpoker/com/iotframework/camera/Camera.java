package tatanpoker.com.iotframework.camera;

import android.app.Activity;
import android.content.Context;
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

@Device(id = Framework.CAMERA_ID, stub = true, layout = R.layout.camera_layout)
public class Camera extends NetworkComponent {
    private int triggerCount;
    public Camera(int id, int layout, Context context) throws InvalidIDException {
        super(id, layout, context);
        triggerCount = 0;
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
    /*
    public void increaseNumber(final Integer amount) {
        ((Activity)context).runOnUiThread(() -> {
            TextView textView = ((Activity) context).findViewById(R.id.cameraTriggerCount);
            String initialCameraTextValue = context.getResources().getString(R.string.cameraPrintDefault);
            triggerCount +=amount;
            textView.setText(initialCameraTextValue+" "+triggerCount);
        });
    }*/


    public void changeText(String text) {
        ((Activity) context).runOnUiThread(() -> {
            TextView textView = ((Activity) context).findViewById(R.id.recievedText);
            String initialCameraTextValue = context.getResources().getString(R.string.cameraRecievedText);
            textView.setText(initialCameraTextValue + text);
        });
    }
}
