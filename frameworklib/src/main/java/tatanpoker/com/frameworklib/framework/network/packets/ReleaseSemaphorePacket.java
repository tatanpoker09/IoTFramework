package tatanpoker.com.frameworklib.framework.network.packets;

import java.net.Socket;

import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.types.SimplePacket;

public class ReleaseSemaphorePacket extends SimplePacket {

    private NetworkComponent component;

    public ReleaseSemaphorePacket(NetworkComponent component) {
        super(EncryptionType.AES);
        this.component = component;
    }

    public ReleaseSemaphorePacket() {
        super(EncryptionType.AES);
    }

    @Override
    protected void process(String endpointId) {

    }

    @Override
    public void process(Socket socket, ConnectionThread clientThread) {
        if (component == null) {
            Framework.getNetwork().getSemaphore().release();
        } else {
            component.getSemaphore().release();
        }
    }
}
