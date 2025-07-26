package com.freemaker.fasf.http;

public interface HttpClient {
    <T> T get(Class<T> returnType, HttpRequest request);
    <T> T post(Class<T> returnType, PostRequest request);

    class DefaultHttpClient implements HttpClient {

        @Override
        public <T> T get(Class<T> returnType, HttpRequest request) {
            return null;
        }

        @Override
        public <T> T post(Class<T> returnType, PostRequest request) {
            return null;
        }
    }
}
