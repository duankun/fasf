package com.freemaker.fasf.http;

import com.alibaba.fastjson2.JSON;

public class PostRequest extends HttpRequest {
    private String body;

    public PostRequest(String url, String body) {
        super(url);
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString(){
        return "PostRequest url:" + getUrl() + " headers:" + JSON.toJSONString(getHeaders()) + " body:" + body;
    }
}
