package org.fasf.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

public interface HttpClient {
    String get(GetRequest request) throws HttpException;

    String post(PostRequest request) throws HttpException;

    class DefaultHttpClient implements HttpClient {
        private final Logger logger = LoggerFactory.getLogger(DefaultHttpClient.class);
        private final RestTemplate restTemplate;

        public DefaultHttpClient() {
            this.restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new CustomResponseErrorHandler());
        }

        @Override
        public String get(GetRequest request) throws HttpException{
            if (logger.isDebugEnabled()) {
                logger.debug(request.toString());
            }
            MultiValueMap<String, String> headers = new HttpHeaders();
            request.getHeaders().forEach(headers::add);
            RequestEntity<Void> requestEntity = new RequestEntity<>(null, headers, HttpMethod.GET, URI.create(request.getUrl()));
            ResponseEntity<String> exchange = restTemplate.exchange(requestEntity, String.class);
            HttpStatus statusCode = (HttpStatus) exchange.getStatusCode();
            if (statusCode.is2xxSuccessful()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("GetResponse:{}", exchange.getBody());
                }
                return exchange.getBody();
            } else {
                throw new HttpException(statusCode);
            }
        }

        @Override
        public String post(PostRequest request) throws HttpException{
            if (logger.isDebugEnabled()) {
                logger.debug(request.toString());
            }
            MultiValueMap<String, String> headers = new HttpHeaders();
            request.getHeaders().forEach(headers::add);
            RequestEntity<String> requestEntity = new RequestEntity<>(request.getBody(), headers, HttpMethod.POST, URI.create(request.getUrl()));
            ResponseEntity<String> exchange = restTemplate.exchange(requestEntity, String.class);
            HttpStatus statusCode = (HttpStatus) exchange.getStatusCode();
            if (statusCode.is2xxSuccessful()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("PostResponse:{}", exchange.getBody());
                }
                return exchange.getBody();
            } else {
                throw new HttpException(statusCode);
            }
        }
    }
}
