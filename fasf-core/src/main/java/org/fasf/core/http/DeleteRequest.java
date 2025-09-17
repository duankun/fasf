package org.fasf.core.http;

import org.fasf.core.util.JSON;

import java.util.HashMap;
import java.util.Map;

public class DeleteRequest extends HttpRequest {
    private final Map<String, String> originQueryParameters = new HashMap<>();
    private final Map<String, String> queryParameters;

    public DeleteRequest(String url, Map<String, String> headers, Map<String, String> queryParameters) {
        super(url);
        this.originQueryParameters.putAll(queryParameters);
        this.queryParameters = queryParameters;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    @Override
    public String toString() {
        return "DeleteRequest: url=" + getUrl() + ", headers=" + JSON.toJson(getHeaders()) + ", queryParameters=" + JSON.toJson(queryParameters) + ", originQueryParameters=" + JSON.toJson(originQueryParameters);
    }
}
