package com.freemaker.fasf.spring.proxy;

import com.freemaker.fasf.annotation.Request;
import com.freemaker.fasf.http.HttpClient;
import com.freemaker.fasf.http.HttpMethod;
import com.freemaker.fasf.spring.context.RemoterContext;

import java.lang.reflect.Method;

public class MethodHandler extends AbstractMethodHandler {
    private final Class<?> returnType;
    private final Request request;

    public MethodHandler(Method method, RemoterContext remoterContext, HttpClient httpClient) {
        super(method,remoterContext, httpClient);
        this.returnType = method.getReturnType();
        this.request = method.getAnnotation(Request.class);
    }

    public Object invoke(Object[] args) {
        Object result = null;
        if (request.method() == HttpMethod.POST) {
            result = super.post(returnType, request.path(), args[0]);
        } else if(request.method() == HttpMethod.GET){
            result = super.get(returnType, request.path(), super.resolveQueryParameters(args));
        }
        return result;
    }
}
