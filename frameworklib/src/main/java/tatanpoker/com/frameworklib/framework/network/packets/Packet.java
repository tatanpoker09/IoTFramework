package tatanpoker.com.frameworklib.framework.network.packets;

import java.io.Serializable;
import java.net.Socket;
import java.util.UUID;

import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.db.PacketEntity;
import tatanpoker.com.frameworklib.framework.network.packets.types.PacketType;

public abstract class Packet implements Serializable {
    private UUID uuid = UUID.randomUUID(); //Everytime a packet is created, it is assigned a random uuid.
    private EncryptionType encryptionType;
    private PacketType packetType;

    public Packet(PacketType packetType, EncryptionType encryptionType) {
        this.packetType = packetType;
        this.encryptionType = encryptionType;
    }

    public Packet(PacketType packetType) {
        this(packetType, EncryptionType.AES);
    }

    public Packet(EncryptionType encryptionType) {
        this(PacketType.SIMPLE, encryptionType);
    }

    public Packet() {
        this(PacketType.SIMPLE, EncryptionType.AES);
    }

    /**
     * Called whenever a packet is recieved from a socket.
     * Only processes packets once.
     *
     * @param socket
     */
    public final void recieve(Socket socket, ConnectionThread clientThread) {
        if (isNotProcessed()) {
            process(socket, clientThread);
            PacketEntity packetEntity = new PacketEntity(uuid.toString(), getClass().getName(), false);
            Framework.getDatabase().packetDao().insert(packetEntity);
        }
    }

    public final void recieve(String endpointId) {
        if (isNotProcessed()) {
            process(endpointId);
            PacketEntity packetEntity = new PacketEntity(uuid.toString(), getClass().getName(), false);
            Framework.getDatabase().packetDao().insert(packetEntity);
        }
    }

    protected abstract void process(String endpointId);

    public abstract void process(Socket socket, ConnectionThread clientThread);

    /**
     * Checks if a packet has been processed already.
     *
     * @return true if the packet was processed
     */
    private boolean isNotProcessed() {
        final PacketEntity packet = Framework.getDatabase().packetDao().getByUUID(uuid.toString());
        return packet == null; //This means the packet was not found, therefore it was not processed.
    }

    public EncryptionType getEncryptionType() {
        return encryptionType;
    }

    public void setEncryptionType(EncryptionType encrypt) {
        this.encryptionType = encrypt;
    }
}
