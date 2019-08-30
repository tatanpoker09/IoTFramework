package iotframework;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import iotframework.alarm.Alarm;
import iotframework.alarm.AlarmStub;
import iotframework.camera.Camera;
import iotframework.camera.CameraStub;
import tatanpoker.com.frameworklib.events.alarm.AlarmTriggerEvent;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.Tree;
import tatanpoker.com.frameworklib.framework.network.server.SocketServer;
import static tatanpoker.com.frameworklib.framework.Framework.ALARM_ID;
import static tatanpoker.com.frameworklib.framework.Framework.CAMERA_ID;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 * <p>
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
/*
CUSTOM ANNOTATION PROCESSOR.
 */
public class MainActivity extends Activity {
    private Camera camera;
    private SocketServer socketServer;
    private Alarm alarm;

    private int local_id = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Instantiate and give a different frontend to each.
        super.onCreate(savedInstanceState);
        Framework.registerComponent(CameraStub.class, R.layout.camera_layout);
        Framework.registerComponent(AlarmStub.class, R.layout.alarm_layout);

        Framework.startNetwork(this, local_id);

        Tree network = (Tree)Framework.getNetwork();

        network.registerEvents(new ServerEvents());

        Framework.networkEnable();

        if(local_id != 0) {// 0 = SERVER_ID.
            setContentView(network.getLocal().getLayout());
        } else {
            setContentView(R.layout.server_layout);
        }
        network.callEvent(new AlarmTriggerEvent("This is a test"));
        try {
            alarm = (Alarm) network.getComponent(ALARM_ID);
            camera = (Camera) network.getComponent(CAMERA_ID);
        } catch (InvalidIDException e) {
            e.printStackTrace();
        }
    }

    public void increaseNumber(View view){
        camera.increaseNumber(1);
    }

    public Alarm getAlarm() {
        return alarm;
    }
}
