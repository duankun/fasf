package org.fasf.http;

import org.fasf.util.JSON;

import java.util.Map;

public class PostRequest extends HttpRequest {
    private Object originBody;
    private String body;

    public PostRequest(String url, String body) {
        super(url);
        this.body = body;
    }

    public PostRequest(String url, Map<String, String> headers, Object originBody, String body) {
        super(url, headers);
        this.originBody = originBody;
        this.body = body;
    }

    public Object getOriginBody() {
        return originBody;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "PostRequest url:" + getUrl() + " headers:" + JSON.toJson(getHeaders()) + " body:" + body + " originBody:" + JSON.toJson(originBody);
    }
}
