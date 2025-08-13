package org.fasf.spring.context;

import org.fasf.interceptor.RequestInterceptor;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RemoterContext {
    private String endpoint;
    private final Map<Method, Set<RequestInterceptor>> requestInterceptors = new HashMap<>();

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Set<RequestInterceptor> getRequestInterceptors(Method method) {
        return requestInterceptors.get(method);
    }

    public void addRequestInterceptors(Method method, Set<RequestInterceptor> interceptors) {
        if (!CollectionUtils.isEmpty(interceptors)) {
            this.requestInterceptors.put(method, interceptors);
        }
    }

}
