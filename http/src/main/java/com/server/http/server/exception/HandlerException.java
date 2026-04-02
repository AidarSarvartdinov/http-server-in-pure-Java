package com.server.http.server.exception;

public class HandlerException extends RuntimeException {

    public HandlerException() {
    }

    public HandlerException(String message) {
        super(message);
    }

    public HandlerException(Throwable cause) {
        super(cause);
    }

    public HandlerException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
