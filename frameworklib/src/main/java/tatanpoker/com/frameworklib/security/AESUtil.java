package tatanpoker.com.frameworklib.security;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import tatanpoker.com.frameworklib.framework.network.packets.Packet;

public class AESUtil {
    public static SecretKey generateKey() {
        KeyGenerator generator = null;
        try {
            generator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (generator != null) {
            generator.init(128); // The AES key size in number of bits
            SecretKey secKey = generator.generateKey();
            return secKey;
        }
        return null;
    }

    public static byte[] encrypt(Serializable object, SecretKey key) {
        Cipher aesCipher = null;
        byte[] byteCipher;
        try {
            aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] data = SerializationUtils.serialize(object);
            byteCipher = aesCipher.doFinal(data);
            return byteCipher;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Packet decrypt(byte[] secretKey, byte[] message) {
        //Convert bytes to AES SecertKey
        SecretKey originalKey = new SecretKeySpec(secretKey, 0, secretKey.length, "AES");
        Cipher aesCipher = null;
        try {
            aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.DECRYPT_MODE, originalKey);
            byte[] bytes = aesCipher.doFinal(message);
            return SerializationUtils.deserialize(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }
}
