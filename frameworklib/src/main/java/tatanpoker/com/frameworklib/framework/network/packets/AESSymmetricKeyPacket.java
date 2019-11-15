package tatanpoker.com.frameworklib.framework.network.packets;

import org.json.JSONObject;

import java.net.Socket;

import javax.crypto.SecretKey;

import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;

public class AESSymmetricKeyPacket extends Packet {


    private int id;
    private SecretKey symmetricKey;

    public AESSymmetricKeyPacket(int id, SecretKey symmetricKey) {
        this.id = id;
        this.symmetricKey = symmetricKey;
        this.setEncryptionType(EncryptionType.RSA);
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
        try {
            NetworkComponent component = Framework.getNetwork().getComponent(id);
            component.setSymmetricKey(symmetricKey);
        } catch (InvalidIDException e) {
            e.printStackTrace();
        }
    }
}
