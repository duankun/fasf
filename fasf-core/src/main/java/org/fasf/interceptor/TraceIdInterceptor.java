package org.fasf.interceptor;


import org.fasf.http.HttpRequest;
import org.slf4j.MDC;

import static org.fasf.Constants.TRACE_ID;

public class TraceIdInterceptor implements RequestInterceptor {
    @Override
    public void intercept(HttpRequest request) {
        String traceId = MDC.get(TRACE_ID);
        request.addHeader(TRACE_ID, traceId);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
