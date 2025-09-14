package org.fasf.http;

import org.fasf.util.MDCUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.Map;

public interface HttpClient {
    Mono<HttpResponse> getAsync(GetRequest request);

    Mono<HttpResponse> postAsync(PostRequest request);

    Mono<HttpResponse> putAsync(PutRequest request);

    Mono<HttpResponse> deleteAsync(DeleteRequest request);

    class DefaultHttpClient implements HttpClient {
        private final Logger logger = LoggerFactory.getLogger(DefaultHttpClient.class);
        private final WebClient webClient;
        private final Scheduler responseCallbackScheduler;

        public DefaultHttpClient(WebClient webClient, Scheduler responseCallbackScheduler) {
            this.webClient = webClient;
            this.responseCallbackScheduler = responseCallbackScheduler;
        }

        public DefaultHttpClient(reactor.netty.http.client.HttpClient httpClient, Scheduler responseCallbackScheduler) {
            this(WebClient.builder()
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .build(), responseCallbackScheduler);
        }

        @Override
        public Mono<HttpResponse> getAsync(GetRequest request) {
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            if (logger.isDebugEnabled()) {
                logger.debug("HTTP GetRequest: {}", request);
            }
            return webClient.get()
                    .uri(request.getUrl())
                    .headers(httpHeaders -> {
                        if (request.getHeaders() != null) {
                            request.getHeaders().forEach(httpHeaders::add);
                        }
                    }).exchangeToMono(clientResponse -> {
                        MDCUtils.setContextMap(contextMap);
                        try {
                            HttpStatus httpStatus = (HttpStatus) clientResponse.statusCode();
                            if (httpStatus.isError()) {
                                throw new WebClientResponseException(httpStatus.value(), httpStatus.getReasonPhrase(), null, null, null);
                            }
                            return clientResponse.bodyToMono(byte[].class).map(bytes -> new HttpResponse(clientResponse.statusCode(), clientResponse.headers().asHttpHeaders(), bytes));
                        } finally {
                            MDCUtils.cleanupMDC();
                        }
                    })
                    .publishOn(responseCallbackScheduler)
                    .onErrorMap(throwable -> {
                        MDCUtils.setContextMap(contextMap);
                        try {
                            return this.handleException(throwable);
                        } finally {
                            MDCUtils.cleanupMDC();
                        }
                    });
        }

        @Override
        public Mono<HttpResponse> postAsync(PostRequest request) {
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            if (logger.isDebugEnabled()) {
                logger.debug("HTTP PostRequest: {}", request);
            }
            return webClient.post()
                    .uri(request.getUrl())
                    .headers(httpHeaders -> {
                        if (request.getHeaders() != null) {
                            request.getHeaders().forEach(httpHeaders::add);
                        }
                    })
                    .bodyValue(request.getBody())
                    .exchangeToMono(clientResponse -> {
                        HttpStatus httpStatus = (HttpStatus) clientResponse.statusCode();
                        if (httpStatus.isError()) {
                            throw new WebClientResponseException(httpStatus.value(), httpStatus.getReasonPhrase(), null, null, null);
                        }
                        return clientResponse.bodyToMono(byte[].class).map(bytes -> new HttpResponse(clientResponse.statusCode(), clientResponse.headers().asHttpHeaders(), bytes));
                    })
                    .publishOn(responseCallbackScheduler)
                    .onErrorMap(throwable -> {
                        try {
                            MDCUtils.setContextMap(contextMap);
                            return this.handleException(throwable);
                        } finally {
                            MDCUtils.cleanupMDC();
                        }
                    });
        }

        @Override
        public Mono<HttpResponse> putAsync(PutRequest request) {
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            if (logger.isDebugEnabled()) {
                logger.debug("HTTP PutRequest: {}", request);
            }
            return webClient.put()
                    .uri(request.getUrl())
                    .headers(httpHeaders -> {
                        if (request.getHeaders() != null) {
                            request.getHeaders().forEach(httpHeaders::add);
                        }
                    })
                    .bodyValue(request.getBody())
                    .exchangeToMono(clientResponse -> {
                        HttpStatus httpStatus = (HttpStatus) clientResponse.statusCode();
                        if (httpStatus.isError()) {
                            throw new WebClientResponseException(httpStatus.value(), httpStatus.getReasonPhrase(), null, null, null);
                        }
                        return clientResponse.bodyToMono(byte[].class).map(bytes -> new HttpResponse(clientResponse.statusCode(), clientResponse.headers().asHttpHeaders(), bytes));
                    })
                    .publishOn(responseCallbackScheduler)
                    .onErrorMap(throwable -> {
                        try {
                            MDCUtils.setContextMap(contextMap);
                            return this.handleException(throwable);
                        } finally {
                            MDCUtils.cleanupMDC();
                        }
                    });
        }

        @Override
        public Mono<HttpResponse> deleteAsync(DeleteRequest request) {
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            if (logger.isDebugEnabled()) {
                logger.debug("HTTP DeleteRequest: {}", request);
            }
            return webClient.delete()
                    .uri(request.getUrl())
                    .headers(httpHeaders -> {
                        if (request.getHeaders() != null) {
                            request.getHeaders().forEach(httpHeaders::add);
                        }
                    })
                    .exchangeToMono(clientResponse -> {
                        HttpStatus httpStatus = (HttpStatus) clientResponse.statusCode();
                        if (httpStatus.isError()) {
                            throw new WebClientResponseException(httpStatus.value(), httpStatus.getReasonPhrase(), null, null, null);
                        }
                        return clientResponse.bodyToMono(byte[].class).map(bytes -> new HttpResponse(clientResponse.statusCode(), clientResponse.headers().asHttpHeaders(), bytes));
                    })
                    .publishOn(responseCallbackScheduler)
                    .onErrorMap(throwable -> {
                        try {
                            MDCUtils.setContextMap(contextMap);
                            return this.handleException(throwable);
                        } finally {
                            MDCUtils.cleanupMDC();
                        }
                    });
        }

        private Throwable handleException(Throwable throwable) {
            if (throwable instanceof WebClientResponseException webClientResponseException) {
                HttpStatus status = HttpStatus.valueOf(webClientResponseException.getStatusCode().value());
                logger.warn("Request encounter an error:{} {}", status.value(), status.getReasonPhrase());
                return new HttpException(status);
            }
            return throwable;
        }
    }
}
