package org.fasf.interceptor;

import org.fasf.http.HttpResponse;

public interface ResponseInterceptor {
    void intercept(HttpResponse httpResponse);

    class NoOpResponseInterceptor implements ResponseInterceptor {
        public void intercept(HttpResponse httpResponse) {

        }
    }
}
