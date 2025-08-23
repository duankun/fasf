package org.fasf.http;

import io.netty.channel.ChannelOption;
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
import reactor.core.scheduler.Schedulers;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

import java.time.Duration;
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

        public DefaultHttpClient() {
            ConnectionProvider connectionProvider = ConnectionProvider.builder("high-concurrency-provider")
                    .maxConnections(1000)
                    .pendingAcquireTimeout(Duration.ofSeconds(10))
                    .pendingAcquireMaxCount(5000)
                    .maxIdleTime(Duration.ofSeconds(30))
                    .maxLifeTime(Duration.ofMinutes(5))
                    .evictInBackground(Duration.ofSeconds(10))
                    .build();

            LoopResources loopResources = LoopResources.create("fasf-reactor-io", Runtime.getRuntime().availableProcessors(), true);

            reactor.netty.http.client.HttpClient nettyClient = reactor.netty.http.client.HttpClient.create(connectionProvider)
                    .runOn(loopResources)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000)
                    .responseTimeout(Duration.ofSeconds(15))
                    .keepAlive(true);

            this.webClient = WebClient.builder()
                    .clientConnector(new ReactorClientHttpConnector(nettyClient))
                    .build();
            int threadCount = Math.max(Runtime.getRuntime().availableProcessors() * 10, 100);
            this.responseCallbackScheduler = Schedulers.newBoundedElastic(
                    threadCount,
                    10000,
                    "fasf-response-callback-scheduler"
            );
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
