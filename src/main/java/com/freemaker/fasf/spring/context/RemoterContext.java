package com.freemaker.fasf.spring.context;

import com.freemaker.fasf.interceptor.RequestInterceptor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RemoterContext {
    private String endpoint;
    private Map<Method, Set<RequestInterceptor>> interceptors = new HashMap<>();

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Set<RequestInterceptor> getInterceptors(Method method) {
        return interceptors.get(method);
    }

    public void addInterceptors(Method method, Set<RequestInterceptor> interceptors) {
        this.interceptors.put(method, interceptors);
    }

    public void setInterceptors(Map<Method, Set<RequestInterceptor>> interceptors) {
        this.interceptors = interceptors;
    }
}
