package tatanpoker.com.frameworklib.framework.network.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;

import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.IPacket;
import tatanpoker.com.frameworklib.framework.network.packets.RecognizeDevicePacket;

import static tatanpoker.com.frameworklib.framework.Tree.SERVER_IP;
import static tatanpoker.com.frameworklib.framework.network.server.Server.SERVERPORT;

public class SocketClient extends ClientConnection{
    Socket socket;
    ConnectionThread clientThread;
    @Override
    public void connect() {
        ConnectionRunnable connectionRunnable = new ConnectionRunnable(this);
        new Thread(connectionRunnable).start();
    }

    @Override
    public void sendPacket(IPacket packet) {
        this.clientThread.sendPacket(packet);
    }
}

class ConnectionRunnable implements Runnable{
    private SocketClient socketClient;

    public ConnectionRunnable(SocketClient socketClient){
        this.socketClient = socketClient;
    }

    @Override
    public void run() {
        try {
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
            Framework.getLogger().info(String.format("Attempting to connect to %s:%s", SERVER_IP, SERVERPORT));
            socketClient.socket = new Socket(serverAddr, SERVERPORT);
            Framework.getLogger().info("Successfully connected to socketServer on ip: " + SERVER_IP + ":" + SERVERPORT);
            Framework.getLogger().info("Sending Recognize Packet with id: " + Framework.getNetwork().getId());
            IPacket recognizePacket = new RecognizeDevicePacket(Framework.getNetwork().getId(), Objects.requireNonNull(Framework.getNetwork().getComponent(Framework.getNetwork().getId())).getClass().getName());
            socketClient.clientThread = new ConnectionThread(socketClient.socket);
            socketClient.clientThread.sendPacket(recognizePacket);
        } catch (IOException | InvalidIDException e) {
            e.printStackTrace();
        }
        socketClient.clientThread.start();
    }
}
