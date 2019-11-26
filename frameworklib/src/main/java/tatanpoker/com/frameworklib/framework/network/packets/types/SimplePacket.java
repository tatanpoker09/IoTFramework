package tatanpoker.com.frameworklib.framework.network.packets.types;

import tatanpoker.com.frameworklib.framework.network.packets.EncryptionType;
import tatanpoker.com.frameworklib.framework.network.packets.Packet;

public abstract class SimplePacket extends Packet {
    public SimplePacket(EncryptionType encryptionType) {
        super(PacketType.SIMPLE, encryptionType);
    }
}
