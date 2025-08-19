package org.fasf.spring.proxy;

import org.fasf.annotation.Request;
import org.fasf.annotation.Retryable;
import org.fasf.http.HttpClient;
import org.fasf.spring.context.ApiContext;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

public class MethodHandler extends AbstractMethodHandler {
    private final Class<?> returnType;
    private final Request request;

    public MethodHandler(Method method, ApiContext apiContext, HttpClient httpClient) {
        super(method, apiContext, httpClient);
        this.returnType = method.getReturnType();
        this.request = method.getAnnotation(Request.class);
    }

    public Object invoke(Object[] args) {
        if (CompletableFuture.class.isAssignableFrom(returnType) && super.getMethod().getAnnotation(Retryable.class) != null) {
            throw new RuntimeException("Retry is not supported when returnType is specified by CompletableFuture,you can retry manually");
        }
        return switch (request.method()) {
            case POST -> super.post(request, args[0], returnType);
            case GET -> super.get(request, args, returnType);
            default -> throw new IllegalArgumentException("Unsupported method: " + request.method());
        };
    }
}
