package tatanpoker.com.frameworklib.framework.network.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Objects;

import tatanpoker.com.frameworklib.exceptions.DeviceOfflineException;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.TreeStatus;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.Packet;
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
    public void sendPacket(Packet packet) {
        try {
            this.clientThread.sendPacket(packet);
        } catch (DeviceOfflineException e) {
            e.printStackTrace();
        }
    }
}

class ConnectionRunnable implements Runnable{
    private SocketClient socketClient;

    ConnectionRunnable(SocketClient socketClient){
        this.socketClient = socketClient;
    }

    @Override
    public void run() {
        try {
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
            Framework.getLogger().info(String.format("Attempting to connect to %s:%s", SERVER_IP, SERVERPORT));
            socketClient.socket = new Socket(serverAddr, SERVERPORT);
            Framework.getLogger().info("Successfully connected to socketServer on ip: " + SERVER_IP + ":" + SERVERPORT);
            Framework.getNetwork().getServer().setStatus(TreeStatus.ONLINE);
            Framework.getLogger().info("Sending Recognize Packet with id: " + Framework.getNetwork().getLocal().getId());

            Packet recognizePacket = new RecognizeDevicePacket(
                    Framework.getNetwork().getLocal().getId(),
                    Objects.requireNonNull(Framework.getNetwork().getComponent(
                            Framework.getNetwork().getLocal().getId())).getClass().getName(),
                    Framework.getNetwork().getPublicKey());
            socketClient.clientThread = new ConnectionThread(socketClient.socket);
            socketClient.sendPacket(recognizePacket);
            Framework.getNetwork().getLocal().setStatus(TreeStatus.ONLINE);
            socketClient.clientThread.start();
        } catch (IOException | InvalidIDException e) {
            e.printStackTrace();
        }
    }
}
