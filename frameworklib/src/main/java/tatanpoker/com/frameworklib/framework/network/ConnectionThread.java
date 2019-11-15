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
import tatanpoker.com.frameworklib.framework.network.server.Server;
import tatanpoker.com.frameworklib.security.AESUtil;
import tatanpoker.com.frameworklib.security.RSAUtil;

/*
Protocol is length, encryptiontype (ordinal), message.
 */
public class ConnectionThread extends Thread {
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

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
                int ordinal = dataInputStream.readInt();
                EncryptionType encryptionType = EncryptionType.values()[ordinal];
                System.out.println("Recieved length: " + length);
                if(length>0) {
                    byte[] message = new byte[length];
                    dataInputStream.readFully(message, 0, message.length); // read the message
                    Packet packet;
                    switch (encryptionType) {
                        default:
                        case AES:
                            SecretKey secretKey = Framework.getNetwork().getLocal().getSymmetricKey();
                            packet = AESUtil.decrypt(secretKey.getEncoded(), message);
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
                    packet.recieve(socket, this);
                }
            } catch (IOException e) {
                Framework.getLogger().severe("Extra packet sent(?");
                e.printStackTrace();
                break;
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
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
            PacketSender packetSender = new PacketSender(recognizePacket);
            Thread packetThread = new Thread(packetSender);
            packetThread.start();
        } else {
            throw new DeviceOfflineException(socket.getInetAddress().toString());
        }
    }

    class PacketSender implements Runnable {
        private Packet packet;

        PacketSender(Packet packet) {
            this.packet = packet;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public synchronized void sendPacket(Packet packet) throws IOException, InvalidIDException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException {
            if (!socket.isClosed()) {
                if(dataOutputStream == null){
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                }
                byte[] data;
                Framework.getLogger().info("Sending packet: " + packet.getClass().getName() + " through socket.");
                NetworkComponent component = Framework.getNetwork().getComponent(ConnectionThread.this);
                switch (packet.getEncryptionType()) {
                    case AES:
                        data = AESUtil.encrypt(packet, component.getSymmetricKey());
                        break;
                    case RSA:
                        data = RSAUtil.encrypt(packet, component.getPublicKey());
                        break;
                    case NONE:
                        data = SerializationUtils.serialize(packet);
                        break;
                    default:
                        data = AESUtil.encrypt(packet, component.getSymmetricKey());
                        break;
                }
                assert data != null;
                System.out.println("Sending length: " + data.length);
                dataOutputStream.writeInt(data.length); //Write length
                dataOutputStream.writeInt(packet.getEncryptionType().ordinal());
                dataOutputStream.write(data); //Write data
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run(){
            try {
                sendPacket(packet);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (InvalidIDException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            }
        }
    }
}