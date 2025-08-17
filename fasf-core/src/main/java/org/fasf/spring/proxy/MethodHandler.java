package org.fasf.spring.proxy;

import org.fasf.annotation.Request;
import org.fasf.http.HttpClient;
import org.fasf.spring.context.ApiContext;

import java.lang.reflect.Method;

public class MethodHandler extends AbstractMethodHandler {
    private final Class<?> returnType;
    private final Request request;

    public MethodHandler(Method method, ApiContext apiContext, HttpClient httpClient) {
        super(method, apiContext, httpClient);
        this.returnType = method.getReturnType();
        this.request = method.getAnnotation(Request.class);
    }

    public Object invoke(Object[] args) {
        return switch (request.method()) {
            case POST -> super.post(request, returnType, args[0]);
            case GET -> super.get(returnType, request.path(), args);
            default -> throw new IllegalArgumentException("Unsupported method: " + request.method());
        };
    }
}
