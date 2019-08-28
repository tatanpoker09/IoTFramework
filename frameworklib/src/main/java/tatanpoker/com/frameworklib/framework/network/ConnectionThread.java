package tatanpoker.com.frameworklib.framework.network;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.network.packets.IPacket;

public class ConnectionThread extends Thread {
    private Socket socket;
    private ObjectInputStream objectInputStream;

    public ConnectionThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public synchronized void run() {
        try {
            Framework.getLogger().info("Device ready to recieve packets.");
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (!Thread.currentThread().isInterrupted()) {
            try {
                IPacket packet = (IPacket) objectInputStream.readObject();
                packet.recieve(socket, this);
            } catch (IOException e) {
                Framework.getLogger().severe("Extra packet sent(?");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(100); //Saves CPU usage
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            NetworkComponent component = Framework.getNetwork().getComponent(this);
            if(component!=null) {
                Framework.getLogger().info(String.format("Disconnecting component with id %d", component.getId()));
                component.setClientThread(null);
                component.setConnected(false);
            }
        } catch (InvalidIDException e) {
            Framework.getLogger().severe("Error disconnecting unknown component?");
            e.printStackTrace();
        }
    }

    public void sendPacket(IPacket recognizePacket) {
        PacketSender packetSender = new PacketSender(recognizePacket);
        Thread packetThread = new Thread(packetSender);
        packetThread.start();
    }

    class PacketSender implements Runnable {
        private IPacket packet;

        public PacketSender(IPacket packet){
            this.packet = packet;
        }

        public synchronized void sendPacket(IPacket packet) throws IOException {
            if (!socket.isClosed()) {
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(packet);
            }
        }

        @Override
        public void run(){
            try {
                sendPacket(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}