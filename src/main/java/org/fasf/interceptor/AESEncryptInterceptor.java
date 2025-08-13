package org.fasf.interceptor;

import org.fasf.http.GetRequest;
import org.fasf.http.HttpRequest;
import org.fasf.http.PostRequest;
import org.fasf.model.Consts;
import org.fasf.util.AesUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import javax.crypto.SecretKey;
import java.util.Map;

public class AESEncryptInterceptor implements RequestInterceptor, InitializingBean {
    @Value("${fasf.remoter.encrypt.aesKey}")
    private String aesKey;
    private SecretKey aesSecretKey;

    @Override
    public void intercept(HttpRequest request) {
        request.addHeader(Consts.ENCRYPT_TYPE, EncryptTypeEnum.AES.name());
        if (request instanceof GetRequest getRequest) {
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
        } else if (request instanceof PostRequest postRequest) {
            String body = postRequest.getBody();
            try {
                postRequest.setBody(AesUtils.encrypt(body, aesSecretKey));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.aesSecretKey = AesUtils.generateKey(aesKey);
    }
}
