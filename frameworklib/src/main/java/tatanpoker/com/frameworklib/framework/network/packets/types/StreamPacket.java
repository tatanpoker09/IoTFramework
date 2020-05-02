package tatanpoker.com.frameworklib.framework.network.packets.types;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.EncryptionType;
import tatanpoker.com.frameworklib.framework.network.packets.Packet;

public abstract class StreamPacket extends Packet {
    private static final int CHUNK_SIZE = 1024;

    public StreamPacket(EncryptionType encryptionType) {
        super(PacketType.STREAM, encryptionType);
    }

    @Override
    public final void sendPacket(DataOutputStream dataOutputStream, ConnectionThread connectionThread) {
        streamPacket(dataOutputStream, connectionThread);
    }



    public void streamPacket(DataOutputStream dataOutputStream, ConnectionThread connectionThread){
        try {
            dataOutputStream.writeUTF("play");
            //We separate in chunks and send little by little.
            byte[] myBuffer = new byte[CHUNK_SIZE];
            int bytesRead = 0;
            BufferedInputStream in = new BufferedInputStream(getInputStream());
            while ((bytesRead = in.read(myBuffer, 0, CHUNK_SIZE)) != -1) {
                dataOutputStream.write(myBuffer);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract InputStream getInputStream();
}
