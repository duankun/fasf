package org.fasf.spring.proxy;

import org.fasf.annotation.Request;
import org.fasf.http.HttpClient;
import org.fasf.spring.context.RemoterContext;

import java.lang.reflect.Method;

public class MethodHandler extends AbstractMethodHandler {
    private final Class<?> returnType;
    private final Request request;

    public MethodHandler(Method method, RemoterContext remoterContext, HttpClient httpClient) {
        super(method, remoterContext, httpClient);
        this.returnType = method.getReturnType();
        this.request = method.getAnnotation(Request.class);
    }

    public Object invoke(Object[] args) {
        return switch (request.method()) {
            case POST -> super.post(request, returnType, args[0]);
            case GET -> super.get(returnType, request.path(), super.resolveQueryParameters(args));
            default -> throw new IllegalArgumentException("Unsupported method: " + request.method());
        };
    }
}
