package org.fasf.core.http;

import org.fasf.core.util.JSON;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpResponse implements Serializable {
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

    public String getBodyAsString(Charset charset){
        return new String(body, charset);
    }
    public String getBodyAsString() {
        return getBodyAsString(getCharset());
    }

    public Charset getCharset(){
        String contentType = headers.getFirst(HttpHeaders.CONTENT_TYPE);
        if (contentType != null) {
            MediaType mediaType = MediaType.parseMediaType(contentType);
            if (mediaType.getCharset() != null) {
                return mediaType.getCharset();
            }
        }
        return StandardCharsets.UTF_8;
    }

    @Override
    public String toString(){
        return "HttpResponse: status=" + status + ", headers=" + JSON.toJson(getHeaders()) + ", body=" + this.getBodyAsString();
    }
}
