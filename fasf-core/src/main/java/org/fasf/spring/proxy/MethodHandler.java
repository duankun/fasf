package org.fasf.spring.proxy;

import org.fasf.annotation.QueryParam;
import org.fasf.annotation.Request;
import org.fasf.annotation.Retryable;
import org.fasf.http.*;
import org.fasf.interceptor.RequestInterceptor;
import org.fasf.interceptor.ResponseInterceptor;
import org.fasf.spring.context.ApiContext;
import org.fasf.util.ClassUtils;
import org.fasf.util.JSON;
import org.fasf.util.MDCUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MethodHandler {
    private final Logger logger = LoggerFactory.getLogger(MethodHandler.class);
    private final Method method;
    private final Request request;
    private final ApiContext apiContext;
    private final HttpClient httpClient;

    public MethodHandler(Method method, ApiContext apiContext, HttpClient httpClient) {
        this.method = method;
        this.request = method.getAnnotation(Request.class);
        this.apiContext = apiContext;
        this.httpClient = httpClient == null ? new HttpClient.DefaultHttpClient() : httpClient;
    }

    public Object invoke(Object[] args) {
        Class<?> returnType = method.getReturnType();
        if (Mono.class.isAssignableFrom(returnType) && method.getAnnotation(Retryable.class) != null) {
            throw new RuntimeException("Retry is not supported when returnType is specified by Mono,you can retry manually");
        }
        return switch (request.method()) {
            case GET -> this.get(args, returnType);
            case POST -> this.post(args, returnType);
            case PUT -> this.put(args, returnType);
            case DELETE -> this.delete(args, returnType);
            default -> throw new IllegalArgumentException("Unsupported method: " + request.method());
        };
    }

    public <T> T get(Object[] args, Class<T> returnType) {
        Map<String, String> queryParameters = this.resolveQueryParameters(args);
        GetRequest getRequest = (GetRequest) new HttpRequest.HttpRequestBuilder()
                .method(HttpMethod.GET)
                .queryParameters(queryParameters)
                .build();
        MDCUtils.setupMDC();
        try {
            this.applyRequestInterceptors(getRequest);
            getRequest.setUrl(this.buildUrlWithQueryParameters(apiContext.getEndpoint() + request.path(), getRequest.getQueryParameters()));
            if (logger.isDebugEnabled()) {
                logger.debug("Execute get method:{}", method);
            }
            Mono<HttpResponse> mono = httpClient.getAsync(getRequest);
            return this.getReturn(mono, getRequest, returnType);
        } finally {
            MDCUtils.cleanupMDC();
        }
    }

    private <T> T getReturn(Mono<HttpResponse> mono, HttpRequest request, Class<T> returnType) {
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        mono = mono.map(httpResponse -> {
            MDCUtils.setContextMap(mdcContext);
            try {
                return this.applyResponseInterceptor(httpResponse);
            } finally {
                MDCUtils.cleanupMDC();
            }
        });
        if (Mono.class.isAssignableFrom(returnType)) {
            Class<?> genericReturnType = ClassUtils.getGenericReturnType(method);
            if (HttpResponse.class.isAssignableFrom(genericReturnType)) {
                //noinspection unchecked
                return (T) mono;
            } else {
                //noinspection unchecked
                return (T) mono.map(response -> JSON.fromJson(response.getBodyAsString(), genericReturnType));
            }
        } else {
            T t;
            try {
                t = mono.map(httpResponse -> JSON.fromJson(httpResponse.getBodyAsString(), returnType)).block();
            } catch (Exception e) {
                t = JSON.fromJson(this.handleExceptionAndRetry(request, e).getBodyAsString(), returnType);
            }
            return t;
        }
    }

    private Map<String, String> resolveQueryParameters(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        Map<String, String> queryParameters = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            MethodParameter methodParameter = new MethodParameter(method, i);
            if (methodParameter.hasParameterAnnotation(QueryParam.class)) {
                QueryParam parameterAnnotation = methodParameter.getParameterAnnotation(QueryParam.class);
                if (parameterAnnotation != null) {
                    queryParameters.put(parameterAnnotation.value(), args[i].toString());
                }
            }
        }
        return queryParameters;
    }

    private String buildUrlWithQueryParameters(String baseUrl, Map<String, String> queryParameters) {
        if (queryParameters == null || queryParameters.isEmpty()) {
            return baseUrl;
        }
        return baseUrl + "?" + queryParameters.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
    }

    public <T> T post(Object[] args, Class<T> returnType) {
        if (args != null && args.length > 1) {
            logger.warn("POST request only uses the first argument as request body, other arguments are ignored");
        }
        PostRequest postRequest = (PostRequest) new HttpRequest.HttpRequestBuilder()
                .url(apiContext.getEndpoint() + request.path())
                .method(HttpMethod.POST)
                .header("Content-Type", request.contentType())
                .body(args == null ? null : args[0])
                .build();
        this.applyRequestInterceptors(postRequest);
        MDCUtils.setupMDC();
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Execute post method:{}", method);
            }
            Mono<HttpResponse> mono = httpClient.postAsync(postRequest);
            return this.getReturn(mono, postRequest, returnType);
        } finally {
            MDCUtils.cleanupMDC();
        }
    }

    public <T> T put(Object[] args, Class<T> returnType) {
        if (args != null && args.length > 1) {
            logger.warn("PUT request only uses the first argument as request body, other arguments are ignored");
        }
        PutRequest putRequest = (PutRequest) new HttpRequest.HttpRequestBuilder()
                .url(apiContext.getEndpoint() + request.path())
                .method(HttpMethod.PUT)
                .header("Content-Type", request.contentType())
                .body(args == null ? null : args[0])
                .build();
        this.applyRequestInterceptors(putRequest);
        MDCUtils.setupMDC();
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Execute put method:{}", method);
            }
            Mono<HttpResponse> mono = httpClient.putAsync(putRequest);

            return this.getReturn(mono, putRequest, returnType);
        } finally {
            MDCUtils.cleanupMDC();
        }
    }

    public <T> T delete(Object[] args, Class<T> returnType) {
        Map<String, String> queryParameters = this.resolveQueryParameters(args);
        DeleteRequest deleteRequest = (DeleteRequest) new HttpRequest.HttpRequestBuilder()
                .method(HttpMethod.DELETE)
                .queryParameters(queryParameters)
                .build();
        this.applyRequestInterceptors(deleteRequest);
        deleteRequest.setUrl(this.buildUrlWithQueryParameters(apiContext.getEndpoint() + request.path(), deleteRequest.getQueryParameters()));
        MDCUtils.setupMDC();
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Execute delete method:{}", method);
            }
            Mono<HttpResponse> mono = httpClient.deleteAsync(deleteRequest);
            return this.getReturn(mono, deleteRequest, returnType);
        } finally {
            MDCUtils.cleanupMDC();
        }
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

    private HttpResponse applyResponseInterceptor(HttpResponse httpResponse) {
        ResponseInterceptor responseInterceptor = apiContext.getResponseInterceptor(method);
        if (responseInterceptor != null) {
            String responseInterceptorName = responseInterceptor.getClass().getName();
            if (logger.isDebugEnabled()) {
                logger.debug("Apply response interceptor:{} ,before={}", responseInterceptorName, httpResponse);
            }
            responseInterceptor.intercept(httpResponse);
            if (logger.isDebugEnabled()) {
                logger.debug("Apply response interceptor:{} ,after={}", responseInterceptorName, httpResponse);
            }
        }
        return httpResponse;
    }

    private HttpResponse handleExceptionAndRetry(HttpRequest request, Exception exception) {
        Retryable retryable = method.getAnnotation(Retryable.class);
        if (retryable == null) {
            throw new RuntimeException(exception);
        }
        if (exception instanceof HttpException httpException && httpException.retryable()) {
            return this.retry(request, retryable);
        }
        throw new RuntimeException(exception);
    }

    private HttpResponse retry(HttpRequest request, Retryable retryable) {
        HttpResponse httpResponse = null;
        for (int i = 0; i < retryable.maxAttempts(); i++) {
            try {
                logger.debug("Retrying request, attempt {}/{}", i + 1, retryable.maxAttempts());
                TimeUnit.MILLISECONDS.sleep(retryable.delay());
                switch (request) {
                    case GetRequest getRequest -> httpResponse = httpClient.getAsync(getRequest).block();
                    case PutRequest putRequest -> httpResponse = httpClient.putAsync(putRequest).block();
                    case PostRequest postRequest -> httpResponse = httpClient.postAsync(postRequest).block();
                    case DeleteRequest deleteRequest -> httpResponse = httpClient.deleteAsync(deleteRequest).block();
                    default ->
                            throw new IllegalArgumentException("Unsupported request type: " + request.getClass().getName());
                }
                break;
            } catch (Exception e) {
                if (e instanceof HttpException httpException) {
                    if (!httpException.retryable()) {
                        throw httpException;
                    }
                } else {
                    throw new RuntimeException(e);
                }
            }
        }
        if (httpResponse == null) {
            throw new HttpException(500, "Request failed after retry " + retryable.maxAttempts() + " times");
        }
        return httpResponse;
    }
}
