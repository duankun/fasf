package org.fasf.interceptor;


import org.fasf.util.AesUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;

public class AESResponseInterceptor implements ResponseInterceptor, InitializingBean {
    @Value("${fasf.api.encrypt.aesKey}")
    private String aesKey;
    private SecretKey aesSecretKey;
    @Override
    public String intercept(String originResponseBody) {
        try {
            return AesUtils.decrypt(originResponseBody, aesSecretKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.aesSecretKey = AesUtils.generateKey(aesKey);
    }
}
