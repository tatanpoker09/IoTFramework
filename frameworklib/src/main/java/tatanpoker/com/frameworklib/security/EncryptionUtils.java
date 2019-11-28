package tatanpoker.com.frameworklib.security;

import android.annotation.TargetApi;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import tatanpoker.com.frameworklib.framework.NetworkComponent;
import tatanpoker.com.frameworklib.framework.network.packets.EncryptionType;

public class EncryptionUtils {
    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static byte[] encrypt(NetworkComponent component, Serializable object, EncryptionType encryptionType) {
        byte[] data = new byte[0];
        switch (encryptionType) {
            case AES:
            default:
                try {
                    data = AESUtil.encrypt(object, component.getSymmetricKey().getEncoded());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case RSA:
                try {
                    data = RSAUtil.encrypt(object, component.getPublicKey());
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
                data = SerializationUtils.serialize(object);
                break;
        }
        return data;
    }

    public static byte[] encrypt(NetworkComponent component, byte[] data, EncryptionType encryptionType) {

        byte[] encryptedData = new byte[0];
        switch (encryptionType) {
            case AES:
            default:
                try {
                    //TODO TEST THIS TO SEE IF I CAN USE BYTE[] AS SERIALIZABLE OBJECT.
                    encryptedData = AESUtil.encrypt(data, component.getSymmetricKey().getEncoded());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case RSA:
                try {
                    encryptedData = RSAUtil.encrypt(data, component.getPublicKey());
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
                encryptedData = data;
                break;
        }
        return encryptedData;
    }
}
