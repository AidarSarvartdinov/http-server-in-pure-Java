package com.server.http.server.interceptor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;

import com.server.http.server.exception.InterceptorException;
import com.server.http.server.request.RequestContext;
import com.server.http.server.response.ResponseContext;

public class EncodeInterceptor implements Interceptor {
    @Override
    public void beforeSendResponse(RequestContext requestContext, ResponseContext responseContext) {
        String acceptEncoding = requestContext.getHeaders().getFirst("Accept-Encoding");
        if (acceptEncoding != null && !acceptEncoding.isBlank() && responseContext.getResponseBody() != null) {
            String[] parts = acceptEncoding.split(",");
            Arrays.stream(parts)
                .filter(it -> it.trim().equalsIgnoreCase("gzip"))
                .findFirst()
                .ifPresent(gzipString -> {
                    byte[] responseBody = compressResponseBody(responseContext.getResponseBody());
                    responseContext.getHeaders().set("Content-Length", String.valueOf(responseBody.length));
                    responseContext.getHeaders().set("Content-Encoding", "gzip");
                    responseContext.setResponseBody(responseBody);
                });
        }
    }

    private byte[] compressResponseBody(byte[] responseBody) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream)) {
                gzipOutputStream.write(responseBody);
            }
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new InterceptorException("Exception trying to gzip response body! " + new String(responseBody));
        }
    }
}
