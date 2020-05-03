package tatanpoker.com.iotframework.devices;

import com.example.iotframework.R;

import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.network.streaming.FileStream;
import tatanpoker.com.frameworklib.framework.network.server.Server;
import tatanpoker.com.iotframework.Devices;
import tatanpoker.com.tree.annotations.Device;

import static tatanpoker.com.iotframework.devices.Microphone.MICROPHONE_ID;

@Device(id = MICROPHONE_ID, layout = R.layout.microphone_layout)
public class Microphone extends NetworkComponent {
    static final int MICROPHONE_ID = 1;

    public Microphone(int id, int layout) throws InvalidIDException {
        super(id, layout);
    }

    public void onRecognition(String intent) {
        if (intent.startsWith("play")) {
            String fileName = intent.replace("play ", "") + ".mp3";
            Devices deviceManager = (Devices) Framework.getDeviceManager();
            CustomServer server = deviceManager.getServer();
            Speaker speaker = deviceManager.getSpeaker();
            server.startFileStream(fileName, speaker); //Return a "proxy" with a special id. Then match inputstream to said ID on the other side?
        }
    }

    public void onDevicesRegistered() {
        onRecognition("buttercup");

    }
}
