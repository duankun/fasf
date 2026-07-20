package org.fasf.sctel.interceptor;

import org.fasf.core.http.HttpRequest;
import org.fasf.core.http.PostRequest;
import org.fasf.core.interceptor.RequestInterceptor;
import org.fasf.sctel.util.SMUtils;


public class AuthorizationRequestInterceptor implements RequestInterceptor {
    private final AbstractRequestContext requestContext;

    public AuthorizationRequestInterceptor(AbstractRequestContext requestContext) {
        this.requestContext = requestContext;
    }
    @Override
    public void intercept(HttpRequest request) {
        PostRequest postRequest = (PostRequest) request;
        String sign = SMUtils.SM2Encrypt(requestContext.getSm4Key(), requestContext.getSm2PublicKey(), CodeType.Hex);
        postRequest.addHeader("Message-Sign", sign);
        String token = String.format("%s %s", "Bearer", requestContext.getAccessToken());
        postRequest.addHeader("Authorization", token);
        postRequest.addHeader("Tenant-Id", requestContext.getTenantId());
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
