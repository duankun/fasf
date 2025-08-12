package com.freemaker.fasf.interceptor;

import com.freemaker.fasf.http.HttpRequest;

public class AuthInterceptor implements RequestInterceptor{
    @Override
    public void intercept(HttpRequest request) {
        request.addHeader("Authorization", "Bearer 123456");
        request.addHeader("Content-Type", "application/json");
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
