package tatanpoker.com.frameworklib.framework.network.packets.types;

import tatanpoker.com.frameworklib.framework.network.packets.EncryptionType;
import tatanpoker.com.frameworklib.framework.network.packets.Packet;

public abstract class StreamPacket extends Packet {
    public StreamPacket(EncryptionType encryptionType) {
        super(PacketType.STREAM, encryptionType);
    }
}
