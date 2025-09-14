package org.fasf.interceptor.encrypt;

import jakarta.annotation.PostConstruct;
import org.fasf.http.HttpRequest;
import org.fasf.util.RsaUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.security.PublicKey;

public class RSAEncryptRequestInterceptor extends EncryptRequestInterceptor {
    @Value("${fasf.api.encrypt.serverPublicKey}")
    private String serverPublicKey;
    private PublicKey publicKey;

    @Override
    public EncryptTypeEnum encryptType() {
        return EncryptTypeEnum.RSA;
    }

    @Override
    public void interceptorInternal(HttpRequest request) {
        super.encryptRequest(request, (v, k) -> RsaUtils.encrypt(v, (PublicKey) k), publicKey);
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @PostConstruct
    public void setUpSecretKey() {
        Assert.notNull(serverPublicKey, "serverPublicKey can not be null");
        this.publicKey = RsaUtils.stringToPublicKey(serverPublicKey);
    }
}
