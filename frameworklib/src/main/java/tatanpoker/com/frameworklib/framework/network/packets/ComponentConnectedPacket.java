package tatanpoker.com.frameworklib.framework.network.packets;

import org.json.JSONObject;

import java.net.Socket;
import java.security.PublicKey;

import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.OnNodeConnectionListener;
import tatanpoker.com.frameworklib.framework.TreeStatus;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;

public class ComponentConnectedPacket extends Packet {
    private int id; //Component
    private PublicKey publicKey;

    public ComponentConnectedPacket(int id, PublicKey publicKey) {
        this.id = id;
        this.publicKey = publicKey;
    }

    @Override
    public JSONObject toJson() {
        return null;
    }

    @Override
    void process(String endpointId) {

    }

    @Override
    void process(Socket socket, ConnectionThread clientThread) {
        NetworkComponent component = getComponent();
        component.setStatus(TreeStatus.ONLINE);
        OnNodeConnectionListener nodeConnectionListener = Framework.getNetwork().getLocal().getConnectionListener();
        Framework.getNetwork().getServer().setPublicKey(publicKey);
        if (nodeConnectionListener != null)
            Framework.getNetwork().getLocal().getConnectionListener().onNodeConnected(component);
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
