package tatanpoker.com.iotframework;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import tatanpoker.com.frameworklib.framework.Framework;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Devices deviceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Framework.startNetwork(this);
        deviceManager = Framework.registerComponents(Devices.class);
        Framework.networkEnable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public Devices getDeviceManager() {
        return deviceManager;
    }

    public void setDeviceManager(Devices deviceManager) {
        this.deviceManager = deviceManager;
    }
}
