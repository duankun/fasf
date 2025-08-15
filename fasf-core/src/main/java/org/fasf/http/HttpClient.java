package org.fasf.http;

import org.fasf.interceptor.TraceIdInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Duration;
import java.util.Map;

public interface HttpClient {
    String get(GetRequest request) throws HttpException;

    String post(PostRequest request) throws HttpException;

    class DefaultHttpClient implements HttpClient {
        private final Logger logger = LoggerFactory.getLogger(DefaultHttpClient.class);
        private final RestTemplate restTemplate;

        public DefaultHttpClient() {
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setConnectTimeout((int) Duration.ofSeconds(5).toMillis());
            requestFactory.setReadTimeout((int) Duration.ofSeconds(30).toMillis());

            this.restTemplate = new RestTemplateBuilder()
                    .requestFactory(() -> requestFactory)
                    .errorHandler(new CustomResponseErrorHandler())
                    .build();
        }

        @Override
        public String get(GetRequest request) throws HttpException {
            ResponseEntity<String> exchange = executeRequest(request);
            return exchange.getBody();
        }

        @Override
        public String post(PostRequest request) throws HttpException {
            ResponseEntity<String> exchange = executeRequest(request);
            return exchange.getBody();
        }

        private ResponseEntity<String> executeRequest(HttpRequest request) {
            long startTime = System.currentTimeMillis();
            MultiValueMap<String, String> httpHeaders = new HttpHeaders();
            Map<String, String> headers = request.getHeaders();
            String traceId = null;
            if (headers != null) {
                traceId = headers.get(TraceIdInterceptor.TRACE_ID);
                headers.forEach(httpHeaders::add);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("HTTP Request [{}]: {}",
                        traceId, request);
            }
            String body = null;
            HttpMethod httpMethod = null;
            if (request instanceof GetRequest) {
                httpMethod = HttpMethod.GET;
            } else if (request instanceof PostRequest postRequest) {
                body = postRequest.getBody();
                httpMethod = HttpMethod.POST;
            }
            RequestEntity<String> requestEntity = new RequestEntity<>(body, httpHeaders, httpMethod, URI.create(request.getUrl()));
            ResponseEntity<String> exchange = restTemplate.exchange(requestEntity, String.class);
            long duration = System.currentTimeMillis() - startTime;

            if (logger.isDebugEnabled()) {
                logger.debug("HTTP Response [{}]: Status={}, Duration={}ms, ResponseBody={} ",
                        traceId, exchange.getStatusCode(), duration,
                        exchange.getBody() != null ? exchange.getBody() : null);
            }
            return exchange;
        }
    }
}
