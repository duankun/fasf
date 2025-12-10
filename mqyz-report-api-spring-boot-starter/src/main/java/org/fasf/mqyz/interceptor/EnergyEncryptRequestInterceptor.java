package org.fasf.mqyz.interceptor;

import org.fasf.core.http.HttpRequest;
import org.fasf.core.http.PostRequest;
import org.fasf.core.interceptor.RequestInterceptor;

/**
 * @author duankun
 * @date: 2025/11/28
 */
public class EnergyEncryptRequestInterceptor implements RequestInterceptor {
    private final EnergyRequestContext energyRequestContext;

    public EnergyEncryptRequestInterceptor(EnergyRequestContext energyRequestContext) {
        this.energyRequestContext = energyRequestContext;
    }
    @Override
    public void intercept(HttpRequest request) {
        PostRequest postRequest = (PostRequest) request;
        String paramEncrypt = SMUtils.SM4Encrypt(postRequest.getBody(), energyRequestContext.getSm4Key(), CodeType.Hex);
        String queryString = "{\"queryChain\":\"" + paramEncrypt + "\"}";
        postRequest.setBody(queryString);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
