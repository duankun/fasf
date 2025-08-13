package com.freemaker.fasf.interceptor;

import com.freemaker.fasf.http.GetRequest;
import com.freemaker.fasf.http.HttpRequest;
import com.freemaker.fasf.http.PostRequest;
import com.freemaker.fasf.model.Consts;
import com.freemaker.fasf.util.AesUtils;
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
