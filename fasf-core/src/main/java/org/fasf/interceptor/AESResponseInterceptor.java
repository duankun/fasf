package org.fasf.interceptor;


import org.fasf.http.HttpResponse;
import org.fasf.util.AesUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class AESResponseInterceptor implements ResponseInterceptor, InitializingBean {
    @Value("${fasf.api.encrypt.aesKey}")
    private String aesKey;
    private SecretKey aesSecretKey;
    @Override
    public void intercept(HttpResponse httpResponse) {
        try {
            httpResponse.setBody(AesUtils.decrypt(httpResponse.getBodyAsString(), aesSecretKey).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.aesSecretKey = AesUtils.generateKey(aesKey);
    }
}
