package tatanpoker.com.frameworklib.security;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import tatanpoker.com.frameworklib.framework.network.packets.Packet;

public class RSAUtil {
        @RequiresApi(api = Build.VERSION_CODES.O)
        public static PublicKey getPublicKey(String base64PublicKey){
            PublicKey publicKey = null;
            try{
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                publicKey = keyFactory.generatePublic(keySpec);
                return publicKey;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            }
            return publicKey;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public static PrivateKey getPrivateKey(String base64PrivateKey){
            PrivateKey privateKey = null;
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey.getBytes()));
            KeyFactory keyFactory = null;
            try {
                keyFactory = KeyFactory.getInstance("RSA");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            }
            try {
                privateKey = keyFactory.generatePrivate(keySpec);
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            }
            return privateKey;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public static byte[] encrypt(Serializable object, PublicKey publicKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] data = SerializationUtils.serialize(object);
            return cipher.doFinal(data);
        }

        public static Packet decrypt(byte[] data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return SerializationUtils.deserialize(cipher.doFinal(data));
        }
}