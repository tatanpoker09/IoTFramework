package iotframework;

import android.app.Activity;
import android.os.Bundle;

import tatanpoker.com.frameworklib.components.Alarm;
import tatanpoker.com.frameworklib.components.AlarmTriggerEvent;
import tatanpoker.com.frameworklib.components.Server;
import tatanpoker.com.frameworklib.components.camera.Camera;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.network.Tree;

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
public class MainActivity extends Activity {
    public static final int CAMERA_ID = 1;
    public static final int ALARM_ID = 2;

    private Camera camera;
    private Server server;
    private Alarm alarm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Instantiate and give a different frontend to each.
        try {
            camera = new Camera(CAMERA_ID, R.layout.activity_main);
            server = new Server();//Server doesn't have a frontend. It also isn't a component really, but it fits.
            alarm = new Alarm(ALARM_ID, R.layout.activity_main);
        } catch (InvalidIDException e) {
            e.printStackTrace();
        }


        super.onCreate(savedInstanceState);
        Framework.registerComponents(camera, server, alarm);
        try {
            Framework.startNetwork(CAMERA_ID, this, server);
        } catch (InvalidIDException e) {
            e.printStackTrace();
        }
        Tree network = (Tree)Framework.getNetwork();

        network.registerEvents(new AlarmEvents(this));
        network.registerEvents(new CameraEvents(this));
        setContentView(network.getLocal().getLayout());
        network.callEvent(new AlarmTriggerEvent("This is a test"));
    }

    public Alarm getAlarm() {
        return alarm;
    }
}
