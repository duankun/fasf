package com.freemaker.fasf.spring.remoter;

import com.freemaker.fasf.http.HttpClient;
import com.freemaker.fasf.spring.context.RequestContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RemoterInvocationHandler implements InvocationHandler {
    private final Class<?> remoterInterface;
    private final Map<Method, MethodHandler> methodHandlers = new HashMap<>();

    public RemoterInvocationHandler(Class<?> remoterInterface, RequestContext requestContext, HttpClient httpClient) {
        this.remoterInterface = remoterInterface;
        Method[] declaredMethods = remoterInterface.getDeclaredMethods();
        Arrays.stream(declaredMethods).forEach(method -> methodHandlers.put(method, new MethodHandler(method, requestContext, httpClient)));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("toString")) {
            return "RemoterProxy for " + remoterInterface.getName();
        }
        MethodHandler methodHandler = methodHandlers.get(method);
        assert methodHandler != null;
        return methodHandler.invoke(args);
    }
}
