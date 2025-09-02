package org.fasf.http;


import org.fasf.util.JSON;

import java.util.HashMap;
import java.util.Map;

public class GetRequest extends HttpRequest {
    private final Map<String, String> originQueryParameters = new HashMap<>();
    private final Map<String, String> queryParameters = new HashMap<>();

    public GetRequest(String url, Map<String, String> queryParameters) {
        super(url);
        this.originQueryParameters.putAll(queryParameters);
        this.queryParameters.putAll(queryParameters);
    }


    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public void addParameter(String key, String value) {
        queryParameters.put(key, value);
    }

    @Override
    public String toString() {
        return "GetRequest: url=" + getUrl() + ", headers=" + JSON.toJson(getHeaders()) + ", queryParameters=" + JSON.toJson(queryParameters) + ", originQueryParameters=" + JSON.toJson(originQueryParameters);
    }
}
