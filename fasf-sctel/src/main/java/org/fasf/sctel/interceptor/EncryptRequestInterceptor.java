package org.fasf.sctel.interceptor;

import org.fasf.core.http.HttpRequest;
import org.fasf.core.http.PostRequest;
import org.fasf.core.interceptor.RequestInterceptor;
import org.fasf.sctel.util.SMUtils;

public class EncryptRequestInterceptor implements RequestInterceptor {
    private final AbstractRequestContext requestContext;

    public EncryptRequestInterceptor(AbstractRequestContext requestContext) {
        this.requestContext = requestContext;
    }
    @Override
    public void intercept(HttpRequest request) {
        PostRequest postRequest = (PostRequest) request;
        String paramEncrypt = SMUtils.SM4Encrypt(postRequest.getBody(), requestContext.getSm4Key(), CodeType.Hex);
        String queryString = "{\"queryChain\":\"" + paramEncrypt + "\"}";
        postRequest.setBody(queryString);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
