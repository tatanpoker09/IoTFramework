package tatanpoker.com.frameworklib.framework.network.packets;

import org.json.JSONObject;

import java.net.Socket;

import tatanpoker.com.frameworklib.exceptions.DeviceOfflineException;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.TreeStatus;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;

public class RecognizeDevicePacket extends Packet {
    private int id;
    private String name;

    public RecognizeDevicePacket(int id, String name) {
        this.id = id;
        this.name = name;
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

            component.setClientThread(clientThread);
            Framework.getNetwork().getServer().devices += 1;
            Framework.getLogger().info(String.format("Device %s connected! (%d/%d)", component.getId(), Framework.getNetwork().getServer().devices, Framework.getNetwork().getComponents().size() - 1));

            for (NetworkComponent networkComponent : Framework.getNetwork().getComponents()) {
                if (networkComponent.getStatus() == TreeStatus.ONLINE) {
                    networkComponent.getClientThread().sendPacket(new ComponentConnectedPacket(component.getId()));
                }
            }
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
