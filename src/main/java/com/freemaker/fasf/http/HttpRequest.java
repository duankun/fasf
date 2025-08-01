package com.freemaker.fasf.http;

import com.alibaba.fastjson2.JSON;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String url;
    private Map<String, String> headers = new HashMap<>();

    public HttpRequest() {
    }

    public HttpRequest(String url) {
        this.url = url;
    }

    public HttpRequest(String url, Map<String, String> headers) {
        this.url = url;
        this.headers = headers;
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
        private Map<String, String> queryParameters;
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

        public HttpRequestBuilder queryParameters(Map<String, String> queryParameters) {
            this.queryParameters = queryParameters;
            return this;
        }

        public HttpRequestBuilder body(Object body) {
            this.body = JSON.toJSONString(body);
            return this;
        }

        public HttpRequest build() {
            HttpRequest request = null;
            if (method == HttpMethod.POST) {
                request = new PostRequest(url, body);
            } else if (method == HttpMethod.GET) {
                request = new GetRequest(url, queryParameters);
            }
            return request;
        }
    }
}
