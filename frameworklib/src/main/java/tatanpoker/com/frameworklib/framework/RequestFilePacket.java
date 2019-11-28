package tatanpoker.com.frameworklib.framework;

import java.net.Socket;

import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.EncryptionType;
import tatanpoker.com.frameworklib.framework.network.packets.types.SimplePacket;

public class RequestFilePacket extends SimplePacket {
    private final int id_to;
    private final int id_from;
    private final String fileName;

    public RequestFilePacket(int id_from, int id_to, String fileName) {
        super(EncryptionType.AES);
        this.id_to = id_to;
        this.id_from = id_from;
        this.fileName = fileName;
    }


    @Override
    protected void process(String endpointId) {

    }


    @Override
    public void process(Socket socket, ConnectionThread connectionThread) {

    }
}
