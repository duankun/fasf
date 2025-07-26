package com.freemaker.fasf.spring.context;

import com.freemaker.fasf.interceptor.RequestInterceptor;

import java.util.List;

public class RequestContext {
    private String endpoint;
    private List<RequestInterceptor> interceptors;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public List<RequestInterceptor> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<RequestInterceptor> interceptors) {
        this.interceptors = interceptors;
    }
}
