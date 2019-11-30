package tatanpoker.com.frameworklib.framework.network.packets;

import java.net.Socket;
import java.security.PublicKey;

import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.types.SimplePacket;

public class ConnectionResponsePacket extends SimplePacket {
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
        Framework.getNetwork().sendPacket(Framework.getNetwork().getServer(), symmetricKeyPacket);
        //Framework.getNetwork().getServer().getClientThread().sendPacket(symmetricKeyPacket); deprecated call.
    }
}
