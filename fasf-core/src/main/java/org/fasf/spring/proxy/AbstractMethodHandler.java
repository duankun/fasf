package org.fasf.spring.proxy;

import org.fasf.annotation.GetParam;
import org.fasf.annotation.Request;
import org.fasf.annotation.Retryable;
import org.fasf.http.*;
import org.fasf.interceptor.RequestInterceptor;
import org.fasf.interceptor.ResponseInterceptor;
import org.fasf.interceptor.TraceIdInterceptor;
import org.fasf.spring.context.RemoterContext;
import org.fasf.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class AbstractMethodHandler {
    private final Logger logger = LoggerFactory.getLogger(AbstractMethodHandler.class);
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
        this.applyRequestInterceptors(request);
        String traceId = request.getHeaders().get(TraceIdInterceptor.TRACE_ID);
        if (logger.isDebugEnabled()) {
            logger.debug("Execute post method [{}]:{}", traceId, method);
        }
        String originResponseBody;
        try {
            originResponseBody = httpClient.post(request);
        } catch (HttpException httpException) {
            Retryable retryable = method.getAnnotation(Retryable.class);
            if (retryable != null && httpException.retryable()) {
                originResponseBody = this.retry(request, retryable);
            } else {
                throw httpException;
            }
        }
        originResponseBody = this.applyResponseInterceptor(originResponseBody, traceId);
        return JSON.fromJson(originResponseBody, returnType);
    }

    public <T> T get(Class<T> returnType, String path, Map<String, String> queryParameters) {
        GetRequest request = (GetRequest) new HttpRequest.HttpRequestBuilder()
                //.url(this.buildUrlWithParams(remoterContext.getEndpoint() + path, queryParameters))
                .method(HttpMethod.GET)
                .queryParameters(queryParameters)
                .build();
        this.applyRequestInterceptors(request);
        String traceId = request.getHeaders().get(TraceIdInterceptor.TRACE_ID);
        if (logger.isDebugEnabled()) {
            logger.debug("Execute get method [{}]:{}", traceId, method);
        }
        //fix bug which cause the encrypted query parameters not work
        request.setUrl(this.buildUrlWithParams(remoterContext.getEndpoint() + path, request.getQueryParameters()));
        String originResponseBody;
        try {
            originResponseBody = httpClient.get(request);
        } catch (HttpException httpException) {
            Retryable retryable = method.getAnnotation(Retryable.class);
            if (retryable != null && httpException.retryable()) {
                originResponseBody = this.retry(request, retryable);
            } else {
                throw httpException;
            }
        }
        originResponseBody = this.applyResponseInterceptor(originResponseBody, traceId);
        return JSON.fromJson(originResponseBody, returnType);
    }

    public Map<String, String> resolveQueryParameters(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        Map<String, String> queryParameters = new HashMap<>();
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

    private void applyRequestInterceptors(HttpRequest request) {
        Set<RequestInterceptor> requestInterceptors = remoterContext.getRequestInterceptors(method);
        if (!CollectionUtils.isEmpty(requestInterceptors)) {
            requestInterceptors.forEach(interceptor -> interceptor.intercept(request));
            if (logger.isDebugEnabled()) {
                logger.debug("Apply request interceptors [{}]: {}", request.getHeaders().get(TraceIdInterceptor.TRACE_ID), requestInterceptors);
            }
        }
    }

    private String applyResponseInterceptor(String originResponseString, String traceId) {
        ResponseInterceptor responseInterceptor = remoterContext.getResponseInterceptor(method);
        if (responseInterceptor != null) {
            String interceptedResponseString = responseInterceptor.intercept(originResponseString);
            if (logger.isDebugEnabled()) {
                logger.debug("Apply response interceptor [{}]: {} ,before interceptor={}, after interceptor:{}", traceId, responseInterceptor, originResponseString, interceptedResponseString);
            }
            return interceptedResponseString;
        }
        return originResponseString;
    }

    private String retry(HttpRequest request, Retryable retryable) {
        String originResponseString = null;
        for (int i = 0; i < retryable.maxAttempts(); i++) {
            try {
                TimeUnit.MILLISECONDS.sleep(retryable.delay());
                if (request instanceof GetRequest getRequest) {
                    originResponseString = httpClient.get(getRequest);
                } else if (request instanceof PostRequest postRequest) {
                    originResponseString = httpClient.post(postRequest);
                }
                break;
            } catch (HttpException e) {
                if (!e.retryable()) {
                    throw e;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (!StringUtils.hasText(originResponseString)) {
            throw new HttpException(500, "Request failed after retry " + retryable.maxAttempts() + " times");
        }
        return originResponseString;
    }
}
