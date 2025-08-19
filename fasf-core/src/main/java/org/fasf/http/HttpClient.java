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

    CompletableFuture<String> getAsync(GetRequest request);

    CompletableFuture<String> postAsync(PostRequest request);

    class DefaultHttpClient implements HttpClient {
        private final Logger logger = LoggerFactory.getLogger(DefaultHttpClient.class);
        private final WebClient webClient;

        public DefaultHttpClient() {
            webClient = WebClient.builder()
                    .clientConnector(new ReactorClientHttpConnector(reactor.netty.http.client.HttpClient.create()
                            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                            .responseTimeout(Duration.ofSeconds(30))
                            .keepAlive(true))).build();
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
                    .onErrorMap(throwable -> {
                        try {
                            MDCUtils.setContextMap(contextMap);
                            return handleWebClientException(throwable);
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
                    .onErrorMap(throwable -> {
                        try {
                            MDCUtils.setContextMap(contextMap);
                            return handleWebClientException(throwable);
                        } finally {
                            MDCUtils.cleanupMDC();
                        }
                    })
                    .toFuture();
        }

        private Throwable handleWebClientException(Throwable throwable) {
            if (throwable instanceof WebClientResponseException ex) {
                HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
                logger.warn("Request encounter an error:{} {}", status.value(), status.getReasonPhrase());
                return new HttpException(status);
            }
            return throwable;
        }
    }
}
