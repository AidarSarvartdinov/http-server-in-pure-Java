package com.server.http.server.response;

import com.server.http.server.common.HttpHeaders;
import com.server.http.server.common.HttpStatus;
import com.server.http.server.exception.ResponseContextException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * Represents an HTTP response context containing status, headers, and body.
 */
public class ResponseContext {
    private HttpStatus status;

    private HttpHeaders headers;

    private byte[] responseBody;

    public static ResponseContext build(HttpStatus status) {
        return build(status, null, null);
    }

    public static ResponseContext build(HttpStatus status, HttpHeaders headers) {
        return build(status, headers, null);
    }

    public static ResponseContext build(HttpStatus status, HttpHeaders headers, String responseBody) {
        return buildWithBytes(
                status,
                headers,
                responseBody == null ? null : responseBody.getBytes());
    }

    public static ResponseContext buildWithBytes(HttpStatus status, HttpHeaders headers, byte[] responseBody) {
        ResponseContext context = new ResponseContext();
        context.setStatus(status);
        context.setHeaders(headers);
        context.setResponseBody(responseBody);

        return context;
    }

    /**
     * Assembles the complete HTTP response as a byte array.
     * <p>
     * The response conforms to HTTP/1.1: status line, headers (if any), an empty line,
     * and the body.
     * </p>
     *
     * @return the full HTTP response bytes
     * @throws ResponseContextException if an I/O error occurs
     */
    public byte[] getResponseAsBytes() throws ResponseContextException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.write(("HTTP/1.1 " + status.getCode() + " " + status.getReasonPhrase() + "\r\n").getBytes());
            
            if (headers != null) {
                outputStream.write(headers.toString().getBytes());
            } else {
                outputStream.write("\r\n".getBytes());
            }

            if (responseBody != null) {
                outputStream.write(responseBody);
            }

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new ResponseContextException("Exception trying to get bytes for response", e);
        }
    }

    public void setResponseBody(byte[] responseBody) {
        this.responseBody = responseBody;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public HttpStatus getStatus() {
        return this.status;
    }

    public void setStatus(HttpStatus status) {
        this.status = Objects.requireNonNull(status);
    }

    public byte[] getResponseBody() {
        return responseBody;
    }

}
