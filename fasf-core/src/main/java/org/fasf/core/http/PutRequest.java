package org.fasf.core.http;

import java.util.Map;

public class PutRequest extends PostRequest{
    public PutRequest(String url, Map<String, String> headers, Object originBody) {
        super(url, headers, originBody);
    }
}
