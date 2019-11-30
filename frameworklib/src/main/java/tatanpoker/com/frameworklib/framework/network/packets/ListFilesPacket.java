package tatanpoker.com.frameworklib.framework.network.packets;

import java.io.File;
import java.net.Socket;

import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.types.SimplePacket;

/*
 * TODO FINISH THIS PACKET. KINDA LIKE REQUEST FROM NETWORK COMPONENT FILE LIST. THEN RETURN THIS.
 */
public class ListFilesPacket extends SimplePacket {
    private final int id_from;
    private final int id_to;
    private final File[] files;

    public ListFilesPacket(int id_from, int id_to, File[] files) {
        super(EncryptionType.AES);
        this.id_from = id_from;
        this.id_to = id_to;
        this.files = files;
    }

    @Override
    protected void process(String endpointId) {

    }

    @Override
    public void process(Socket socket, ConnectionThread clientThread) {

    }
}
