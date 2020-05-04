package tatanpoker.com.iotframework.devices;

import com.example.iotframework.R;

import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.network.server.SocketServer;
import tatanpoker.com.frameworklib.framework.network.streaming.FileStream;
import tatanpoker.com.tree.annotations.Device;

@Device(id = 0, layout = R.layout.activity_server)
public class CustomServer extends SocketServer {
    public CustomServer(int id, int layout) throws InvalidIDException {
        super(id, layout);
    }

    public void startFileStream(String fileName, int speakerID) {
        Speaker speaker = null;
        try {
            speaker = (Speaker) Framework.getNetwork().getComponent(speakerID);
        } catch (InvalidIDException e) {
            e.printStackTrace();
        }
        assert speaker != null;
        FileStream fileStream = streamFile(fileName,speaker.getId());
        speaker.play(fileStream);
    }
}
