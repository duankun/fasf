package org.fasf.interceptor;

public interface ResponseInterceptor {
    String intercept(String originResponseBody);
    class NoOpResponseInterceptor implements ResponseInterceptor {
        public String intercept(String originResponseBody) {
            return originResponseBody;
        }
    }
}
