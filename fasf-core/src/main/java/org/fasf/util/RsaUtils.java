package org.fasf.util;


import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RsaUtils {
    private static final String ALGORITHM = "RSA";
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final int KEY_SIZE = 2048; // 密钥长度

    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE);
        return keyPairGenerator.generateKeyPair();
    }

    public static PublicKey getPublicKey(byte[] publicKeyBytes) {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance(ALGORITHM);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PrivateKey getPrivateKey(byte[] privateKeyBytes) {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance(ALGORITHM);
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String publicKeyToString(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public static String privateKeyToString(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    public static PublicKey stringToPublicKey(String publicKeyString) {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyString);
        return getPublicKey(keyBytes);
    }

    public static PrivateKey stringToPrivateKey(String privateKeyString) {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyString);
        return getPrivateKey(keyBytes);
    }

    public static String encrypt(String data, PublicKey publicKey) {
        Cipher cipher;
        byte[] dataBytes;
        byte[] encryptedData;
        try {
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            dataBytes = data.getBytes(DEFAULT_CHARSET);
            encryptedData = cipher.doFinal(dataBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public static String decrypt(String encryptedData, PrivateKey privateKey) {
        Cipher cipher;
        byte[] decodedData;
        byte[] decryptedData;
        try {
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            decodedData = Base64.getDecoder().decode(encryptedData);
            decryptedData = cipher.doFinal(decodedData);
            return new String(decryptedData, DEFAULT_CHARSET);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String sign(String data, PrivateKey privateKey) {
        Signature signature;
        byte[] signedData;
        try {
            signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(data.getBytes(DEFAULT_CHARSET));
            signedData = signature.sign();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(signedData);
    }

    public static boolean verify(String data, String sign, PublicKey publicKey) {
        Signature signature;
        byte[] signBytes;
        try {
            signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(data.getBytes(DEFAULT_CHARSET));
            signBytes = Base64.getDecoder().decode(sign);
            return signature.verify(signBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try {
            // 生成RSA密钥对
            KeyPair keyPair = RsaUtils.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            // 原始数据
            String originalData = "Hello, RSA encryption!";

            // 加密
            String encryptedData = RsaUtils.encrypt(originalData, publicKey);
            System.out.println("加密后: " + encryptedData);

            // 解密
            String decryptedData = RsaUtils.decrypt(encryptedData, privateKey);
            System.out.println("解密后: " + decryptedData);

            // 签名
            String signature = RsaUtils.sign(originalData, privateKey);
            System.out.println("签名: " + signature);

            // 验证签名
            boolean isValid = RsaUtils.verify(originalData, signature, publicKey);
            System.out.println("签名验证结果: " + isValid);

            // 密钥转换示例
            String publicKeyString = RsaUtils.publicKeyToString(publicKey);
            String privateKeyString = RsaUtils.privateKeyToString(privateKey);

            System.out.println("公钥Base64: " + publicKeyString);
            System.out.println("私钥Base64: " + privateKeyString);

            // 从字符串还原密钥
            PublicKey restoredPublicKey = RsaUtils.stringToPublicKey(publicKeyString);
            PrivateKey restoredPrivateKey = RsaUtils.stringToPrivateKey(privateKeyString);

            // 使用还原的密钥进行加解密
            String encryptedWithRestoredKey = RsaUtils.encrypt(originalData, restoredPublicKey);
            String decryptedWithRestoredKey = RsaUtils.decrypt(encryptedWithRestoredKey, restoredPrivateKey);
            System.out.println("使用还原密钥解密: " + decryptedWithRestoredKey);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
