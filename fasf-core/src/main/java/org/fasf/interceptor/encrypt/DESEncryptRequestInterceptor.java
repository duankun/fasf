package org.fasf.interceptor.encrypt;

import jakarta.annotation.PostConstruct;
import org.fasf.http.HttpRequest;
import org.fasf.util.DesUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import javax.crypto.SecretKey;

public class DESEncryptRequestInterceptor extends EncryptRequestInterceptor {
    @Value("${fasf.api.encrypt.desKey}")
    private String desKey;
    private SecretKey desSecretKey;

    @Override
    public EncryptTypeEnum encryptType() {
        return EncryptTypeEnum.DES;
    }

    @Override
    public void interceptorInternal(HttpRequest request) {
        super.encryptRequest(request, (v, k) -> DesUtils.encrypt(v, (SecretKey) k), desSecretKey);
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @PostConstruct
    public void setUpSecretKey() {
        Assert.notNull(desKey, "desKey can not be null");
        this.desSecretKey = DesUtils.generateKey(desKey);
    }
}
