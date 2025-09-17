package org.fasf.core.http;

import org.springframework.http.HttpStatus;

public class HttpException extends RuntimeException {
    private int code;

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

    //this exception is retryable or not
    public boolean retryable() {
        return switch (this.code) {
            case 408, 429, 500, 502, 503, 504 -> true;
            default -> false;
        };
    }
}
