package tatanpoker.com.frameworklib.framework.network.packets;

import java.net.Socket;

import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.types.SimplePacket;

/*
 * TODO FINISH THIS PACKET.
 */
public class ListFilesPacket extends SimplePacket {
    public ListFilesPacket() {
        super(EncryptionType.AES);
    }

    @Override
    protected void process(String endpointId) {

    }

    @Override
    public void process(Socket socket, ConnectionThread clientThread) {

    }
}
