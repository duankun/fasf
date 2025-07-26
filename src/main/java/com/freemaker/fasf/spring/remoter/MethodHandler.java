package com.freemaker.fasf.spring.remoter;

import com.freemaker.fasf.annotation.RequestLine;
import com.freemaker.fasf.http.HttpClient;
import com.freemaker.fasf.http.HttpMethod;
import com.freemaker.fasf.spring.context.RemoterContext;

import java.lang.reflect.Method;

public class MethodHandler extends AbstractMethodHandler {
    private final Method method;
    private final Class<?> returnType;
    private final RequestLine requestLine;

    public MethodHandler(Method method, RemoterContext remoterContext, HttpClient httpClient) {
        super(remoterContext, httpClient);
        this.method = method;
        this.returnType = method.getReturnType();
        this.requestLine = method.getAnnotation(RequestLine.class);
    }

    public Object invoke(Object[] args) {
        Object result = null;
        if (requestLine.method() == HttpMethod.POST) {
            result = super.post(returnType, requestLine.path(), args[0]);
        } else if(requestLine.method() == HttpMethod.GET){
            result = super.get(returnType, requestLine.path(), super.resolveQueryParameters(method, args));
        }
        return result;
    }
}
