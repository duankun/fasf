package com.freemaker.fasf.http;

import com.alibaba.fastjson2.JSON;

import java.util.Map;

public class GetRequest extends HttpRequest {
    private Map<String, String> queryParameters;

    public GetRequest(String url, Map<String, String> queryParameters) {
        super(url);
        this.queryParameters = queryParameters;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(Map<String, String> queryParameters) {
        this.queryParameters = queryParameters;
    }

    @Override
    public String toString(){
        return "GetRequest url:" + getUrl() + " headers:" + JSON.toJSONString(getHeaders()) + " queryParameters:" + JSON.toJSONString(queryParameters);
    }
}
