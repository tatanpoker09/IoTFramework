package tatanpoker.com.frameworklib.framework.network.packets;

import org.json.JSONObject;

import java.net.Socket;
import java.security.PublicKey;

import tatanpoker.com.frameworklib.exceptions.DeviceOfflineException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;

public class ConnectionResponsePacket extends Packet {
    private PublicKey publicKey;

    public ConnectionResponsePacket(PublicKey publicKey) {
        this.publicKey = publicKey;
        setEncryptionType(EncryptionType.NONE);
    }

    @Override
    public JSONObject toJson() {
        return null;
    }

    @Override
    void process(String endpointId) {

    }

    @Override
    void process(Socket socket, ConnectionThread clientThread) {
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
