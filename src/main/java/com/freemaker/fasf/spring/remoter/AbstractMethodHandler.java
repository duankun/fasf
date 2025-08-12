package com.freemaker.fasf.spring.remoter;

import com.freemaker.fasf.annotation.GetParam;
import com.freemaker.fasf.http.*;
import com.freemaker.fasf.spring.context.RemoterContext;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;
import java.util.Map;

public class AbstractMethodHandler {
    private final Method method;
    private final HttpClient httpClient;
    private final RemoterContext remoterContext;

    public AbstractMethodHandler(Method method, RemoterContext remoterContext) {
        this(method,remoterContext, null);
    }

    public AbstractMethodHandler(Method method,RemoterContext remoterContext, HttpClient httpClient) {
        this.method = method;
        this.remoterContext = remoterContext;
        this.httpClient = httpClient == null ? new HttpClient.DefaultHttpClient() : httpClient;
    }

    public <T> T post(Class<T> returnType, String path, Object body) {
        PostRequest request = (PostRequest) new HttpRequest.HttpRequestBuilder()
                .url(remoterContext.getEndpoint() + path)
                .method(HttpMethod.POST)
                .body(body)
                .build();
        remoterContext.getInterceptors(method).forEach(interceptor -> interceptor.intercept(request));
        System.out.println(request);
        return httpClient.post(returnType, request);
    }

    public Map<String, String> resolveQueryParameters(Object[] args) {
        Map<String, String> queryParameters = new java.util.HashMap<>();
        for (int i = 0; i < args.length; i++) {
            MethodParameter methodParameter = new MethodParameter(method, i);
            if (methodParameter.hasParameterAnnotation(GetParam.class)) {
                GetParam parameterAnnotation = methodParameter.getParameterAnnotation(GetParam.class);
                if (parameterAnnotation != null) {
                    queryParameters.put(parameterAnnotation.value(), args[i].toString());
                }
            }
        }
        return queryParameters;
    }

    public String joinQueryParameters(Map<String, String> queryParameters) {
        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String> entry : queryParameters.entrySet()) {
            query.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return query.toString();
    }

    public <T> T get(Class<T> returnType, String path, Map<String, String> queryParameters) {
        GetRequest request = (GetRequest) new HttpRequest.HttpRequestBuilder()
                .url(remoterContext.getEndpoint() + path + "?" + joinQueryParameters(queryParameters))
                .method(HttpMethod.GET)
                .queryParameters(queryParameters)
                .build();
        remoterContext.getInterceptors(method).forEach(interceptor -> interceptor.intercept(request));
        System.out.println(request);
        return httpClient.get(returnType, request);
    }
}
