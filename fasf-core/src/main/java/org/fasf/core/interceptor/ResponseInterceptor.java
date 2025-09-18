package org.fasf.core.interceptor;

import org.fasf.core.http.HttpResponse;

public interface ResponseInterceptor {
    void intercept(HttpResponse httpResponse);

    default String getName(){
        return this.getClass().getSimpleName();
    }

    class NoOpResponseInterceptor implements ResponseInterceptor {
        public void intercept(HttpResponse httpResponse) {

        }
    }
}
