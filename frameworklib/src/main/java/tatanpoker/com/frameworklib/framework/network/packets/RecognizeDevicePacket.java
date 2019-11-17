package tatanpoker.com.frameworklib.framework.network.packets;

import org.json.JSONObject;

import java.net.Socket;
import java.security.PublicKey;

import tatanpoker.com.frameworklib.exceptions.DeviceOfflineException;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.TreeStatus;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;

public class RecognizeDevicePacket extends Packet {
    private int id;
    private String name;
    private PublicKey publicKey;

    public RecognizeDevicePacket(int id, String name, PublicKey publicKey){
        this.id = id;
        this.name = name;
        this.publicKey = publicKey;
        setEncryptionType(EncryptionType.NONE);
    }

    @Override
    public JSONObject toJson() {
        return null;
    }

    @Override
    public void process(Socket socket, ConnectionThread clientThread) {
        NetworkComponent component;
        try {
            component = Framework.getNetwork().getComponent(id);
            component.setPublicKey(publicKey);
            component.setClientThread(clientThread);
            Framework.getNetwork().getServer().devices += 1;
            Framework.getLogger().info(String.format("Device %s connected! (%d/%d)", component.getId(), Framework.getNetwork().getServer().devices, Framework.getNetwork().getComponents().size() - 1));

            for (NetworkComponent networkComponent : Framework.getNetwork().getComponents()) {
                if (networkComponent.getStatus() == TreeStatus.ONLINE) {
                    networkComponent.getClientThread().sendPacket(new ComponentConnectedPacket(component.getId(), Framework.getNetwork().getPublicKey()));
                }
            }
            ConnectionResponsePacket connectionResponsePacket = new ConnectionResponsePacket(Framework.getNetwork().getLocal().getPublicKey());
            component.getClientThread().sendPacket(connectionResponsePacket);

            component.setStatus(TreeStatus.ONLINE);

        } catch (InvalidIDException e) {
            e.printStackTrace();
        } catch (DeviceOfflineException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(String endpointId) {
        //This is only necessary when done through Sockets.
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
