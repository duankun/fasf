package org.fasf.interceptor.encrypt;

import jakarta.annotation.PostConstruct;
import org.fasf.core.http.HttpRequest;
import org.fasf.core.interceptor.EncryptTypeEnum;
import org.fasf.core.util.AesUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import javax.crypto.SecretKey;

public class AESEncryptRequestInterceptor extends EncryptRequestInterceptor {
    @Value("${fasf.api.encrypt.aesKey}")
    private String aesKey;
    private SecretKey aesSecretKey;

    @Override
    public EncryptTypeEnum encryptType() {
        return EncryptTypeEnum.AES;
    }

    @Override
    public void interceptorInternal(HttpRequest request) throws Exception {
        super.encryptRequest(request, (v, k) -> AesUtils.encrypt(v, (SecretKey) k), aesSecretKey);
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    @PostConstruct
    public void setUpSecretKey() {
        Assert.notNull(aesKey, "aesKey can not be null");
        this.aesSecretKey = AesUtils.generateKey(aesKey);
    }
}
