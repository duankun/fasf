package org.fasf.core.http;

import org.fasf.core.util.MDCUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;
import java.util.Objects;

public interface HttpClient {
    Mono<HttpResponse> getAsync(GetRequest request);

    Mono<HttpResponse> postAsync(PostRequest request);

    Mono<HttpResponse> putAsync(PutRequest request);

    Mono<HttpResponse> deleteAsync(DeleteRequest request);

    class DefaultHttpClient implements HttpClient {
        private final Logger logger = LoggerFactory.getLogger(DefaultHttpClient.class);
        private final RestTemplate restTemplate;
        private final Scheduler scheduler;

        public DefaultHttpClient(RestTemplate restTemplate, Scheduler scheduler) {
            this.restTemplate = restTemplate;
            this.scheduler = scheduler;
        }

        @Override
        public Mono<HttpResponse> getAsync(GetRequest request) {
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            return Mono.fromCallable(() -> {
                MDCUtils.setContextMap(contextMap);
                try {
                    if (logger.isDebugEnabled()) {
                        logger.debug("get request:{}", request);
                    }
                } finally {
                    MDCUtils.cleanupMDC();
                }
                HttpHeaders headers = new HttpHeaders();
                request.getHeaders().forEach(headers::add);
                HttpEntity<String> entity = new HttpEntity<>(headers);
                ResponseEntity<String> getResponse = restTemplate.exchange(request.getUrl(), HttpMethod.GET, entity, String.class);
                return new HttpResponse(getResponse.getStatusCode(), getResponse.getHeaders(), Objects.requireNonNull(getResponse.getBody()).getBytes());
            }).subscribeOn(scheduler).onErrorMap(throwable -> {
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
            return Mono.fromCallable(() -> {
                MDCUtils.setContextMap(contextMap);
                try {
                    if (logger.isDebugEnabled()) {
                        logger.debug("post request:{}", request);
                    }
                } finally {
                    MDCUtils.cleanupMDC();
                }
                HttpHeaders headers = new HttpHeaders();
                request.getHeaders().forEach(headers::add);
                HttpEntity<String> entity = new HttpEntity<>(request.getBody(), headers);
                ResponseEntity<String> postResponse = restTemplate.exchange(request.getUrl(), HttpMethod.POST, entity, String.class);
                return new HttpResponse(postResponse.getStatusCode(), postResponse.getHeaders(), Objects.requireNonNull(postResponse.getBody()).getBytes());
            }).subscribeOn(scheduler).onErrorMap(throwable -> {
                MDCUtils.setContextMap(contextMap);
                try {
                    return this.handleException(throwable);
                } finally {
                    MDCUtils.cleanupMDC();
                }
            });
        }

        @Override
        public Mono<HttpResponse> putAsync(PutRequest request) {
            throw new NotImplementedException();
        }

        @Override
        public Mono<HttpResponse> deleteAsync(DeleteRequest request) {
            throw new NotImplementedException();
        }

        private Throwable handleException(Throwable throwable) {
            if (throwable instanceof HttpException) {
                HttpException httpException = (HttpException) throwable;
                logger.warn("Request encounter an error:{} {}", httpException.getCode(), httpException.getMessage());
                return httpException;
            }
            return throwable;
        }
    }

}
