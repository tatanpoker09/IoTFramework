package tatanpoker.com.frameworklib.framework.network.packets;

import java.net.Socket;

import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.types.SimplePacket;

public class RequestFileListPacket extends SimplePacket {
    private int id_from;
    private int id_to;

    public RequestFileListPacket() {
        super(EncryptionType.AES);
    }

    @Override
    protected void process(String endpointId) {

    }

    @Override
    public void process(Socket socket, ConnectionThread clientThread) {

    }
}
