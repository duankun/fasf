package org.fasf.sctel.interceptor;

import org.fasf.core.http.HttpResponse;
import org.fasf.core.interceptor.ResponseInterceptor;
import org.fasf.sctel.util.SMUtils;

public class DecryptResponseInterceptor implements ResponseInterceptor {
    private final AbstractRequestContext requestContext;

    public DecryptResponseInterceptor(AbstractRequestContext requestContext) {
        this.requestContext = requestContext;
    }
    @Override
    public void intercept(HttpResponse httpResponse) {
        String decrypt = SMUtils.SM4Decrypt(httpResponse.getBodyAsString(), requestContext.getSm4Key());
        httpResponse.setBody(decrypt.getBytes());
    }
}
