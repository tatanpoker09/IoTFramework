package tatanpoker.com.frameworklib.framework.network.packets.types;

import android.annotation.TargetApi;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.apache.commons.lang3.SerializationUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import tatanpoker.com.frameworklib.exceptions.InvalidIDException;
import tatanpoker.com.frameworklib.framework.Framework;
import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.network.ConnectionThread;
import tatanpoker.com.frameworklib.framework.network.packets.EncryptionType;
import tatanpoker.com.frameworklib.framework.network.packets.Packet;
import tatanpoker.com.frameworklib.security.AESUtil;
import tatanpoker.com.frameworklib.security.RSAUtil;

public abstract class SimplePacket extends Packet {
    public SimplePacket(EncryptionType encryptionType) {
        super(PacketType.SIMPLE, encryptionType);
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public final void sendPacket(DataOutputStream dataOutputStream, ConnectionThread connectionThread) {
        byte[] data = null;
        Framework.getLogger().info("Sending packet: " + getClass().getName() + " through socket.");

        NetworkComponent component;
        switch (getEncryptionType()) {
            case AES:
                try {
                    component = Framework.getNetwork().getComponent(connectionThread);
                } catch (InvalidIDException e) {
                    e.printStackTrace();
                    return;
                }
                try {
                    data = AESUtil.encrypt(this, component.getSymmetricKey().getEncoded());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case RSA:
                try {
                    component = Framework.getNetwork().getComponent(connectionThread);
                } catch (InvalidIDException e) {
                    e.printStackTrace();
                    return;
                }
                try {
                    data = RSAUtil.encrypt(this, component.getPublicKey());
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                break;
            case NONE:
                data = SerializationUtils.serialize(this);
                break;
            default:
                try {
                    component = Framework.getNetwork().getComponent(connectionThread);
                } catch (InvalidIDException e) {
                    e.printStackTrace();
                    return;
                }
                try {
                    data = AESUtil.encrypt(this, component.getSymmetricKey().getEncoded());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        assert data != null;
        try {
            dataOutputStream.writeInt(data.length); //Write length
            dataOutputStream.writeInt(getEncryptionType().ordinal());
            dataOutputStream.writeInt(Framework.getNetwork().getLocal().getId());
            dataOutputStream.write(data); //Write data
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
