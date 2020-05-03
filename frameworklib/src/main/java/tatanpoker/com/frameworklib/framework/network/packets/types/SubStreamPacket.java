package tatanpoker.com.frameworklib.framework.network.packets.types;

import java.net.Socket;
import java.util.UUID;

import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.EncryptionType;
import tatanpoker.com.frameworklib.framework.network.streaming.FileStream;

public class SubStreamPacket extends SimplePacket{
    private UUID streamPacketUUID;
    private byte[] partialData;


    SubStreamPacket(UUID streamPacketUUID, byte[] partialData) {
        super(EncryptionType.AES);
        this.streamPacketUUID = streamPacketUUID;
        this.partialData = partialData;
    }


    @Override
    protected void process(String endpointId) {

    }

    @Override
    public void process(Socket socket, ConnectionThread clientThread) {
        FileStream fileStream = Framework.getNetwork().getStreamingManager().getFileStream(streamPacketUUID);
        fileStream.addSubPacket(this);
    }

    public byte[] getData() {
        return partialData;
    }
}
