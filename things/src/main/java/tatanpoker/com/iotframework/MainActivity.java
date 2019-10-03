package tatanpoker.com.iotframework;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import tatanpoker.com.frameworklib.events.alarm.AlarmTriggerEvent;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.Tree;
import tatanpoker.com.frameworklib.framework.network.server.Server;
import tatanpoker.com.iotframework.alarm.Alarm;
import tatanpoker.com.iotframework.camera.Camera;

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
    private Server server;
    private Alarm alarm;

    private int local_id = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Instantiate and give a different frontend to each.
        super.onCreate(savedInstanceState);

        Framework.startNetwork(this, local_id);
        Devices deviceManager = Framework.registerComponents(this, Devices.class);
        Tree network = Framework.getNetwork();
        network.registerEvents(new ServerEvents());
        Framework.networkEnable();

        //This should be done internally.
        if(local_id != 0) {// 0 = SERVER_ID.
            setContentView(network.getLocal().getLayout());
        } else {
            setContentView(R.layout.server_layout);
        }
        network.callEvent(new AlarmTriggerEvent("This is a test"));
        /* OLD FORMAT
        try {
            alarm = (Alarm) network.getComponent(ALARM_ID);
            camera = (Camera) network.getComponent(CAMERA_ID);
        } catch (InvalidIDException e) {
            e.printStackTrace();
        } */
        //New format: 
        alarm = deviceManager.getAlarm();
        camera = deviceManager.getCamera();
        Framework.getLogger().info("Finished activity setup.");
    }

    public void sendText(View view) {
        TextView textView = findViewById(R.id.textBoxSend);
        String text = textView.getText().toString();
        camera.changeText(text);
    }


    /*public void increaseNumber(View view){
        camera.increaseNumber(1);
    }*/

    public Alarm getAlarm() {
        return alarm;
    }
}
