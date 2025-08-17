package org.fasf.spring.context;

import org.fasf.annotation.Api;
import org.fasf.annotation.Interceptors;
import org.fasf.interceptor.RequestInterceptor;
import org.fasf.interceptor.ResponseInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ApiContextSupport {
    @Autowired(required = false)
    private List<RequestInterceptor> requestInterceptors;
    @Autowired(required = false)
    private List<ResponseInterceptor> responseInterceptors;
    private final ApiContext apiContext = new ApiContext();

    public ApiContext getApiContext() {
        return apiContext;
    }

    public void initRequestContext(Class<?> apiInterface) {
        Assert.notNull(apiInterface, "Api interface cannot be null");
        Api api = apiInterface.getAnnotation(Api.class);
        apiContext.setEndpoint(api.endpoint());
        Interceptors interceptors = apiInterface.getAnnotation(Interceptors.class);
        Set<Class<? extends RequestInterceptor>> specificClassRequestInterceptors = Set.of(interceptors.requestInterceptors());
        Class<? extends ResponseInterceptor> specificResponseInterceptor = interceptors.responseInterceptor();
        Set<RequestInterceptor> classRequestInterceptors = CollectionUtils.isEmpty(requestInterceptors) ? new TreeSet<>() : requestInterceptors.stream().filter(interceptor -> specificClassRequestInterceptors.contains(interceptor.getClass())).collect(Collectors.toCollection(TreeSet::new));
        ResponseInterceptor classResponseInterceptor = CollectionUtils.isEmpty(responseInterceptors) ? null : responseInterceptors.stream().filter(interceptor -> specificResponseInterceptor.isAssignableFrom(interceptor.getClass())).findFirst().orElse(null);
        Method[] declaredMethods = apiInterface.getDeclaredMethods();
        Assert.notEmpty(declaredMethods, "No methods found in api interface " + apiInterface.getName());
        Arrays.stream(declaredMethods).forEach(method -> {
            Interceptors methodAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, Interceptors.class);
            if (methodAnnotation != null) {
                Set<Class<? extends RequestInterceptor>> specificMethodRequestInterceptors = Set.of(methodAnnotation.requestInterceptors());
                Set<RequestInterceptor> methodInterceptors = CollectionUtils.isEmpty(specificMethodRequestInterceptors) ? new TreeSet<>() : requestInterceptors.stream().filter(interceptor -> specificMethodRequestInterceptors.contains(interceptor.getClass())).collect(Collectors.toCollection(TreeSet::new));
                methodInterceptors.addAll(classRequestInterceptors);
                apiContext.addRequestInterceptors(method, methodInterceptors);
                Class<? extends ResponseInterceptor> specificMethodResponseInterceptor = methodAnnotation.responseInterceptor();
                ResponseInterceptor responseInterceptor = CollectionUtils.isEmpty(responseInterceptors) ? null : responseInterceptors.stream().filter(interceptor -> specificMethodResponseInterceptor.isAssignableFrom(interceptor.getClass())).findFirst().orElse(null);
                apiContext.setResponseInterceptor(method, responseInterceptor);
            } else {
                apiContext.addRequestInterceptors(method, classRequestInterceptors);
                apiContext.setResponseInterceptor(method, classResponseInterceptor);
            }
        });
    }
}
