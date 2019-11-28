package tatanpoker.com.frameworklib.framework.network.packets.types;

import java.io.DataOutputStream;

import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.EncryptionType;
import tatanpoker.com.frameworklib.framework.network.packets.Packet;

public abstract class StreamPacket extends Packet {
    public StreamPacket(EncryptionType encryptionType) {
        super(PacketType.STREAM, encryptionType);
    }

    @Override
    public void sendPacket(DataOutputStream dataOutputStream, ConnectionThread connectionThread) {

    }

}
