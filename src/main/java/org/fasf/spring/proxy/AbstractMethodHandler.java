package org.fasf.spring.proxy;

import org.fasf.annotation.GetParam;
import org.fasf.annotation.Request;
import org.fasf.http.*;
import org.fasf.interceptor.RequestInterceptor;
import org.fasf.spring.context.RemoterContext;
import org.springframework.core.MethodParameter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public class AbstractMethodHandler {
    private final Method method;
    private final HttpClient httpClient;
    private final RemoterContext remoterContext;

    public AbstractMethodHandler(Method method, RemoterContext remoterContext, HttpClient httpClient) {
        this.method = method;
        this.remoterContext = remoterContext;
        this.httpClient = httpClient == null ? new HttpClient.DefaultHttpClient() : httpClient;
    }

    public <T> T post(Request request, Class<T> returnType, Object body) {
        return post(request.path(), request.contentType(), body, returnType);
    }

    public <T> T post(String path, String contentType, Object body, Class<T> returnType) {
        PostRequest request = (PostRequest) new HttpRequest.HttpRequestBuilder()
                .url(remoterContext.getEndpoint() + path)
                .method(HttpMethod.POST)
                .header("Content-Type", contentType)
                .body(body)
                .build();
        Set<RequestInterceptor> requestInterceptors = remoterContext.getRequestInterceptors(method);
        if (!CollectionUtils.isEmpty(requestInterceptors)) {
            requestInterceptors.forEach(interceptor -> interceptor.intercept(request));
        }
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

    private String buildUrlWithParams(String baseUrl, Map<String, String> queryParameters) {
        if (queryParameters == null || queryParameters.isEmpty()) {
            return baseUrl;
        }

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl);
        queryParameters.forEach(builder::queryParam);
        return builder.toUriString();
    }

    public <T> T get(Class<T> returnType, String path, Map<String, String> queryParameters) {
        GetRequest request = (GetRequest) new HttpRequest.HttpRequestBuilder()
                //.url(this.buildUrlWithParams(remoterContext.getEndpoint() + path, queryParameters))
                .method(HttpMethod.GET)
                .queryParameters(queryParameters)
                .build();
        Set<RequestInterceptor> requestInterceptors = remoterContext.getRequestInterceptors(method);
        if (!CollectionUtils.isEmpty(requestInterceptors)) {
            requestInterceptors.forEach(interceptor -> interceptor.intercept(request));
        }
        //fix bug which cause the encrypted query parameters not work
        request.setUrl(this.buildUrlWithParams(remoterContext.getEndpoint() + path, request.getQueryParameters()));
        System.out.println(request);
        return httpClient.get(returnType, request);
    }
}
