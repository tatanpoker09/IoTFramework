package tatanpoker.com.frameworklib.framework.network;


import android.os.Build;

import androidx.annotation.RequiresApi;

import org.apache.commons.lang3.SerializationUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import tatanpoker.com.frameworklib.exceptions.DeviceOfflineException;
import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.TreeStatus;
import tatanpoker.com.frameworklib.framework.network.packets.ComponentDisconnectedPacket;
import tatanpoker.com.frameworklib.framework.network.packets.EncryptionType;
import tatanpoker.com.frameworklib.framework.network.packets.Packet;
import tatanpoker.com.frameworklib.framework.network.packets.ServerReadyPacket;
import tatanpoker.com.frameworklib.framework.network.server.Server;
import tatanpoker.com.frameworklib.security.AESUtil;
import tatanpoker.com.frameworklib.security.EncryptionUtils;
import tatanpoker.com.frameworklib.security.RSAUtil;

/*
Protocol is length, encryptiontype (ordinal), id its coming from, message.
 */
public class ConnectionThread extends Thread {
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private PacketSender packetSender;

    public ConnectionThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public synchronized void run() {
        try {
            Framework.getLogger().info("Device ready to recieve packets.");
            dataInputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (!Thread.currentThread().isInterrupted()) {
            try {
                int length = dataInputStream.readInt();                    // read length of incoming message
                int ordinal = dataInputStream.readInt(); //Encryption type
                int id = dataInputStream.readInt(); //component id from where its coming from.
                EncryptionType encryptionType = EncryptionType.values()[ordinal];
                if(length>0) {
                    byte[] message = new byte[length];
                    dataInputStream.readFully(message, 0, message.length); // read the message
                    Packet packet;
                    switch (encryptionType) {
                        default:
                        case AES:
                            SecretKey secretKey = Framework.getNetwork().getComponent(id).getSymmetricKey();
                            packet = AESUtil.decrypt(message, secretKey.getEncoded());
                            break;
                        case RSA:
                            PrivateKey privateKey = Framework.getNetwork().getPrivateKey();
                            packet = RSAUtil.decrypt(message, privateKey);
                            break;
                        case NONE:
                            packet = SerializationUtils.deserialize(message);
                            break;
                    }

                    assert packet != null;
                    packet.preprocess(dataInputStream);
                    Framework.getLogger().info("Recieving " + packet.getClass().getSimpleName() + " through socket with " + packet.getEncryptionType());
                    packet.recieve(socket, this);
                }
            } catch (IOException e) {
                Framework.getLogger().severe("Extra packet sent(?");
                e.printStackTrace();
                break;
            } catch (Exception e) { //This is bad but I'm lazy to change it.
                e.printStackTrace();
            }
            try {
                Thread.sleep(100); //Saves CPU usage
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //Disconnect and try to reconnect.
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
                    Framework.getNetwork().getServer().sendPacket(new ComponentDisconnectedPacket(component.getId()));
                }
            }
        } catch (DeviceOfflineException e) {
            Framework.getLogger().severe("Error disconnecting unknown component?");
            e.printStackTrace();
        }
    }

    public void sendPacket(Packet packet, boolean urgent) throws DeviceOfflineException {
        if (socket.isConnected()) {
            if(packetSender==null) {
                packetSender = new PacketSender();
                new Thread(packetSender).start();
            }
            if(urgent) {
                packetSender.addUrgentPacket(packet);
            } else {
                packetSender.addPacket(packet);
            }
        } else {
            throw new DeviceOfflineException(socket.getInetAddress().toString());
        }
    }

    class PacketSender implements Runnable {
        private List<Packet> packets;

        PacketSender() {
            this.packets = new ArrayList<>();
        }

        public void addPacket(Packet packet){
            this.packets.add(packet);
        }

        public void addUrgentPacket(Packet packet){
            this.packets.add(0, packet);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public synchronized void sendPacket(Packet packet) throws IOException {
            if (!socket.isClosed()) {
                if(dataOutputStream == null){
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                }
                System.out.println("Sending packet: "+packet.getUniqueID());
                packet.sendPacket(dataOutputStream, ConnectionThread.this);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run(){
            while(!Thread.currentThread().isInterrupted()) {
                if(packets.size()>0) {
                    Packet packet = packets.get(0);
                    packets.remove(0);
                    try {
                        sendPacket(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}