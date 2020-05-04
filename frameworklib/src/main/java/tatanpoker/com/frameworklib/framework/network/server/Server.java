package tatanpoker.com.frameworklib.framework.network.server;

import java.io.File;

import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Component;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.TreeStatus;
import tatanpoker.com.frameworklib.framework.network.packets.Packet;
import tatanpoker.com.frameworklib.framework.network.packets.ServerReadyPacket;

public abstract class Server extends NetworkComponent {
    public int devices;
    
    public static final int SERVERPORT = 6666;

    public Server(int id, int layout) throws InvalidIDException {
        super(id, layout);
    }


    @Override
    public void onEnable(){
        if(isLocal()) {
            startServer();
            Framework.getNetwork().getLocal().setStatus(TreeStatus.CONNECTING);
            int componentCount = Framework.getNetwork().getComponents().size();

            Framework.getLogger().info(String.format("Starting Server to wait for devices to connect... (0/%s)", componentCount));
            devices = 0;
        }
    }

    public abstract void sendPacket(Packet serverReadyPacket);

    protected abstract void startServer();
}
