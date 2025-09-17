package org.fasf.interceptor;


import org.fasf.core.http.HttpRequest;
import org.fasf.core.interceptor.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;

import static org.fasf.core.Constants.TRACE_ID;

public class TraceIdInterceptor implements RequestInterceptor {
    @Value("${fasf.api.trace.id.name}")
    private String traceIdName;

    @Override
    public void intercept(HttpRequest request) {
        String traceId = MDC.get(TRACE_ID);
        request.addHeader(traceIdName, traceId);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
