package org.fasf.spring.proxy;

import org.fasf.Const;
import org.fasf.annotation.GetParam;
import org.fasf.annotation.Request;
import org.fasf.annotation.Retryable;
import org.fasf.http.*;
import org.fasf.interceptor.RequestInterceptor;
import org.fasf.interceptor.ResponseInterceptor;
import org.fasf.spring.context.ApiContext;
import org.fasf.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AbstractMethodHandler {
    private final Logger logger = LoggerFactory.getLogger(AbstractMethodHandler.class);
    private final Method method;
    private final HttpClient httpClient;
    private final ApiContext apiContext;

    public AbstractMethodHandler(Method method, ApiContext apiContext, HttpClient httpClient) {
        this.method = method;
        this.apiContext = apiContext;
        this.httpClient = httpClient == null ? new HttpClient.DefaultHttpClient() : httpClient;
    }

    public <T> T post(Request request, Class<T> returnType, Object body) {
        this.setupMDC();
        try {
            return this.post(request.path(), request.contentType(), body, returnType);
        } finally {
            this.cleanupMDC();
        }
    }

    private <T> T post(String path, String contentType, Object body, Class<T> returnType) {
        PostRequest request = (PostRequest) new HttpRequest.HttpRequestBuilder()
                .url(apiContext.getEndpoint() + path)
                .method(HttpMethod.POST)
                .header("Content-Type", contentType)
                .body(body)
                .build();
        this.applyRequestInterceptors(request);
        if (logger.isDebugEnabled()) {
            logger.debug("Execute post method:{}", method);
        }
        String originResponseBody;
        try {
            originResponseBody = httpClient.post(request);
        } catch (Exception e) {
            originResponseBody = this.handleExceptionAndRetry(request, e);
        }
        originResponseBody = this.applyResponseInterceptor(originResponseBody);
        return JSON.fromJson(originResponseBody, returnType);
    }

    public <T> T get(Class<T> returnType, String path, Object[] args) {
        this.setupMDC();
        try {
            return this.get(returnType, path, this.resolveQueryParameters(args));
        } finally {
            this.cleanupMDC();
        }
    }

    private <T> T get(Class<T> returnType, String path, Map<String, String> queryParameters) {
        GetRequest request = (GetRequest) new HttpRequest.HttpRequestBuilder()
                //.url(this.buildUrlWithParams(apiContext.getEndpoint() + path, queryParameters))
                .method(HttpMethod.GET)
                .queryParameters(queryParameters)
                .build();
        this.applyRequestInterceptors(request);
        if (logger.isDebugEnabled()) {
            logger.debug("Execute get method:{}", method);
        }
        //fix bug which cause the encrypted query parameters not work
        request.setUrl(this.buildUrlWithParams(apiContext.getEndpoint() + path, request.getQueryParameters()));
        String originResponseBody;
        try {
            originResponseBody = httpClient.get(request);
        } catch (Exception e) {
            originResponseBody = this.handleExceptionAndRetry(request, e);
        }
        originResponseBody = this.applyResponseInterceptor(originResponseBody);
        return JSON.fromJson(originResponseBody, returnType);
    }

    private Map<String, String> resolveQueryParameters(Object[] args) {
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

    private void setupMDC() {
        String traceId = MDC.get(Const.TRACE_ID);
        if (traceId == null) {
            traceId = UUID.randomUUID().toString().substring(0, 8);
            MDC.put(Const.TRACE_ID, traceId);
        }
    }

    private void cleanupMDC() {
        MDC.clear();
    }

    private void applyRequestInterceptors(HttpRequest request) {
        Set<RequestInterceptor> requestInterceptors = apiContext.getRequestInterceptors(method);
        if (!CollectionUtils.isEmpty(requestInterceptors)) {
            requestInterceptors.forEach(interceptor -> interceptor.intercept(request));
            if (logger.isDebugEnabled()) {
                logger.debug("Apply request interceptors: {}", requestInterceptors);
            }
        }
    }

    private String applyResponseInterceptor(String originResponseString) {
        ResponseInterceptor responseInterceptor = apiContext.getResponseInterceptor(method);
        if (responseInterceptor != null) {
            String interceptedResponseString = responseInterceptor.intercept(originResponseString);
            if (logger.isDebugEnabled()) {
                logger.debug("Apply response interceptor: {} ,before interceptor={}, after interceptor:{}", responseInterceptor, originResponseString, interceptedResponseString);
            }
            return interceptedResponseString;
        }
        return originResponseString;
    }

    private String handleExceptionAndRetry(HttpRequest request, Exception exception) {
        Retryable retryable = method.getAnnotation(Retryable.class);
        if (retryable != null && exception instanceof HttpException httpException && httpException.retryable()) {
            return this.retry(request, retryable);
        } else {
            throw new RuntimeException(exception);
        }
    }

    private String retry(HttpRequest request, Retryable retryable) {
        String originResponseString = null;
        for (int i = 0; i < retryable.maxAttempts(); i++) {
            try {
                logger.debug("Retrying request, attempt {}/{}", i + 1, retryable.maxAttempts());
                TimeUnit.MILLISECONDS.sleep(retryable.delay());
                if (request instanceof GetRequest getRequest) {
                    originResponseString = httpClient.get(getRequest);
                } else if (request instanceof PostRequest postRequest) {
                    originResponseString = httpClient.post(postRequest);
                }
                break;
            } catch (Exception e) {
                if (e instanceof HttpException exception) {
                    if (!exception.retryable()) {
                        throw new RuntimeException(e);
                    }
                } else {
                    throw new RuntimeException(e);
                }
            }
        }
        if (!StringUtils.hasText(originResponseString)) {
            throw new HttpException(500, "Request failed after retry " + retryable.maxAttempts() + " times");
        }
        return originResponseString;
    }
}
