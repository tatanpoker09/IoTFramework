package tatanpoker.com.frameworklib.framework.network.packets.types;

import java.io.DataOutputStream;

import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.EncryptionType;
import tatanpoker.com.frameworklib.framework.network.packets.Packet;

public abstract class FilePacket extends Packet {
    public FilePacket(EncryptionType encryptionType) {
        super(PacketType.FILE, encryptionType);
    }

    @Override
    public void sendPacket(DataOutputStream dataOutputStream, ConnectionThread connectionThread) {

    }
}
