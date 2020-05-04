package tatanpoker.com.iotframework.devices;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.iotframework.R;

import java.util.ArrayList;
import java.util.Locale;

import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.network.streaming.FileStream;
import tatanpoker.com.frameworklib.framework.network.server.Server;
import tatanpoker.com.iotframework.Devices;
import tatanpoker.com.tree.annotations.Device;

import static tatanpoker.com.iotframework.devices.Microphone.MICROPHONE_ID;

@Device(id = MICROPHONE_ID, layout = R.layout.activity_microphone)
public class Microphone extends NetworkComponent {
    static final int MICROPHONE_ID = 1;
    public static final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView txtSpeechInput;

    public Microphone(int id, int layout) throws InvalidIDException {
        super(id, layout);
    }

    public void initialize(){
    }

    public void onRecognition(String intent) {
        if (intent.startsWith("play")) {
            String fileName = intent.replace("play ", "") + ".mp3";
            Devices deviceManager = (Devices) Framework.getDeviceManager();
            CustomServer server = deviceManager.getServer();
            Speaker speaker = deviceManager.getSpeaker();
            server.startFileStream(fileName, speaker.getId()); //Return a "proxy" with a special id. Then match inputstream to said ID on the other side?
        }
    }

    public void onDevicesRegistered() {
    }

    @Override
    public void onServerReady() {
        Activity context = (Activity) Framework.getNetwork().getContext();
        context.runOnUiThread(() -> {
            txtSpeechInput = context.findViewById(R.id.txtSpeechInput);
            ImageButton btnSpeak = context.findViewById(R.id.btnSpeak);
            context.getActionBar().hide();
            btnSpeak.setOnClickListener(v -> promptSpeechInput());
        });
    }

    /**
     * Showing google speech input dialog
     * */

    public void promptSpeechInput() {
        Activity context = (Activity) Framework.getNetwork().getContext();
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                context.getString(R.string.speech_prompt));
        try {
            context.startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(context.getApplicationContext(),
                    context.getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
