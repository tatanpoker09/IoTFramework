package tatanpoker.com.frameworklib.framework.network;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import tatanpoker.com.frameworklib.exceptions.DeviceOfflineException;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.TreeStatus;
import tatanpoker.com.frameworklib.framework.network.packets.ComponentDisconnectedPacket;
import tatanpoker.com.frameworklib.framework.network.packets.Packet;
import tatanpoker.com.frameworklib.framework.network.server.Server;

public class ConnectionThread extends Thread {
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

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
                Packet packet = (Packet) objectInputStream.readObject();
                packet.recieve(socket, this);
            } catch (IOException e) {
                Framework.getLogger().severe("Extra packet sent(?");
                e.printStackTrace();
                break;
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
                component.setStatus(TreeStatus.OFFLINE);
                if (!(Framework.getNetwork().getLocal() instanceof Server)) {
                    Framework.getNetwork().connect();
                } else {
                    Framework.getNetwork().getServer().devices -= 1;

                    for (NetworkComponent networkComponent : Framework.getNetwork().getComponents()) {
                        if (networkComponent.getStatus() == TreeStatus.ONLINE) {
                            networkComponent.getClientThread().sendPacket(new ComponentDisconnectedPacket(component.getId()));
                        }
                    }
                }
            }
        } catch (InvalidIDException e) {
            Framework.getLogger().severe("Error disconnecting unknown component?");
            e.printStackTrace();
        } catch (DeviceOfflineException e) {
            e.printStackTrace();
        }
    }

    public void sendPacket(Packet recognizePacket) throws DeviceOfflineException {
        if (socket.isConnected()) {
            PacketSender packetSender = new PacketSender(recognizePacket, objectOutputStream);
            Thread packetThread = new Thread(packetSender);
            packetThread.start();
        } else {
            throw new DeviceOfflineException(socket.getInetAddress().toString());
        }
    }

    class PacketSender implements Runnable {
        private Packet packet;

        PacketSender(Packet packet, ObjectOutputStream oos) {
            this.packet = packet;
        }

        public synchronized void sendPacket(Packet packet) throws IOException {
            if (!socket.isClosed()) {
                if(objectOutputStream == null){
                    objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                }
                Framework.getLogger().info("Sending packet: "+packet.getClass().getName()+" through socket.");
                objectOutputStream.writeObject(packet);
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