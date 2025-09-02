package org.fasf.http;


import org.fasf.util.JSON;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest implements Serializable {
    private String url;
    private Map<String, String> headers = new HashMap<>();

    public HttpRequest(String url) {
        this.url = url;
    }

    public HttpRequest(String url, Map<String, String> headers) {
        this.url = url;
        this.headers.putAll(headers);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public static class HttpRequestBuilder {
        private String url;
        private HttpMethod method;
        private final Map<String, String> headers = new HashMap<>();
        private Map<String, String> queryParameters;
        private Object originBody;
        private String body;

        public HttpRequestBuilder() {
        }

        public HttpRequestBuilder url(String url) {
            this.url = url;
            return this;
        }

        public HttpRequestBuilder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public HttpRequestBuilder header(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public HttpRequestBuilder queryParameters(Map<String, String> queryParameters) {
            this.queryParameters = queryParameters;
            return this;
        }

        public HttpRequestBuilder body(Object body) {
            this.originBody = body;
            this.body = JSON.toJson(originBody);
            return this;
        }

        public HttpRequest build() {
            return switch (method) {
                case POST -> new PostRequest(url, headers, originBody, body);
                case GET -> new GetRequest(url, queryParameters);
                case PUT -> new PutRequest(url, headers, originBody, body);
                case DELETE -> new DeleteRequest(url, headers, queryParameters);
                default -> throw new IllegalArgumentException("Invalid HTTP method:" + method);
            };
        }
    }
}
