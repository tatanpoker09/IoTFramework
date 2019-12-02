package tatanpoker.com.iotframework.devices;

import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.network.server.SocketServer;
import tatanpoker.com.iotframework.R;
import tatanpoker.com.tree.annotations.Device;

@Device(id = 0, layout = R.layout.server_layout)
public class CustomServer extends SocketServer {
    public CustomServer(int id, int layout) throws InvalidIDException {
        super(id, layout);
    }
}
