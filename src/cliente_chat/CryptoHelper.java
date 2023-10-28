package cliente_chat;



/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author guama
 */

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class CryptoHelper {

    private static final String ALGORITHM = "AES";
    private static final byte[] KEY = "T1h2I3s4I5s6A7K8".getBytes(); // Clave de 16 bytes.

    public static String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(KEY, ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error encriptando", e);
        }
    }

    public static String decrypt(String encryptedData) {
        try {
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(KEY, ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error desencriptando", e);
        }
    }
}

