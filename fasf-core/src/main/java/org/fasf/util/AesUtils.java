package org.fasf.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AesUtils {
    private static final String AES = "AES";
    private static final String AES_ALGORITHM = "AES/ECB/PKCS5Padding";

    public static SecretKey generateKey(String originKey) {
        byte[] keyBytes = originKey.getBytes(StandardCharsets.UTF_8);
        byte[] adjustedKey = new byte[16];
        System.arraycopy(keyBytes, 0, adjustedKey, 0, Math.min(keyBytes.length, adjustedKey.length));
        return new SecretKeySpec(adjustedKey, AES);
    }

    public static String encrypt(String data, String originKey) throws Exception {
        return encrypt(data, generateKey(originKey));
    }

    public static String encrypt(String data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedData, String originKey) throws Exception {
        return decrypt(encryptedData, generateKey(originKey));
    }


    public static String decrypt(String encryptedData, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        try {

            String originalData = "hello fasf";
            String key = "MySecretKey12345"; // 16字节密钥

            // 加密
            String encryptedData = encrypt(originalData, key);
            System.out.println("原始数据: " + originalData);
            System.out.println("加密后数据: " + encryptedData);

            // 解密
            String decryptedData = decrypt(encryptedData, key);
            System.out.println("解密后数据: " + decryptedData);

            /**
             * 原始数据: {"orderId":"123456789"}
             * 加密后数据: xruxTM3VY/aRysgiMjJjBZipkRVh29I/xILYGPUoQXE=
             * 解密后数据: {"orderId":"123456789"}
             */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
