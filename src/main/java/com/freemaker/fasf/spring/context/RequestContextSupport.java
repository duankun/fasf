package com.freemaker.fasf.spring.context;

import com.freemaker.fasf.annotation.Remoter;
import com.freemaker.fasf.interceptor.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

public class RequestContextSupport {
    @Autowired
    private List<RequestInterceptor> interceptors;
    private RequestContext requestContext;

    public RequestContext getRequestContext() {
        return requestContext;
    }

    public void initRequestContext(Remoter remoter) {
        requestContext = new RequestContext();
        requestContext.setEndpoint(remoter.endpoint());
        Set<Class<? extends RequestInterceptor>> specificInterceptors = Set.of(remoter.interceptors());
        requestContext.setInterceptors(interceptors.stream().filter(interceptor -> specificInterceptors.contains(interceptor.getClass())).toList());
    }
}
