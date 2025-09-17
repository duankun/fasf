package org.fasf.interceptor.encrypt;

import org.fasf.core.http.*;
import org.fasf.core.interceptor.EncryptTypeEnum;
import org.fasf.core.interceptor.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import java.security.Key;
import java.util.Map;
import java.util.function.BiFunction;

public abstract class EncryptRequestInterceptor implements RequestInterceptor {
    @Value("${fasf.api.encrypt.type.name}")
    private String encryptTypeName;

    @Override
    public void intercept(HttpRequest request) {
        request.addHeader(encryptTypeName, encryptType().name());
        try {
            interceptorInternal(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void encryptRequest(HttpRequest request, BiFunction<String, Key, String> fun, Key secretKey) {
        switch (request) {
            case GetRequest getRequest -> this.getRequestEncrypt(getRequest, fun, secretKey);
            case PutRequest putRequest -> this.putRequestEncrypt(putRequest, fun, secretKey);
            case PostRequest postRequest -> this.postRequestEncrypt(postRequest, fun, secretKey);
            case DeleteRequest deleteRequest -> this.deleteRequestEncrypt(deleteRequest, fun, secretKey);
            default -> throw new IllegalArgumentException("Unexpected http request type: " + request);
        }
    }

    private void getRequestEncrypt(GetRequest getRequest, BiFunction<String, Key, String> fun, Key secretKey) {
        queryParametersEncrypt(getRequest.getQueryParameters(), fun, secretKey);
    }

    private void queryParametersEncrypt(Map<String, String> queryParameters, BiFunction<String, Key, String> fun, Key secretKey) {
        if (!CollectionUtils.isEmpty(queryParameters)) {
            queryParameters.forEach((k, v) -> queryParameters.put(k, fun.apply(v, secretKey)));
        }
    }

    private void postRequestEncrypt(PostRequest postRequest, BiFunction<String, Key, String> fun, Key secretKey) {
        postRequest.setBody(fun.apply(postRequest.getBody(), secretKey));
    }

    private void putRequestEncrypt(PutRequest putRequest, BiFunction<String, Key, String> fun, Key secretKey) {
        postRequestEncrypt(putRequest, fun, secretKey);
    }

    private void deleteRequestEncrypt(DeleteRequest deleteRequest, BiFunction<String, Key, String> fun, Key secretKey) {
        queryParametersEncrypt(deleteRequest.getQueryParameters(), fun, secretKey);
    }

    abstract EncryptTypeEnum encryptType();

    abstract void interceptorInternal(HttpRequest request) throws Exception;
}
