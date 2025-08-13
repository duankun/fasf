package org.fasf.interceptor;

import org.fasf.http.HttpRequest;
import org.fasf.model.Consts;
import org.springframework.beans.factory.annotation.Value;

public abstract class EncryptRequestInterceptor implements RequestInterceptor{
    @Value("${fasf.remoter.encrypt.type}")
    private String encryptType;

    @Override
    public void intercept(HttpRequest request){
        request.addHeader(Consts.ENCRYPT_TYPE, encryptType);
        interceptorInternal(request);
    }

    abstract void interceptorInternal(HttpRequest request);
}
