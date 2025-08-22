package org.fasf.http;

import java.util.Map;

public class PutRequest extends PostRequest{
    public PutRequest(String url, Map<String, String> headers, Object originBody, String body) {
        super(url, headers, originBody, body);
    }
}
