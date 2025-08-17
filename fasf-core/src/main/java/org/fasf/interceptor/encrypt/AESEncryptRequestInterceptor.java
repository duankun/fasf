package org.fasf.interceptor.encrypt;

import org.fasf.http.GetRequest;
import org.fasf.http.HttpRequest;
import org.fasf.http.PostRequest;
import org.fasf.util.AesUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.crypto.SecretKey;
import java.util.Map;

public class AESEncryptRequestInterceptor extends EncryptRequestInterceptor implements InitializingBean {
    @Value("${fasf.api.encrypt.aesKey}")
    private String aesKey;
    private SecretKey aesSecretKey;

    @Override
    public void interceptorInternal(HttpRequest request) {
        if (request instanceof GetRequest getRequest) {
            this.getRequestEncrypt(getRequest);
        } else if (request instanceof PostRequest postRequest) {
            this.postRequestEncrypt(postRequest);
        }
    }

    private void getRequestEncrypt(GetRequest getRequest) {
        Map<String, String> queryParameters = getRequest.getQueryParameters();
        if (!CollectionUtils.isEmpty(queryParameters)) {
            queryParameters.forEach((key, value) -> {
                try {
                    String encryptedValue = AesUtils.encrypt(value, aesSecretKey);
                    queryParameters.put(key, encryptedValue);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private void postRequestEncrypt(PostRequest postRequest) {
        String body = postRequest.getBody();
        try {
            postRequest.setBody(AesUtils.encrypt(body, aesSecretKey));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(aesKey, "aesKey can not be null");
        this.aesSecretKey = AesUtils.generateKey(aesKey);
    }
}
