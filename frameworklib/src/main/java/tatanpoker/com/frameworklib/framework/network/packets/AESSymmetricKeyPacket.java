package tatanpoker.com.frameworklib.framework.network.packets;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.types.SimplePacket;

//We send this as bytes but we don't parse it as a packet to send it as an objectoutputstream.
public class AESSymmetricKeyPacket extends SimplePacket {
    private int id;
    private SecretKey symmetricKey;

    public AESSymmetricKeyPacket(int id, SecretKey symmetricKey) {
        super(EncryptionType.RSA);
        this.id = id;
        this.symmetricKey = symmetricKey;
    }

    public static AESSymmetricKeyPacket fromBytes(byte[] data) {
        byte[] encodedID = Arrays.copyOfRange(data, 0, 4);
        byte[] encodedKey = Arrays.copyOfRange(data, 4, data.length);
        int id = ByteBuffer.wrap(encodedID).getInt();
        SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return new AESSymmetricKeyPacket(id, originalKey);

    }


    public byte[] toBytes() {
        byte[] encodedKey = symmetricKey.getEncoded();
        byte[] encodedID = ByteBuffer.allocate(4).putInt(id).array();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            outputStream.write(encodedID);
            outputStream.write(encodedKey);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

    @Override
    protected void process(String endpointId) {

    }

    @Override
    public void process(Socket socket, ConnectionThread clientThread) {
        try {
            NetworkComponent component = Framework.getNetwork().getComponent(id);
            String encodedKey = Base64.encodeToString(symmetricKey.getEncoded(), Base64.DEFAULT);
            Framework.getLogger().info("Set Secret Key " + encodedKey + " to component: " + component.getId());
            component.setSymmetricKey(symmetricKey);
        } catch (InvalidIDException e) {
            e.printStackTrace();
        }
    }
}
