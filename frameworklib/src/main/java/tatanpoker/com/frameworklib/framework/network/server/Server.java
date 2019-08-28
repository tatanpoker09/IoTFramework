package tatanpoker.com.frameworklib.framework.network.server;

import java.net.ServerSocket;
import java.util.concurrent.Semaphore;

import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.network.packets.IPacket;
import tatanpoker.com.frameworklib.framework.network.packets.ServerReadyPacket;

public abstract class Server extends NetworkComponent {
    private Semaphore semaphore;
    protected ServerSocket serverSocket;
    public int devices;
    
    public static final int SERVERPORT = 6666;

    public Server(int id, int layout) throws InvalidIDException {
        super(id, layout);
    }


    @Override
    public void onEnable(){
        if(isLocal()) {
            startServer();
            int componentCount = Framework.getComponents().size();
            semaphore = new Semaphore(0);

            Framework.getLogger().info(String.format("Starting Semaphore to wait for devices to connect... (0/%s)", componentCount));
            devices = 0; /* TODO ASK BENEDETTO HOW TO IMPROVE THIS */
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ServerReadyPacket serverReadyPacket = new ServerReadyPacket();
            sendPacket(serverReadyPacket);
        }
    }

    abstract void sendPacket(IPacket serverReadyPacket);

    protected abstract void startServer();

    public Semaphore getSemaphore() {
        return semaphore;
    }
}
