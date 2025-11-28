package org.fasf.core.http;

import org.springframework.http.HttpStatus;

public class HttpException extends RuntimeException {
    private int code;
    private long totalRetries;

    public HttpException(HttpStatus status) {
        this(status.value(), "Request failed: " + status.getReasonPhrase());
    }

    public HttpException(int code, String message) {
        super(message);
        this.code = code;
    }

    public HttpException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public HttpException(Throwable cause) {
        super(cause);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getTotalRetries() {
        return totalRetries;
    }

    public void setTotalRetries(long totalRetries) {
        this.totalRetries = totalRetries;
    }

    //this exception is retryable or not
    public boolean retryable() {
        switch (this.code) {
            case 408:
            case 429:
            case 500:
            case 502:
            case 503:
            case 504:
                return true;
            default:
                return false;
        }
    }
}
