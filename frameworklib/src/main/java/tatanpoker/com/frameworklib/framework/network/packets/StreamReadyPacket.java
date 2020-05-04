package tatanpoker.com.frameworklib.framework.network.packets;

import java.net.Socket;

import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.types.SimplePacket;

public class StreamReadyPacket extends SimplePacket {
    public StreamReadyPacket() {
        super(EncryptionType.AES);
    }

    @Override
    protected void process(String endpointId) {

    }

    @Override
    public void process(Socket socket, ConnectionThread clientThread) {
        Framework.getNetwork().getLocal().getSemaphore().release();
    }
}
