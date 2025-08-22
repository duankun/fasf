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
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
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
        if (CompletableFuture.class.isAssignableFrom(returnType) && method.getAnnotation(Retryable.class) != null) {
            throw new RuntimeException("Retry is not supported when returnType is specified by CompletableFuture,you can retry manually");
        }
        return switch (request.method()) {
            case GET -> this.get(args, returnType);
            case POST -> this.post(args[0], returnType);
            case PUT -> this.put(args[0], returnType);
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
        this.applyRequestInterceptors(getRequest);
        getRequest.setUrl(this.buildUrlWithQueryParameters(apiContext.getEndpoint() + request.path(), getRequest.getQueryParameters()));
        MDCUtils.setupMDC();
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Execute get method:{}", method);
            }
            //noinspection unchecked
            return CompletableFuture.class.isAssignableFrom(returnType) ? (T) this.getFuture(getRequest) : this.get(getRequest, returnType);
        } finally {
            MDCUtils.cleanupMDC();
        }
    }

    public <T> CompletableFuture<T> getFuture(GetRequest request) {
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        CompletableFuture<String> future = httpClient.getAsync(request);
        return future.thenApply(responseString -> {
            MDCUtils.setContextMap(mdcContext);
            try {
                //noinspection unchecked
                return JSON.fromJson(this.applyResponseInterceptor(responseString), (Class<T>) ClassUtils.getGenericReturnType(method));
            } finally {
                MDCUtils.cleanupMDC();
            }
        });
    }

    private <T> T get(GetRequest request, Class<T> returnType) {
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

    public <T> T post(Object body, Class<T> returnType) {
        PostRequest postRequest = (PostRequest) new HttpRequest.HttpRequestBuilder()
                .url(apiContext.getEndpoint() + request.path())
                .method(HttpMethod.POST)
                .header("Content-Type", request.contentType())
                .body(body)
                .build();
        this.applyRequestInterceptors(postRequest);
        MDCUtils.setupMDC();
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Execute post method:{}", method);
            }
            //noinspection unchecked
            return CompletableFuture.class.isAssignableFrom(returnType) ? (T) this.postFuture(postRequest) : this.post(postRequest, returnType);
        } finally {
            MDCUtils.cleanupMDC();
        }
    }

    public <T> CompletableFuture<T> postFuture(PostRequest request) {
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        CompletableFuture<String> future = httpClient.postAsync(request);
        return future.thenApply(responseString -> {
            MDCUtils.setContextMap(mdcContext);
            try {
                //noinspection unchecked
                return JSON.fromJson(this.applyResponseInterceptor(responseString), (Class<T>) ClassUtils.getGenericReturnType(method));
            } finally {
                MDCUtils.cleanupMDC();
            }
        });
    }

    private <T> T post(PostRequest request, Class<T> returnType) {
        String originResponseBody;
        try {
            originResponseBody = httpClient.post(request);
        } catch (Exception e) {
            originResponseBody = this.handleExceptionAndRetry(request, e);
        }
        originResponseBody = this.applyResponseInterceptor(originResponseBody);
        return JSON.fromJson(originResponseBody, returnType);
    }

    public <T> T put(Object body, Class<T> returnType) {
        PutRequest putRequest = (PutRequest) new HttpRequest.HttpRequestBuilder()
                .url(apiContext.getEndpoint() + request.path())
                .method(HttpMethod.PUT)
                .header("Content-Type", request.contentType())
                .body(body)
                .build();
        this.applyRequestInterceptors(putRequest);
        MDCUtils.setupMDC();
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Execute put method:{}", method);
            }
            //noinspection unchecked
            return CompletableFuture.class.isAssignableFrom(returnType) ? (T) this.putFuture(putRequest) : this.put(putRequest, returnType);
        } finally {
            MDCUtils.cleanupMDC();
        }
    }

    public <T> CompletableFuture<T> putFuture(PutRequest request) {
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        CompletableFuture<String> future = httpClient.putAsync(request);
        return future.thenApply(responseString -> {
            MDCUtils.setContextMap(mdcContext);
            try {
                //noinspection unchecked
                return JSON.fromJson(this.applyResponseInterceptor(responseString), (Class<T>) ClassUtils.getGenericReturnType(method));
            } finally {
                MDCUtils.cleanupMDC();
            }
        });
    }

    private <T> T put(PutRequest request, Class<T> returnType) {
        this.applyRequestInterceptors(request);
        String originResponseBody;
        try {
            originResponseBody = httpClient.put(request);
        } catch (Exception e) {
            originResponseBody = this.handleExceptionAndRetry(request, e);
        }
        originResponseBody = this.applyResponseInterceptor(originResponseBody);
        return JSON.fromJson(originResponseBody, returnType);
    }

    public <T> T delete(Object[] args, Class<T> returnType) {
        Map<String, String> queryParameters = this.resolveQueryParameters(args);
        DeleteRequest deleteRequest = (DeleteRequest) new HttpRequest.HttpRequestBuilder()
                .method(HttpMethod.GET)
                .queryParameters(queryParameters)
                .build();
        this.applyRequestInterceptors(deleteRequest);
        deleteRequest.setUrl(this.buildUrlWithQueryParameters(apiContext.getEndpoint() + request.path(), deleteRequest.getQueryParameters()));
        MDCUtils.setupMDC();
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Execute delete method:{}", method);
            }
            //noinspection unchecked
            return CompletableFuture.class.isAssignableFrom(returnType) ? (T) this.deleteFuture(deleteRequest) : this.delete(deleteRequest, returnType);
        } finally {
            MDCUtils.cleanupMDC();
        }
    }

    public <T> CompletableFuture<T> deleteFuture(DeleteRequest request) {
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        CompletableFuture<String> future = httpClient.deleteAsync(request);
        return future.thenApply(responseString -> {
            MDCUtils.setContextMap(mdcContext);
            try {
                //noinspection unchecked
                return JSON.fromJson(this.applyResponseInterceptor(responseString), (Class<T>) ClassUtils.getGenericReturnType(method));
            } finally {
                MDCUtils.cleanupMDC();
            }
        });
    }

    private <T> T delete(DeleteRequest request, Class<T> returnType) {
        String originResponseBody;
        try {
            originResponseBody = httpClient.delete(request);
        } catch (Exception e) {
            originResponseBody = this.handleExceptionAndRetry(request, e);
        }
        originResponseBody = this.applyResponseInterceptor(originResponseBody);
        return JSON.fromJson(originResponseBody, returnType);
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
                logger.debug("Apply response interceptor:{} ,before={}, after={}", responseInterceptor, originResponseString, interceptedResponseString);
            }
            return interceptedResponseString;
        }
        return originResponseString;
    }

    private String handleExceptionAndRetry(HttpRequest request, Exception exception) {
        Retryable retryable = method.getAnnotation(Retryable.class);
        if (retryable == null) {
            throw new RuntimeException(exception);
        }
        if (exception instanceof CompletionException completionException) {
            Throwable cause = completionException.getCause();
            if (cause instanceof HttpException httpException && httpException.retryable()) {
                return this.retry(request, retryable);
            }
        }
        throw new RuntimeException(exception);
    }

    private String retry(HttpRequest request, Retryable retryable) {
        String originResponseString = null;
        for (int i = 0; i < retryable.maxAttempts(); i++) {
            try {
                logger.debug("Retrying request, attempt {}/{}", i + 1, retryable.maxAttempts());
                TimeUnit.MILLISECONDS.sleep(retryable.delay());
                switch (request) {
                    case GetRequest getRequest -> originResponseString = httpClient.get(getRequest);
                    case PutRequest putRequest -> originResponseString = httpClient.put(putRequest);
                    case PostRequest postRequest -> originResponseString = httpClient.post(postRequest);
                    case DeleteRequest deleteRequest -> originResponseString = httpClient.delete(deleteRequest);
                    default ->
                            throw new IllegalArgumentException("Unsupported request type: " + request.getClass().getName());
                }
                break;
            } catch (Exception e) {
                if (e instanceof CompletionException completionException) {
                    Throwable cause = completionException.getCause();
                    if (cause instanceof HttpException httpException) {
                        if (!httpException.retryable()) {
                            throw httpException;
                        }
                    } else {
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
