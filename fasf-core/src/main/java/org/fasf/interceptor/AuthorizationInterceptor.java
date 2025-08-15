package org.fasf.interceptor;

import org.fasf.http.HttpRequest;

public class AuthorizationInterceptor implements RequestInterceptor {
    @Override
    public void intercept(HttpRequest request) {
        request.addHeader("Authorization", "Bearer 123456");
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
