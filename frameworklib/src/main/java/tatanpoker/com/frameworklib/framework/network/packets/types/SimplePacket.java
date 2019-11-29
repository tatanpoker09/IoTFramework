package tatanpoker.com.frameworklib.framework.network.packets.types;

import java.io.DataOutputStream;

import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.EncryptionType;
import tatanpoker.com.frameworklib.framework.network.packets.Packet;

public abstract class SimplePacket extends Packet {
    public SimplePacket(EncryptionType encryptionType) {
        super(PacketType.SIMPLE, encryptionType);
    }

    @Override
    public final void sendPacket(DataOutputStream dataOutputStream, ConnectionThread connectionThread) {

    }
}
