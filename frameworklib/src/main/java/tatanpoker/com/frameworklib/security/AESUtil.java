package tatanpoker.com.frameworklib.security;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
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

    private static final String ALGO = "AES";

    public static byte[] encrypt(Serializable object, byte[] secretKey) throws Exception {
        byte[] data = SerializationUtils.serialize(object);
        Key key = getKey(secretKey);
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(data);
        return encVal;
    }

    public static Packet decrypt(byte[] encryptedData, byte[] secretKey) throws Exception {
        Key key = getKey(secretKey);
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);

        byte[] decValue = c.doFinal(encryptedData);
        return SerializationUtils.deserialize(decValue);
    }

    private static Key getKey(byte[] secretKey) throws Exception {
        Key key = new SecretKeySpec(secretKey, ALGO);
        return key;
    }
}
