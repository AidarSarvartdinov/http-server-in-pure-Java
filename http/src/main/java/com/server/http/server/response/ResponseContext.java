package com.server.http.server.response;

import com.server.http.server.common.HttpStatus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ResponseContext {
    private HttpStatus status;

    public HttpStatus getStatus() {
        return this.status;
    }

    public void setStatus(final HttpStatus status) {
        this.status = status;
    }

    public static ResponseContext build(HttpStatus status) {
        ResponseContext context = new ResponseContext();
        context.setStatus(status);
        return context;
    }

    public byte[] getResponseAsBytes() {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.write(("HTTP/1.1 " + status.getCode() + " " + status.getReasonPhrase() + "\r\n").getBytes());
            //TODO: headers
            outputStream.write("\r\n".getBytes());
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Exception trying to get bytes for response", e);
        }
    }


}
