package org.fasf.mqyz.interceptor;

import org.fasf.core.http.HttpRequest;
import org.fasf.core.http.PostRequest;
import org.fasf.core.interceptor.RequestInterceptor;


/**
* @author duankun
* @date: 2025/11/28
*/
public class EnergyAuthorizationRequestInterceptor implements RequestInterceptor {
    private final EnergyRequestContext energyRequestContext;

    public EnergyAuthorizationRequestInterceptor(EnergyRequestContext energyRequestContext) {
        this.energyRequestContext = energyRequestContext;
    }
    @Override
    public void intercept(HttpRequest request) {
        PostRequest postRequest = (PostRequest) request;
        postRequest.addHeader("Content-Type", "application/json");
        String sign = SMUtils.SM2Encrypt(energyRequestContext.getSm4Key(), energyRequestContext.getSm2PublicKey(), CodeType.Hex);
        postRequest.addHeader("Message-Sign", sign);
        String token = String.format("%s %s", "Bearer", energyRequestContext.getAccessToken());
        postRequest.addHeader("Authorization", token);
        postRequest.addHeader("Tenant-Id", energyRequestContext.getTenantId());
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
