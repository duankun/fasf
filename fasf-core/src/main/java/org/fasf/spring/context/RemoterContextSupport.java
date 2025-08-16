package org.fasf.spring.context;

import org.fasf.annotation.Interceptors;
import org.fasf.annotation.Remoter;
import org.fasf.interceptor.RequestInterceptor;
import org.fasf.interceptor.ResponseInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class RemoterContextSupport {
    @Autowired(required = false)
    private List<RequestInterceptor> requestInterceptors;
    @Autowired(required = false)
    private List<ResponseInterceptor> responseInterceptors;
    private final RemoterContext remoterContext = new RemoterContext();

    public RemoterContext getRemoterContext() {
        return remoterContext;
    }

    public void initRequestContext(Class<?> remoterInterface) {
        Assert.notNull(remoterInterface, "Remoter interface cannot be null");
        Remoter remoter = remoterInterface.getAnnotation(Remoter.class);
        remoterContext.setEndpoint(remoter.endpoint());
        Interceptors interceptors = remoterInterface.getAnnotation(Interceptors.class);
        Set<Class<? extends RequestInterceptor>> specificClassRequestInterceptors = Set.of(interceptors.requestInterceptors());
        Class<? extends ResponseInterceptor> specificResponseInterceptor = interceptors.responseInterceptor();
        Set<RequestInterceptor> classRequestInterceptors = CollectionUtils.isEmpty(requestInterceptors) ? new TreeSet<>() : requestInterceptors.stream().filter(interceptor -> specificClassRequestInterceptors.contains(interceptor.getClass())).collect(Collectors.toCollection(TreeSet::new));
        ResponseInterceptor classResponseInterceptor = CollectionUtils.isEmpty(responseInterceptors) ? null : responseInterceptors.stream().filter(interceptor -> specificResponseInterceptor.isAssignableFrom(interceptor.getClass())).findFirst().orElse(null);
        Method[] declaredMethods = remoterInterface.getDeclaredMethods();
        Assert.notEmpty(declaredMethods, "No methods found in remoter interface " + remoterInterface.getName());
        Arrays.stream(declaredMethods).forEach(method -> {
            Interceptors methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, Interceptors.class);
            if (methodAnnotation != null) {
                Set<Class<? extends RequestInterceptor>> specificMethodRequestInterceptors = Set.of(methodAnnotation.requestInterceptors());
                Set<RequestInterceptor> methodInterceptors = CollectionUtils.isEmpty(specificMethodRequestInterceptors) ? new TreeSet<>() : requestInterceptors.stream().filter(interceptor -> specificMethodRequestInterceptors.contains(interceptor.getClass())).collect(Collectors.toCollection(TreeSet::new));
                methodInterceptors.addAll(classRequestInterceptors);
                remoterContext.addRequestInterceptors(method, methodInterceptors);
                Class<? extends ResponseInterceptor> specificMethodResponseInterceptor = methodAnnotation.responseInterceptor();
                ResponseInterceptor responseInterceptor = CollectionUtils.isEmpty(responseInterceptors) ? null : responseInterceptors.stream().filter(interceptor -> specificMethodResponseInterceptor.isAssignableFrom(interceptor.getClass())).findFirst().orElse(null);
                remoterContext.setResponseInterceptor(method, responseInterceptor);
            } else {
                remoterContext.addRequestInterceptors(method, classRequestInterceptors);
                remoterContext.setResponseInterceptor(method, classResponseInterceptor);
            }
        });
    }
}
