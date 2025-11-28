package org.fasf.core.spring.proxy;

import org.fasf.core.annotation.*;
import org.fasf.core.http.*;
import org.fasf.core.interceptor.RequestInterceptor;
import org.fasf.core.interceptor.ResponseInterceptor;
import org.fasf.core.spring.context.ApiContext;
import org.fasf.core.util.ClassUtils;
import org.fasf.core.util.JSON;
import org.fasf.core.util.MDCUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MethodHandler {
    private final Logger logger = LoggerFactory.getLogger(MethodHandler.class);
    private final Method method;
    private final RequestMapping requestMapping;
    private final ApiContext apiContext;
    private final HttpClient httpClient;

    public MethodHandler(Method method, ApiContext apiContext, HttpClient httpClient) {
        this.method = method;
        this.requestMapping = method.getAnnotation(RequestMapping.class);
        this.apiContext = apiContext;
        this.httpClient = httpClient;
    }

    public Object invoke(Object[] args) {
        Class<?> returnType = method.getReturnType();
        HttpMethod httpMethod = requestMapping.method();
        if (httpMethod == HttpMethod.GET) {
            return this.get(args, returnType);
        } else if (httpMethod == HttpMethod.PUT) {
            return this.put(args, returnType);
        } else if (httpMethod == HttpMethod.POST) {
            return this.post(args, returnType);
        } else if (httpMethod == HttpMethod.DELETE) {
            return this.delete(args, returnType);
        } else {
            throw new IllegalArgumentException("Unsupported method: " + requestMapping.method());
        }
    }

    public <T> T get(Object[] args, Class<T> returnType) {
        GetRequest getRequest = (GetRequest) new HttpRequest.HttpRequestBuilder()
                .method(HttpMethod.GET)
                .queryParameters(this.resolveQueryParameters(args))
                .build();
        MDCUtils.setupMDC();
        try {
            this.applyRequestInterceptors(getRequest);
            getRequest.setUrl(this.buildUrlWithQueryParameters(this.resolvePathParameters(apiContext.getEndpoint() + requestMapping.path(), args), getRequest.getQueryParameters()));
            if (logger.isDebugEnabled()) {
                logger.debug("Execute get method:{}", method);
            }
            return this.getReturn(getRequest, returnType);
        } finally {
            MDCUtils.cleanupMDC();
        }
    }

    private String resolvePathParameters(String url, Object[] args) {
        if (args == null || args.length == 0) {
            return url;
        }
        for (int i = 0; i < args.length; i++) {
            MethodParameter methodParameter = new MethodParameter(method, i);
            if (methodParameter.hasParameterAnnotation(PathParam.class)) {
                PathParam parameterAnnotation = methodParameter.getParameterAnnotation(PathParam.class);
                if (parameterAnnotation != null) {
                    url = url.replace("{" + parameterAnnotation.value() + "}", args[i].toString());
                }
            }
        }
        return url;
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
        if (CollectionUtils.isEmpty(queryParameters)) {
            return baseUrl;
        }

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl);
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParameters.forEach(queryParams::add);
        uriComponentsBuilder.queryParams(queryParams);

        return uriComponentsBuilder.toUriString();
    }

    public <T> T post(Object[] args, Class<T> returnType) {
        PostRequest postRequest = (PostRequest) new HttpRequest.HttpRequestBuilder()
                .method(HttpMethod.POST)
                .header("Content-Type", requestMapping.contentType())
                .queryParameters(this.resolveQueryParameters(args))
                .body(getRequestBody(args))
                .build();
        MDCUtils.setupMDC();
        try {
            this.applyRequestInterceptors(postRequest);
            postRequest.setUrl(this.buildUrlWithQueryParameters(this.resolvePathParameters(apiContext.getEndpoint() + requestMapping.path(), args), postRequest.getQueryParameters()));
            if (logger.isDebugEnabled()) {
                logger.debug("Execute post method:{}", method);
            }
            return this.getReturn(postRequest, returnType);
        } finally {
            MDCUtils.cleanupMDC();
        }
    }

    private Object getRequestBody(Object[] args) {
        if (args == null) {
            return null;
        }
        if (args.length == 1) {
            return args[0];
        } else {
            for (int i = 0; i < args.length; i++) {
                MethodParameter methodParameter = new MethodParameter(method, i);
                if (methodParameter.hasParameterAnnotation(RequestBody.class)) {
                    return args[i];
                }
            }
        }
        return null;
    }

    public <T> T put(Object[] args, Class<T> returnType) {
        PutRequest putRequest = (PutRequest) new HttpRequest.HttpRequestBuilder()
                .method(HttpMethod.PUT)
                .header("Content-Type", requestMapping.contentType())
                .queryParameters(this.resolveQueryParameters(args))
                .body(getRequestBody(args))
                .build();
        MDCUtils.setupMDC();
        try {
            this.applyRequestInterceptors(putRequest);
            putRequest.setUrl(this.buildUrlWithQueryParameters(this.resolvePathParameters(apiContext.getEndpoint() + requestMapping.path(), args), putRequest.getQueryParameters()));
            if (logger.isDebugEnabled()) {
                logger.debug("Execute put method:{}", method);
            }
            return this.getReturn(putRequest, returnType);
        } finally {
            MDCUtils.cleanupMDC();
        }
    }

    public <T> T delete(Object[] args, Class<T> returnType) {
        DeleteRequest deleteRequest = (DeleteRequest) new HttpRequest.HttpRequestBuilder()
                .method(HttpMethod.DELETE)
                .queryParameters(this.resolveQueryParameters(args))
                .build();
        MDCUtils.setupMDC();
        try {
            this.applyRequestInterceptors(deleteRequest);
            deleteRequest.setUrl(this.buildUrlWithQueryParameters(this.resolvePathParameters(apiContext.getEndpoint() + requestMapping.path(), args), deleteRequest.getQueryParameters()));
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
                logger.debug("Apply request interceptors: {}", requestInterceptors.stream().map(RequestInterceptor::getName).collect(Collectors.toList()));
            }
        }
    }

    private HttpResponse applyResponseInterceptor(HttpResponse httpResponse) {
        ResponseInterceptor responseInterceptor = apiContext.getResponseInterceptor(method);
        if (responseInterceptor != null) {
            String responseInterceptorName = responseInterceptor.getName();
            if (logger.isDebugEnabled()) {
                logger.debug("Apply response interceptor:{},before={}", responseInterceptorName, httpResponse);
            }
            responseInterceptor.intercept(httpResponse);
            if (logger.isDebugEnabled()) {
                logger.debug("Apply response interceptor:{},after={}", responseInterceptorName, httpResponse);
            }
        }
        return httpResponse;
    }

    private <T> T getReturn(HttpRequest request, Class<T> returnType) {
        Retry retry = method.getAnnotation(Retry.class);
        Mono<HttpResponse> mono = this.retryWrapper(request, retry);
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
            //noinspection unchecked
            return httpResponse == null ? null : HttpResponse.class.isAssignableFrom(returnType) ? (T) httpResponse : JSON.fromJson(httpResponse.getBodyAsString(), returnType);
        }
    }

    private Mono<HttpResponse> retryWrapper(HttpRequest request, Retry retry) {
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        Mono<HttpResponse> mono = retry == null ? this.execute(request) : Mono.defer(() -> {
                    MDCUtils.setContextMap(mdcContext);
                    try {
                        return this.execute(request);
                    } finally {
                        MDCUtils.cleanupMDC();
                    }
                })
                .retryWhen(reactor.util.retry.Retry.backoff(retry.maxAttempts(), Duration.ofSeconds(retry.delay()))
                        .maxBackoff(Duration.ofSeconds(retry.maxBackoff()))
                        .filter(throwable -> {
                            MDCUtils.setContextMap(mdcContext);
                            try {
                                if (throwable instanceof HttpException) {
                                    HttpException httpException = (HttpException) throwable;
                                    boolean retryable = httpException.retryable();
                                    if (retryable) {
                                        logger.debug("Retrying request due to: {}", throwable.getMessage());
                                    } else {
                                        logger.warn("Non-retryable exception due to: {}", throwable.getMessage());
                                    }
                                    return retryable;
                                }
                                return false;
                            } finally {
                                MDCUtils.cleanupMDC();
                            }
                        })
                        .doBeforeRetry(retrySignal -> {
                            MDCUtils.setContextMap(mdcContext);
                            try {
                                logger.debug("Retrying request, attempt {}/{}", retrySignal.totalRetries() + 1, retry.maxAttempts());
                            } finally {
                                MDCUtils.cleanupMDC();
                            }
                        })
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                            Throwable failure = retrySignal.failure();
                            if (failure instanceof HttpException) {
                                HttpException httpException = (HttpException) failure;
                                httpException.setTotalRetries(retrySignal.totalRetries() + 1);
                                return httpException;
                            }
                            return new HttpException(500, "Request failed after " + retry.maxAttempts() + " attempts", failure);
                        })
                )
                .onErrorResume(throwable -> {
                    MDCUtils.setContextMap(mdcContext);
                    try {
                        if (throwable instanceof HttpException) {
                            HttpException httpException = (HttpException) throwable;
                            logger.debug("Request failed after retry {} times", httpException.getTotalRetries());
                            return Mono.error(throwable);
                        }
                        return Mono.error(new HttpException(500, "Request failed after retry " + retry.maxAttempts() + " times", throwable));
                    } finally {
                        MDCUtils.cleanupMDC();
                    }
                });
        return mono.map(httpResponse -> {
            MDCUtils.setContextMap(mdcContext);
            try {
                return this.applyResponseInterceptor(httpResponse);
            } finally {
                MDCUtils.cleanupMDC();
            }
        });
    }

    private Mono<HttpResponse> execute(HttpRequest request) {
        if (request instanceof GetRequest) {
            return httpClient.getAsync((GetRequest) request);
        } else if (request instanceof PutRequest) {
            return httpClient.putAsync((PutRequest) request);
        } else if (request instanceof PostRequest) {
            return httpClient.postAsync((PostRequest) request);
        } else if (request instanceof DeleteRequest) {
            return httpClient.deleteAsync((DeleteRequest) request);
        } else {
            throw new IllegalArgumentException("Unsupported request type: " + request.getClass().getName());
        }
    }
}
