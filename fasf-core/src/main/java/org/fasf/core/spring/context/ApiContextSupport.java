package org.fasf.core.spring.context;

import org.fasf.core.annotation.Api;
import org.fasf.core.annotation.Interceptors;
import org.fasf.core.interceptor.RequestInterceptor;
import org.fasf.core.interceptor.ResponseInterceptor;
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
    private ApiContext apiContext;

    public ApiContext getApiContext() {
        return apiContext;
    }

    public void createApiContext(Class<?> apiInterface, List<RequestInterceptor> requestInterceptors, List<ResponseInterceptor> responseInterceptors) {
        Assert.notNull(apiInterface, "Api interface cannot be null");
        apiContext = new ApiContext();
        apiContext.setApiInterface(apiInterface);
        Api api = apiInterface.getAnnotation(Api.class);
        apiContext.setEndpoint(api.endpoint());
        Interceptors classInterceptorsAnnotation = apiInterface.getAnnotation(Interceptors.class);
        Set<Class<? extends RequestInterceptor>> specificClassRequestInterceptors = Set.of(classInterceptorsAnnotation.requestInterceptors());
        Class<? extends ResponseInterceptor> specificClassResponseInterceptor = classInterceptorsAnnotation.responseInterceptor();
        Set<RequestInterceptor> classRequestInterceptors = CollectionUtils.isEmpty(requestInterceptors) ? new TreeSet<>() : requestInterceptors.stream().filter(interceptor -> specificClassRequestInterceptors.contains(interceptor.getClass())).collect(Collectors.toCollection(TreeSet::new));
        ResponseInterceptor classResponseInterceptor = CollectionUtils.isEmpty(responseInterceptors) ? null : responseInterceptors.stream().filter(interceptor -> specificClassResponseInterceptor.isAssignableFrom(interceptor.getClass())).findFirst().orElse(null);
        Method[] declaredMethods = apiInterface.getDeclaredMethods();
        Assert.notEmpty(declaredMethods, "No methods found in api interface " + apiInterface.getName());
        Arrays.stream(declaredMethods).forEach(method -> {
            Interceptors methodInterceptorsAnnotation = AnnotatedElementUtils.findMergedAnnotation(method, Interceptors.class);
            if (methodInterceptorsAnnotation != null) {
                Set<Class<? extends RequestInterceptor>> specificMethodRequestInterceptors = Set.of(methodInterceptorsAnnotation.requestInterceptors());
                Set<RequestInterceptor> methodRequestInterceptors = CollectionUtils.isEmpty(specificMethodRequestInterceptors) ? new TreeSet<>() : requestInterceptors.stream().filter(interceptor -> specificMethodRequestInterceptors.contains(interceptor.getClass())).collect(Collectors.toCollection(TreeSet::new));
                methodRequestInterceptors.addAll(classRequestInterceptors);
                apiContext.addRequestInterceptors(method, methodRequestInterceptors);
                Class<? extends ResponseInterceptor> specificMethodResponseInterceptor = methodInterceptorsAnnotation.responseInterceptor();
                ResponseInterceptor methodResponseInterceptor = CollectionUtils.isEmpty(responseInterceptors) ? null : responseInterceptors.stream().filter(interceptor -> specificMethodResponseInterceptor.isAssignableFrom(interceptor.getClass())).findFirst().orElse(null);
                apiContext.setResponseInterceptor(method, methodResponseInterceptor == null ? classResponseInterceptor : methodResponseInterceptor);
            } else {
                apiContext.addRequestInterceptors(method, classRequestInterceptors);
                apiContext.setResponseInterceptor(method, classResponseInterceptor);
            }
        });
    }
}
