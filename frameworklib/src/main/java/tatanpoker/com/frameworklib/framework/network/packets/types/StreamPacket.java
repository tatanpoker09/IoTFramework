package tatanpoker.com.frameworklib.framework.network.packets.types;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.UUID;

import tatanpoker.com.frameworklib.exceptions.DeviceOfflineException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.EncryptionType;
import tatanpoker.com.frameworklib.framework.network.packets.Packet;
import tatanpoker.com.frameworklib.framework.network.packets.StreamReadyPacket;
import tatanpoker.com.frameworklib.framework.network.streaming.FileStream;
import tatanpoker.com.frameworklib.security.EncryptionUtils;

public abstract class StreamPacket extends Packet {
    private static final int CHUNK_SIZE = 4096;

    private UUID streamPacketUUID;
    protected int packetCount;

    private transient boolean streaming;

    public StreamPacket(EncryptionType encryptionType, UUID uuid) {
        super(PacketType.STREAM, encryptionType);
        this.streamPacketUUID = uuid;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public final void sendPacket(DataOutputStream dataOutputStream, ConnectionThread connectionThread) {
        byte[] data;
        Framework.getLogger().info("Sending packet: " + getClass().getName() + " through socket.");
        NetworkComponent component;
        try {
            component = Framework.getNetwork().getComponent(connectionThread);
        } catch (DeviceOfflineException e) {
            e.printStackTrace();
            return;
        }
        try {
            data = EncryptionUtils.encrypt(component, this, this.getEncryptionType());
            dataOutputStream.writeInt(data.length); //Write length
            dataOutputStream.writeInt(this.getEncryptionType().ordinal());
            dataOutputStream.writeInt(Framework.getNetwork().getLocal().getId());
            dataOutputStream.write(data); //Write data
        } catch (IOException e) {
            e.printStackTrace();
        }
        streamPacket(dataOutputStream, connectionThread);
    }


        //TODO MAYBE DO THIS IN ANOTHER THREAD.
    public synchronized void streamPacket(DataOutputStream dataOutputStream, ConnectionThread connectionThread){
        streaming = true;
        NetworkComponent component = null;
        try {
            component = Framework.getNetwork().getComponent(connectionThread);
        } catch (DeviceOfflineException e) {
            e.printStackTrace();
        }
        try {
            //We separate in chunks and send little by little.
            byte[] myBuffer = new byte[CHUNK_SIZE];
            int bytesRead = 0;
            BufferedInputStream in = new BufferedInputStream(getInputStream());
            int i = 0;
            while ((bytesRead = in.read(myBuffer, 0, CHUNK_SIZE)) != -1) {
                SubStreamPacket subStreamPacket = new SubStreamPacket(streamPacketUUID, myBuffer, i++);
                assert component != null;
                Framework.getNetwork().sendPacket(component, subStreamPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        streaming = false;
    }

    public abstract InputStream getInputStream();

    @Override
    public void process(Socket socket, ConnectionThread clientThread) {
        //Open
        Framework.getNetwork().getStreamingManager().addFileStream(new FileStream(streamPacketUUID, packetCount));
        try {
            NetworkComponent component = Framework.getNetwork().getComponent(clientThread);
            Framework.getNetwork().sendPacket(component, new StreamReadyPacket());
        } catch (DeviceOfflineException e) {
            e.printStackTrace();
        }
    }

    public long getSize(){
        return 0;
    }

    public int getPacketCount(){
        long packetCount = (getSize()/CHUNK_SIZE);
        int remainder = (int) (getSize()- (CHUNK_SIZE * packetCount));
        if(remainder!=0){
            packetCount+=1;
        }
        System.out.println("Packet count to send: "+packetCount);
        return (int) packetCount;
    }
}

