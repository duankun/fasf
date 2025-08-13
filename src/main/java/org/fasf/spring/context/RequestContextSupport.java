package org.fasf.spring.context;

import org.fasf.annotation.Interceptors;
import org.fasf.annotation.Remoter;
import org.fasf.interceptor.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class RequestContextSupport {
    @Autowired
    private List<RequestInterceptor> interceptors;
    private final RemoterContext remoterContext = new RemoterContext();

    public RemoterContext getRemoterContext() {
        return remoterContext;
    }

    public void initRequestContext(Class<?> remoterInterface) {
        Assert.notNull(remoterInterface, "Remoter interface cannot be null");
        Remoter remoter = remoterInterface.getAnnotation(Remoter.class);
        remoterContext.setEndpoint(remoter.endpoint());
        Set<Class<? extends RequestInterceptor>> specificClassInterceptors = Set.of(remoter.interceptors());
        Set<RequestInterceptor> classInterceptors = interceptors.stream().filter(interceptor -> specificClassInterceptors.contains(interceptor.getClass())).collect(Collectors.toCollection(TreeSet::new));
        Method[] declaredMethods = remoterInterface.getDeclaredMethods();
        Assert.notEmpty(declaredMethods, "No methods found in remoter interface " + remoterInterface.getName());
        Arrays.stream(declaredMethods).forEach(method -> {
            Interceptors methodAnnotation = method.getAnnotation(Interceptors.class);
            if (methodAnnotation != null) {
                Set<Class<? extends RequestInterceptor>> specificMethodInterceptors = Set.of(methodAnnotation.interceptors());
                Set<RequestInterceptor> methodInterceptors = interceptors.stream().filter(interceptor -> specificMethodInterceptors.contains(interceptor.getClass())).collect(Collectors.toCollection(TreeSet::new));
                classInterceptors.addAll(methodInterceptors);
            }
            remoterContext.addRequestInterceptors(method, classInterceptors);
        });
    }
}
