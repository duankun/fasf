package org.fasf.interceptor;


import org.fasf.http.HttpRequest;

import java.util.UUID;

public class TraceIdInterceptor implements RequestInterceptor {
    public static final String TRACE_ID = "X-Trace-Id";
    @Override
    public void intercept(HttpRequest request) {
        String traceId = UUID.randomUUID().toString();
        request.addHeader(TRACE_ID, traceId);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
