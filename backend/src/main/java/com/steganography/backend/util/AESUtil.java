package com.steganography.backend.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {

    private static final String ALGO = "AES";

    private static SecretKeySpec getKey(String password) {
        byte[] key = new byte[16];
        byte[] passBytes = password.getBytes();

        for (int i = 0; i < key.length; i++) {
            key[i] = i < passBytes.length ? passBytes[i] : 0;
        }

        return new SecretKeySpec(key, ALGO);
    }

    public static String encrypt(String data, String password) throws Exception {
        SecretKeySpec key = getKey(password);
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    }

    public static String decrypt(String data, String password) throws Exception {
        SecretKeySpec key = getKey(password);
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.getDecoder().decode(data)));
    }
}