package tatanpoker.com.iotframework;

import android.app.Activity;
import android.os.Bundle;

import tatanpoker.com.frameworklib.framework.network.server.SocketServer;
import tatanpoker.com.frameworklib.events.alarm.AlarmTriggerEvent;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.Tree;
import tatanpoker.com.iotframework.alarm.Alarm;
import tatanpoker.com.iotframework.alarm.AlarmStub;
import tatanpoker.com.iotframework.camera.Camera;
import tatanpoker.com.iotframework.camera.CameraStub;

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

    private int local_id = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Instantiate and give a different frontend to each.
        super.onCreate(savedInstanceState);
        Framework.registerComponent(CameraStub.class, R.layout.activity_main);
        Framework.registerComponent(AlarmStub.class, R.layout.activity_main);

        Framework.startNetwork(local_id);

        Tree network = (Tree)Framework.getNetwork();

        network.registerEvents(new ServerEvents());

        Framework.networkEnable();

        if(local_id != 0) {// 0 = SERVER_ID.
            setContentView(network.getLocal().getLayout());
        }
        network.callEvent(new AlarmTriggerEvent("This is a test"));
        try {
            alarm = (Alarm) network.getComponent(ALARM_ID);
            camera = (Camera) network.getComponent(CAMERA_ID);
        } catch (InvalidIDException e) {
            e.printStackTrace();
        }

        alarm.testAlarm();
        camera.cameraTest();
    }

    public Alarm getAlarm() {
        return alarm;
    }
}
