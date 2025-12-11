package org.fasf.mqyz.interceptor;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.ECKeyUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;

import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.util.Base64;

public class SMUtils {

    /**
     * 获得SM2的公私钥对
     *
     * @param keySize 密钥模（modulus ）长度（单位bit）
     * @return
     */
    public static KeyPair getSM2KeyPair(int keySize) {
        return SecureUtil.generateKeyPair("SM2", keySize);
    }

    /**
     * 获得SM2的公私钥对
     * 密钥模（modulus ）长度 默认1024（单位bit）
     *
     * @return
     */
    public static KeyPair getSM2KeyPair() {
        return SecureUtil.generateKeyPair("SM2");
    }

    /**
     * 获得SM2的公钥
     * 公钥前面的02或者03表示是压缩公钥，04表示未压缩公钥, 04的时候，可以去掉前面的04
     *
     * @param keyPair
     * @return
     */
    public static String getSM2PublicKey(KeyPair keyPair, CodeType codeType) {
        BCECPublicKey bcecPublicKey = (BCECPublicKey) keyPair.getPublic();
        //头部带04  不压缩
        byte[] publicKeys = bcecPublicKey.getQ().getEncoded(false);
        if (CodeType.Hex.codeEquals(codeType.getCode())) {
            return HexUtil.encodeHexStr(publicKeys);
        }
        return Base64.getEncoder().encodeToString(publicKeys);
    }

    /**
     * 获得SM2的私钥
     *
     * @param keyPair
     * @return
     */
    public static String getSM2PrivateKey(KeyPair keyPair, CodeType codeType) {
        if (CodeType.Hex.codeEquals(codeType.getCode())) {
            return HexUtil.encodeHexStr(keyPair.getPrivate().getEncoded());
        }
        return Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
    }

    /**
     * 使用SM2公钥加密,模式默认为：C1C3C2
     *
     * @param content   原始内容
     * @param publicKey 公钥
     * @return
     */
    public static String SM2Encrypt(String content, String publicKey, CodeType codeType) {
        byte[] publicKeyBytes = SecureUtil.decode(publicKey);
        SM2 sm2 = new SM2(null, ECKeyUtil.decodePublicKeyParams(publicKeyBytes));
        byte[] encryptBytes = sm2.encrypt(content, KeyType.PublicKey);

        if (CodeType.Hex.codeEquals(codeType.getCode())) {
            return HexUtil.encodeHexStr(encryptBytes);
        }
        return Base64.getEncoder().encodeToString(encryptBytes);
    }


    /**
     * 使用私钥解密,模式默认为：C1C3C2
     * 使用BC库加解密时密文以04开头，传入的密文前面没有04则补上
     *
     * @param ciphertext 密文
     * @param privateKey 私钥
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String SM2Decrypt(String ciphertext, String privateKey)  {
        byte[] privateKeyBytes = SecureUtil.decode(privateKey);
        SM2 sm2 = new SM2(ECKeyUtil.decodePrivateKeyParams(privateKeyBytes), null);
        return StrUtil.str(sm2.decrypt(SecureUtil.decode(ciphertext), KeyType.PrivateKey), CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * 产生一个SM4的密钥key
     * Base64编码格式
     *
     * @return
     */
    public static String getSM4Key() {
        return Base64.getEncoder().encodeToString(SmUtil.sm4().getSecretKey().getEncoded());
    }

    /**
     * 产生一个SM4的密钥key
     * Base64、Hex两种编码格式
     *
     * @return
     */
    public static String[] generateSM4Key() {
        byte[] sm4SecretKey = SmUtil.sm4().getSecretKey().getEncoded();
        // SM4 Base64编码
        String base64_sm4_key = Base64.getEncoder().encodeToString(sm4SecretKey);
        // SM4 Hex编码
        String hex_sm4_key = HexUtil.encodeHexStr(sm4SecretKey);
        System.out.println("生成SM4编码KEY，Base64编码格式:" + base64_sm4_key + "       Hex编码格式:" + hex_sm4_key);

        return new String[]{base64_sm4_key, hex_sm4_key};
    }

    /**
     * 使用SM4进行原文加密,结果字符串使用Base64编码
     *
     * @param content  原文
     * @param key      密钥key (必须为16进制字符串或Base64表示形式)
     * @param codeType 密文数据编码类型
     * @return
     */
    public static String SM4Encrypt(String content, String key, CodeType codeType) {
        SymmetricCrypto sm4 = SmUtil.sm4(SecureUtil.decode(key));
        if (CodeType.Hex.codeEquals(codeType.getCode())) {
            return sm4.encryptHex(content);
        }
        return sm4.encryptBase64(content);
    }

    /**
     * 使用SM4进行原文加密,密文也是byte[]形式，没有作任何编码
     *
     * @param content 原文
     * @param key     密钥key (必须为16进制字符串或Base64表示形式)
     * @return
     */
    public static String SM4Encrypt(byte[] content, String key, CodeType codeType) {
        SymmetricCrypto sm4 = SmUtil.sm4(SecureUtil.decode(key));
        if (CodeType.Hex.codeEquals(codeType.getCode())) {
            return sm4.encryptHex(content);
        }
        return sm4.encryptBase64(content);
    }

    /**
     * bytes形式进，bytes形式出，不作任何的编码处理
     *
     * @param content 原文
     * @param key     密钥key (必须为16进制字符串或Base64表示形式)
     * @return
     */
    public static byte[] SM4Encrypt(byte[] content, String key) {
        SymmetricCrypto sm4 = SmUtil.sm4(SecureUtil.decode(key));
        return sm4.encrypt(content);
    }

    /**
     * SM4解密操作
     * 无论是Key还是密文，必须为16进制字符串或Base64表示形式
     *
     * @param cipherText 密文
     * @param key        密钥key
     * @return
     */
    public static String SM4Decrypt(String cipherText, String key) {
        SymmetricCrypto sm4 = SmUtil.sm4(SecureUtil.decode(key));
        return sm4.decryptStr(cipherText);
    }

    /**
     * 将Base64字符串转化为Hex的字符串
     *
     * @param base64Str
     * @return
     */
    public static String Base64ToHex(String base64Str) {
        byte[] publicKeyBytes = Base64.getDecoder().decode(base64Str);
        return HexUtil.encodeHexStr(publicKeyBytes);
    }
    /**
     * 将Hex字符串转化为Base64的字符串
     *
     * @param hexStr
     * @return
     */
    public static String HexToBase64(String hexStr) {
        byte[] publicKeyBytes = HexUtil.decodeHex(hexStr);
        return Base64.getEncoder().encodeToString(publicKeyBytes);
    }
}
