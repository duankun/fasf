package com.freemaker.fasf.interceptor;

import com.freemaker.fasf.http.HttpRequest;

public interface RequestInterceptor extends Comparable<RequestInterceptor>{
    void intercept(HttpRequest request);
    int getOrder();
    default int compareTo(RequestInterceptor o){
        return Integer.compare(this.getOrder(), o.getOrder());
    }
}
