package org.fasf.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

public interface HttpClient {
    <T> T get(Class<T> returnType, GetRequest request);
    <T> T post(Class<T> returnType, PostRequest request);

    class DefaultHttpClient implements HttpClient {
        private final RestTemplate restTemplate;

        public DefaultHttpClient() {
            this.restTemplate = new RestTemplate();
        }

        @Override
        public <T> T get(Class<T> returnType, GetRequest request) {
            MultiValueMap<String, String> headers = new HttpHeaders();
            request.getHeaders().forEach(headers::add);
            RequestEntity<Void> requestEntity = new RequestEntity<>(null, headers, HttpMethod.GET, URI.create(request.getUrl()));
            return restTemplate.exchange(requestEntity, returnType).getBody();
        }

        @Override
        public <T> T post(Class<T> returnType, PostRequest request) {
            MultiValueMap<String, String> headers = new HttpHeaders();
            request.getHeaders().forEach(headers::add);
            RequestEntity<String> requestEntity = new RequestEntity<>(request.getBody(), headers, HttpMethod.POST, URI.create(request.getUrl()));
            return restTemplate.exchange(requestEntity, returnType).getBody();
        }
    }
}
