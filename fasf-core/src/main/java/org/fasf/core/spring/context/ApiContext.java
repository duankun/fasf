package org.fasf.core.spring.context;

import org.fasf.core.interceptor.RequestInterceptor;
import org.fasf.core.interceptor.ResponseInterceptor;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ApiContext {
    private Class<?> apiInterface;
    private String endpoint;
    private final Map<Method, Set<RequestInterceptor>> requestInterceptors = new HashMap<>();
    private final Map<Method, ResponseInterceptor> responseInterceptors = new HashMap<>();

    public Class<?> getApiInterface() {
        return apiInterface;
    }

    public void setApiInterface(Class<?> apiInterface) {
        this.apiInterface = apiInterface;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Set<RequestInterceptor> getRequestInterceptors(Method method) {
        return requestInterceptors.get(method);
    }

    public ResponseInterceptor getResponseInterceptor(Method method) {
        return responseInterceptors.get(method);
    }

    public void addRequestInterceptors(Method method, Set<RequestInterceptor> interceptors) {
        if (!CollectionUtils.isEmpty(interceptors)) {
            Set<RequestInterceptor> methodInterceptors = this.requestInterceptors.get(method);
            if (methodInterceptors == null) {
                this.requestInterceptors.put(method, interceptors);
            } else {
                methodInterceptors.addAll(interceptors);
            }
        }
    }

    public void setResponseInterceptor(Method method, ResponseInterceptor interceptor) {
        if (interceptor != null) {
            this.responseInterceptors.put(method, interceptor);
        }
    }

}
