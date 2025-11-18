package org.fasf.core.http;

import java.util.Map;

public class PutRequest extends PostRequest{
    public PutRequest(String url, Map<String, String> headers, Map<String, String> queryParameters,Object originBody) {
        super(url, headers, queryParameters, originBody);
    }
}
