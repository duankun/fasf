package org.fasf.interceptor;

import org.fasf.core.http.HttpRequest;
import org.fasf.core.interceptor.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;

public class AuthorizationInterceptor implements RequestInterceptor {
    @Value("${fasf.api.auth.authorization.name}")
    private String authorizationName;
    @Value("${fasf.api.auth.authorization.value}")
    private String authorizationValue;
    @Override
    public void intercept(HttpRequest request) {
        request.addHeader(authorizationName, authorizationValue);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
