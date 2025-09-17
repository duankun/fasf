package org.fasf.core.interceptor;

import org.fasf.core.http.HttpRequest;

public interface RequestInterceptor extends Comparable<RequestInterceptor>{
    void intercept(HttpRequest request);

    int getOrder();

    default int compareTo(RequestInterceptor o){
        return Integer.compare(this.getOrder(), o.getOrder());
    }
}
