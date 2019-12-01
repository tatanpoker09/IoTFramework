package tatanpoker.com.frameworklib.framework.network.packets;

import java.net.Socket;

import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.types.SimplePacket;

public class AcquireSemaphorePacket extends SimplePacket {
    private NetworkComponent component;

    public AcquireSemaphorePacket(NetworkComponent component) {
        super(EncryptionType.AES);
        this.component = component;
    }

    public AcquireSemaphorePacket() {
        super(EncryptionType.AES);
    }

    @Override
    protected void process(String endpointId) {

    }

    @Override
    public void process(Socket socket, ConnectionThread clientThread) {
        try {
            if (component == null) {
                Framework.getNetwork().getSemaphore().acquire();
            } else {
                component.getSemaphore().acquire();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
