package com.server.http.server.exception;

public class ResponseContextException extends RuntimeException {

    public ResponseContextException() {
    }

    public ResponseContextException(String message) {
        super(message);
    }

    public ResponseContextException(Throwable cause) {
        super(cause);
    }

    public ResponseContextException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
