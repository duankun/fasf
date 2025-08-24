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
import reactor.util.retry.Retry;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
            return this.getReturn(getRequest, returnType);
        } finally {
            MDCUtils.cleanupMDC();
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
            return this.getReturn(postRequest, returnType);
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
            return this.getReturn(putRequest, returnType);
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
            return this.getReturn(deleteRequest, returnType);
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

    private <T> T getReturn(HttpRequest request, Class<T> returnType) {
        Retryable retryable = method.getAnnotation(Retryable.class);
        Mono<HttpResponse> mono = this.retryWrapper(request, retryable);
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
            HttpResponse httpResponse = mono.block();
            return httpResponse == null ? null : JSON.fromJson(httpResponse.getBodyAsString(), returnType);
        }
    }

    private Mono<HttpResponse> retryWrapper(HttpRequest request, Retryable retryable) {
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        Mono<HttpResponse> mono = retryable == null ? this.getMono(request) : Mono.defer(() -> {
                    MDCUtils.setContextMap(mdcContext);
                    try {
                        return this.getMono(request);
                    } finally {
                        MDCUtils.cleanupMDC();
                    }
                })
                .retryWhen(Retry.backoff(retryable.maxAttempts(), Duration.ofSeconds(retryable.delay()))
                        .maxBackoff(Duration.ofSeconds(retryable.maxBackoff()))
                        .filter(throwable -> {
                            MDCUtils.setContextMap(mdcContext);
                            try {
                                logger.debug("Retrying request due to: {}", throwable.getMessage());
                                if (throwable instanceof HttpException httpException) {
                                    return httpException.retryable();
                                }
                                return false;
                            } finally {
                                MDCUtils.cleanupMDC();
                            }
                        })
                        .doBeforeRetry(retrySignal -> {
                            MDCUtils.setContextMap(mdcContext);
                            try {
                                logger.debug("Retrying request, attempt {}/{}",
                                        retrySignal.totalRetries() + 1,
                                        retryable.maxAttempts());
                            } finally {
                                MDCUtils.cleanupMDC();
                            }
                        })
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                            Throwable failure = retrySignal.failure();
                            if (failure instanceof HttpException httpException) {
                                return httpException;
                            }
                            return new HttpException(500, "Request failed after " + retryable.maxAttempts() + " attempts", failure);
                        })
                )
                .onErrorResume(throwable -> {
                    MDCUtils.setContextMap(mdcContext);
                    try {
                        logger.debug("Request failed after retry {} times", retryable.maxAttempts());
                        if (throwable instanceof HttpException) {
                            return Mono.error(throwable);
                        }
                        return Mono.error(new HttpException(500, "Request failed after retry " + retryable.maxAttempts() + " times", throwable));
                    } finally {
                        MDCUtils.cleanupMDC();
                    }
                });
        return mono.map(httpResponse -> {
            MDCUtils.setContextMap(mdcContext);
            try{
                return this.applyResponseInterceptor(httpResponse);
            } finally {
                MDCUtils.cleanupMDC();
            }
        });
    }

    private Mono<HttpResponse> getMono(HttpRequest request) {
        return switch (request) {
            case GetRequest getRequest -> httpClient.getAsync(getRequest);
            case PutRequest putRequest -> httpClient.putAsync(putRequest);
            case PostRequest postRequest -> httpClient.postAsync(postRequest);
            case DeleteRequest deleteRequest -> httpClient.deleteAsync(deleteRequest);
            default -> throw new IllegalArgumentException("Unsupported request type: " + request.getClass().getName());
        };
    }
}
