package com.freemaker.fasf.interceptor;

import com.freemaker.fasf.http.HttpRequest;

public interface RequestInterceptor {
    void intercept(HttpRequest request);
}
