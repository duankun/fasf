package org.fasf.interceptor.encrypt;

import org.fasf.http.GetRequest;
import org.fasf.http.HttpRequest;
import org.fasf.http.PostRequest;
import org.fasf.util.RsaUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.security.PublicKey;
import java.util.Map;

public class RSAEncryptRequestInterceptor extends EncryptRequestInterceptor implements InitializingBean {
    @Value("${fasf.api.encrypt.serverPublicKey}")
    private String serverPublicKey;
    private PublicKey publicKey;

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
                    String encryptedValue = RsaUtils.encrypt(value, publicKey);
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
            postRequest.setBody(RsaUtils.encrypt(body, publicKey));
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
        Assert.notNull(serverPublicKey, "serverPublicKey can not be null");
        this.publicKey = RsaUtils.stringToPublicKey(serverPublicKey);
    }
}
