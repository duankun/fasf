package com.freemaker.fasf.spring.proxy;

import com.freemaker.fasf.annotation.Request;
import com.freemaker.fasf.http.HttpClient;
import com.freemaker.fasf.http.HttpMethod;
import com.freemaker.fasf.spring.context.RemoterContext;

import java.lang.reflect.Method;

public class MethodHandler extends AbstractMethodHandler {
    private final Class<?> returnType;
    private final HttpMethod method;
    private final String path;

    public MethodHandler(Method method, RemoterContext remoterContext, HttpClient httpClient) {
        super(method, remoterContext, httpClient);
        this.returnType = method.getReturnType();
        Request request = method.getAnnotation(Request.class);
        this.method = request.method();
        this.path = request.path();
    }

    public Object invoke(Object[] args) {
        return switch (method) {
            case POST -> super.post(returnType, path, args[0]);
            case GET -> super.get(returnType, path, super.resolveQueryParameters(args));
            default -> throw new IllegalArgumentException("Unsupported method: " + method);
        };
    }
}
