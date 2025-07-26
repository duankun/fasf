package com.freemaker.fasf.interceptor;

import com.freemaker.fasf.http.HttpRequest;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author duankun
 * @date 2025/7/26
 */
public class RequestBodyEncryptInterceptor implements RequestInterceptor{
    @Value("${fasf.encrypt.aes.key}")
    private String aesKey;
    @Override
    public void intercept(HttpRequest request) {

    }
}
