package tatanpoker.com.frameworklib.framework.network.packets;

import java.net.Socket;
import java.security.PublicKey;

import tatanpoker.com.frameworklib.exceptions.DeviceOfflineException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;

public class ConnectionResponsePacket extends Packet {
    private PublicKey publicKey;

    public ConnectionResponsePacket(PublicKey publicKey) {
        super(EncryptionType.NONE);
        this.publicKey = publicKey;
    }
    @Override
    protected void process(String endpointId) {

    }

    @Override
    public void process(Socket socket, ConnectionThread clientThread) {
        Framework.getNetwork().getServer().setPublicKey(publicKey);
        int id = Framework.getNetwork().getLocal().getId();
        AESSymmetricKeyPacket symmetricKeyPacket = new AESSymmetricKeyPacket(id, Framework.getNetwork().getLocal().getSymmetricKey());
        try {
            Framework.getNetwork().getServer().getClientThread().sendPacket(symmetricKeyPacket);
        } catch (DeviceOfflineException e) {
            e.printStackTrace();
        }
    }
}
