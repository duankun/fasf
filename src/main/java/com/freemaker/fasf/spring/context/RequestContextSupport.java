package com.freemaker.fasf.spring.context;

import com.freemaker.fasf.annotation.Remoter;
import com.freemaker.fasf.interceptor.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

public class RequestContextSupport {
    @Autowired
    private List<RequestInterceptor> interceptors;
    private RemoterContext remoterContext;

    public RemoterContext getRemoterContext() {
        return remoterContext;
    }

    public void initRequestContext(Remoter remoter) {
        remoterContext = new RemoterContext();
        remoterContext.setEndpoint(remoter.endpoint());
        Set<Class<? extends RequestInterceptor>> specificInterceptors = Set.of(remoter.interceptors());
        remoterContext.setInterceptors(interceptors.stream().filter(interceptor -> specificInterceptors.contains(interceptor.getClass())).toList());
    }
}
