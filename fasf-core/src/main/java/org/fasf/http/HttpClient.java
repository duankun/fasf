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
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface HttpClient {
    default String get(GetRequest request) {
        return this.getAsync(request).join();
    }

    default String post(PostRequest request) {
        return this.postAsync(request).join();
    }

    default String put(PutRequest request) {
        return this.putAsync(request).join();
    }

    default String delete(DeleteRequest request) {
        return this.deleteAsync(request).join();
    }

    CompletableFuture<String> getAsync(GetRequest request);

    CompletableFuture<String> postAsync(PostRequest request);

    CompletableFuture<String> putAsync(PutRequest request);

    CompletableFuture<String> deleteAsync(DeleteRequest request);

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
        public CompletableFuture<String> getAsync(GetRequest request) {
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
                    })
                    .retrieve()
                    .bodyToMono(String.class)
                    .publishOn(responseCallbackScheduler)
                    .onErrorMap(throwable -> {
                        try {
                            MDCUtils.setContextMap(contextMap);
                            return this.handleException(throwable);
                        } finally {
                            MDCUtils.cleanupMDC();
                        }
                    })
                    .toFuture();
        }

        @Override
        public CompletableFuture<String> postAsync(PostRequest request) {
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
                    .retrieve()
                    .bodyToMono(String.class)
                    .publishOn(responseCallbackScheduler)
                    .onErrorMap(throwable -> {
                        try {
                            MDCUtils.setContextMap(contextMap);
                            return this.handleException(throwable);
                        } finally {
                            MDCUtils.cleanupMDC();
                        }
                    })
                    .toFuture();
        }

        @Override
        public CompletableFuture<String> putAsync(PutRequest request) {
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
                    .retrieve()
                    .bodyToMono(String.class)
                    .publishOn(responseCallbackScheduler)
                    .onErrorMap(throwable -> {
                        try {
                            MDCUtils.setContextMap(contextMap);
                            return this.handleException(throwable);
                        } finally {
                            MDCUtils.cleanupMDC();
                        }
                    })
                    .toFuture();
        }

        @Override
        public CompletableFuture<String> deleteAsync(DeleteRequest request) {
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
                    .retrieve()
                    .bodyToMono(String.class)
                    .publishOn(responseCallbackScheduler)
                    .onErrorMap(throwable -> {
                        try {
                            MDCUtils.setContextMap(contextMap);
                            return this.handleException(throwable);
                        } finally {
                            MDCUtils.cleanupMDC();
                        }
                    })
                    .toFuture();
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
