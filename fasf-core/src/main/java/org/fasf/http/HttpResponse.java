package org.fasf.http;

import org.fasf.util.JSON;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpResponse {
    private HttpStatusCode status;
    private HttpHeaders headers;
    private byte[] body;

    public HttpResponse(HttpStatusCode status, HttpHeaders headers, byte[] body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public HttpStatusCode getStatus() {
        return status;
    }

    public void setStatus(HttpStatusCode status) {
        this.status = status;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public String getBodyAsString() {
        Charset charset = StandardCharsets.UTF_8;
        String contentType = headers.getFirst(HttpHeaders.CONTENT_TYPE);
        if (contentType != null) {
            MediaType mediaType = MediaType.parseMediaType(contentType);
            if (mediaType.getCharset() != null) {
                charset = mediaType.getCharset();
            }
        }
        return new String(body, charset);
    }

    @Override
    public String toString(){
        return "HttpResponse: status=" + status + ", headers=" + JSON.toJson(getHeaders()) + ", body=" + this.getBodyAsString();
    }
}
