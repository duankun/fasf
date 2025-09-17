package org.fasf.core.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class DesUtils {
    private static final String ALGORITHM = "DES";
    private static final String DEFAULT_CHARSET = "UTF-8";

    public static SecretKey generateKey(String key) {
        SecureRandom secureRandom;
        KeyGenerator keyGenerator;
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(key.getBytes(DEFAULT_CHARSET));
            keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(56, secureRandom);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return keyGenerator.generateKey();
    }

    public static SecretKey generateKey(byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    public static String encrypt(String data, SecretKey key) {
        Cipher cipher;
        byte[] encryptedData;
        try {
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encryptedData = cipher.doFinal(data.getBytes(DEFAULT_CHARSET));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public static String decrypt(String encryptedData, SecretKey key) {
        Cipher cipher;
        byte[] decodedData;
        byte[] decryptedData;
        try {
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            decodedData = Base64.getDecoder().decode(encryptedData);
            decryptedData = cipher.doFinal(decodedData);
            return new String(decryptedData, DEFAULT_CHARSET);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String keyToString(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static SecretKey stringToKey(String keyString) {
        byte[] keyBytes = Base64.getDecoder().decode(keyString);
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    public static void main(String[] args) {
        try {
            // 生成密钥
            SecretKey key = DesUtils.generateKey("mySecretKey");

            // 原始数据
            String originalData = "Hello, DES encryption!";

            // 加密
            String encryptedData = DesUtils.encrypt(originalData, key);
            System.out.println("加密后: " + encryptedData);

            // 解密
            String decryptedData = DesUtils.decrypt(encryptedData, key);
            System.out.println("解密后: " + decryptedData);

            // 密钥转换示例
            String keyString = DesUtils.keyToString(key);
            System.out.println("密钥Base64: " + keyString);

            SecretKey restoredKey = DesUtils.stringToKey(keyString);
            String decryptedWithRestoredKey = DesUtils.decrypt(encryptedData, restoredKey);
            System.out.println("使用还原密钥解密: " + decryptedWithRestoredKey);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
