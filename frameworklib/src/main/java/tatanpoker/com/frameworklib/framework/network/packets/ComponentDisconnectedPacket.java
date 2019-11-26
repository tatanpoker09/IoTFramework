package tatanpoker.com.frameworklib.framework.network.packets;

import java.net.Socket;

import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.OnNodeConnectionListener;
import tatanpoker.com.frameworklib.framework.TreeStatus;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.types.SimplePacket;

public class ComponentDisconnectedPacket extends SimplePacket {
    private int id; //Component

    public ComponentDisconnectedPacket(int id) {
        super(EncryptionType.AES);
        this.id = id;
    }

    @Override
    protected void process(String endpointId) {

    }

    @Override
    public void process(Socket socket, ConnectionThread clientThread) {
        NetworkComponent component = getComponent();
        component.setStatus(TreeStatus.OFFLINE);
        OnNodeConnectionListener nodeConnectionListener = Framework.getNetwork().getLocal().getConnectionListener();
        if (nodeConnectionListener != null)
            nodeConnectionListener.onNodeDisconnected(component);
    }

    public NetworkComponent getComponent() {
        try {
            return Framework.getNetwork().getComponent(id);
        } catch (InvalidIDException e) {
            e.printStackTrace();
        }
        return null;
    }
}
