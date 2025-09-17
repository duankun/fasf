package org.fasf.interceptor;


import jakarta.annotation.PostConstruct;
import org.fasf.core.http.HttpResponse;
import org.fasf.core.interceptor.ResponseInterceptor;
import org.fasf.core.util.AesUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import javax.crypto.SecretKey;

public class AESResponseInterceptor implements ResponseInterceptor {
    @Value("${fasf.api.encrypt.aesKey}")
    private String aesKey;
    private SecretKey aesSecretKey;

    @Override
    public void intercept(HttpResponse httpResponse) {
        httpResponse.setBody(AesUtils.decrypt(httpResponse.getBodyAsString(), aesSecretKey).getBytes(httpResponse.getCharset()));
    }

    @PostConstruct
    public void setUpSecretKey() {
        Assert.notNull(aesKey, "aesKey can not be null");
        this.aesSecretKey = AesUtils.generateKey(aesKey);
    }
}
