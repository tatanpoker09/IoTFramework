package tatanpoker.com.iotframework;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iotframework.R;

import java.util.ArrayList;
import java.util.Locale;

import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.iotframework.devices.Microphone;

public class MainActivity extends Activity {
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

    public void reconnect(View view) {

    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Microphone.REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    runOnUiThread(() -> {
                        TextView txtSpeechInput = findViewById(R.id.txtSpeechInput);
                        txtSpeechInput.setText(result.get(0));
                    });
                    getDeviceManager().getMicrophone().onRecognition(result.get(0));
                }
                break;
            }
        }
    }

}
