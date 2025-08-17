package org.fasf.spring.proxy;

import org.fasf.http.HttpClient;
import org.fasf.spring.context.ApiContext;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ApiInvocationHandler implements InvocationHandler {
    private final Class<?> apiInterface;
    private final Map<Method, MethodHandler> methodHandlers = new HashMap<>();

    public ApiInvocationHandler(Class<?> apiInterface, ApiContext apiContext, HttpClient httpClient) {
        this.apiInterface = apiInterface;
        Method[] declaredMethods = apiInterface.getDeclaredMethods();
        Assert.notEmpty(declaredMethods, "No methods found in api interface " + apiInterface.getName());
        Arrays.stream(declaredMethods).forEach(method -> methodHandlers.put(method, new MethodHandler(method, apiContext, httpClient)));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("toString")) {
            return "ApiProxy for " + apiInterface.getName();
        }
        MethodHandler methodHandler = methodHandlers.get(method);
        Assert.notNull(methodHandler, "No method handler found for " + method.getName());
        return methodHandler.invoke(args);
    }
}
