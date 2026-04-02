package com.server.http.server.exception;

public class RequestContextException extends RuntimeException {

    public RequestContextException() {
    }

    public RequestContextException(String message) {
        super(message);
    }

    public RequestContextException(Throwable cause) {
        super(cause);
    }

    public RequestContextException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
