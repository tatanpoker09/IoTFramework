package tatanpoker.com.frameworklib.framework.network.packets;

import org.json.JSONObject;

import java.net.Socket;

import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;

public class RecognizeDevicePacket implements IPacket {
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
    public void recieve(Socket socket, ConnectionThread clientThread) {
        NetworkComponent component = null;
        try {
            component = Framework.getNetwork().getComponent(id);

            component.setClientThread(clientThread);
            component.setConnected(true);
            Framework.getNetwork().getServer().devices += 1;
            if(Framework.getNetwork().getServer().devices >= Framework.getComponents().size()){
                Framework.getNetwork().getServer().getSemaphore().release();
            }
            Framework.getLogger().info(String.format("Device %s connected! (%d/%d)", component.getId(), Framework.getNetwork().getServer().devices,Framework.getComponents().size()));
        } catch (InvalidIDException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void recieve(String endpointId) {
        //This is only necessary when done through Sockets.
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
