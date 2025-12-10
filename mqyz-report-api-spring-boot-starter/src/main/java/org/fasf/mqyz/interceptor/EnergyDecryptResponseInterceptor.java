package org.fasf.mqyz.interceptor;

import org.fasf.core.http.HttpResponse;
import org.fasf.core.interceptor.ResponseInterceptor;

/**
 * @author duankun
 * @date: 2025/11/28
 */
public class EnergyDecryptResponseInterceptor implements ResponseInterceptor {
    private final EnergyRequestContext energyRequestContext;

    public EnergyDecryptResponseInterceptor(EnergyRequestContext energyRequestContext) {
        this.energyRequestContext = energyRequestContext;
    }
    @Override
    public void intercept(HttpResponse httpResponse) {
        String decrypt = SMUtils.SM4Decrypt(httpResponse.getBodyAsString(), energyRequestContext.getSm4Key());
        httpResponse.setBody(decrypt.getBytes());
    }
}
