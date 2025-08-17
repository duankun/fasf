package org.fasf.interceptor.encrypt;

import org.fasf.Const;
import org.fasf.http.HttpRequest;
import org.fasf.interceptor.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;

public abstract class EncryptRequestInterceptor implements RequestInterceptor {
    @Value("${fasf.api.encrypt.type}")
    private String encryptType;

    @Override
    public void intercept(HttpRequest request){
        request.addHeader(Const.ENCRYPT_TYPE, encryptType);
        interceptorInternal(request);
    }

    abstract void interceptorInternal(HttpRequest request);
}
