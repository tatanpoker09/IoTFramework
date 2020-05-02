package tatanpoker.com.frameworklib.framework.network.packets.types;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import tatanpoker.com.frameworklib.exceptions.DeviceOfflineException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.EncryptionType;
import tatanpoker.com.frameworklib.framework.network.packets.Packet;
import tatanpoker.com.frameworklib.security.EncryptionUtils;

public abstract class StreamPacket extends Packet {
    private static final int CHUNK_SIZE = 1024;

    private InputStream inputStream;
    private boolean streaming;

    public StreamPacket(EncryptionType encryptionType) {
        super(PacketType.STREAM, encryptionType);
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



    public void streamPacket(DataOutputStream dataOutputStream, ConnectionThread connectionThread){
        try {
            //We separate in chunks and send little by little.
            byte[] myBuffer = new byte[CHUNK_SIZE];
            int bytesRead = 0;
            BufferedInputStream in = new BufferedInputStream(getOutputInputStream());
            while ((bytesRead = in.read(myBuffer, 0, CHUNK_SIZE)) != -1) {
                dataOutputStream.write(myBuffer);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract InputStream getOutputInputStream(); //Byte Inputstream to feed as output.

    @Override
    public void process(Socket socket, ConnectionThread clientThread) {
        //Open
        this.streaming = true;
    }
}
