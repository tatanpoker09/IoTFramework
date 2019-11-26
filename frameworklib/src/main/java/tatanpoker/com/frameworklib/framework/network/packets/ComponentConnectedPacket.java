package tatanpoker.com.frameworklib.framework.network.packets;

import java.net.Socket;
import java.security.PublicKey;

import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.OnNodeConnectionListener;
import tatanpoker.com.frameworklib.framework.TreeStatus;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.types.SimplePacket;

public class ComponentConnectedPacket extends SimplePacket {
    private int id; //Component
    private PublicKey publicKey;

    public ComponentConnectedPacket(int id, PublicKey publicKey) {
        super(EncryptionType.AES);
        this.id = id;
        this.publicKey = publicKey;
    }

    @Override
    protected void process(String endpointId) {

    }

    @Override
    public void process(Socket socket, ConnectionThread clientThread) {
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
