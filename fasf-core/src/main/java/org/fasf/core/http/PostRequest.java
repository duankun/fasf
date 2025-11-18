package org.fasf.core.http;

import org.fasf.core.util.JSON;

import java.util.Map;

public class PostRequest extends HttpRequest {
    private Object originBody;
    private String body;

    public PostRequest(String url, Map<String, String> headers, Map<String,String> queryParameters, Object originBody) {
        super(url, headers,queryParameters);
        this.originBody = originBody;
        this.body = JSON.toJson(originBody);
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
        return "PostRequest: url=" + getUrl() + ", headers=" + JSON.toJson(getHeaders()) + ", body=" + body + ", originBody=" + JSON.toJson(originBody);
    }
}
