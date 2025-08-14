package org.fasf.interceptor;

public interface ResponseInterceptor {
    String intercept(String responseBody);
}
