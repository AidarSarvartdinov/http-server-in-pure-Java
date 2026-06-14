package com.server.http.server.interceptor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.zip.GZIPOutputStream;

import com.server.http.server.common.HttpHeaders;
import com.server.http.server.exception.InterceptorException;
import com.server.http.server.request.RequestContext;
import com.server.http.server.response.ResponseContext;

/**
 * Interceptor that applies gzip compression to the response body if the client
 * supports it (via {@code Accept-Encoding: gzip}).
 * <p>
 * If compression is applied, the {@code Content-Encoding} header is set to
 * {@code gzip},
 * and the {@code Content-Length} header is updated to the compressed size.
 * </p>
 * <p>
 * If {@code responseContext.getHeaders()} returns {@code null}, a new empty
 * header map
 * is created and set. The interceptor does nothing if the request lacks gzip
 * support
 * or the response body is {@code null}.
 * </p>
 */
public class EncodeInterceptor implements Interceptor {
    @Override
    public void beforeSendResponse(RequestContext requestContext, ResponseContext responseContext) {
        if (responseContext.getHeaders() == null) {
            responseContext.setHeaders(new HttpHeaders(new HashMap<>()));
        }

        String acceptEncoding = requestContext.getHeaders().getFirst("Accept-Encoding");
        if (acceptEncoding != null && !acceptEncoding.isBlank() && responseContext.getResponseBody() != null) {
            boolean supportsGzip = Arrays.stream(acceptEncoding.split(","))
                    .anyMatch(enc -> enc.trim().equalsIgnoreCase("gzip"));
            if (supportsGzip) {
                byte[] compressed = compressResponseBody(responseContext.getResponseBody());
                responseContext.getHeaders().set("Content-Length", String.valueOf(compressed.length));
                responseContext.getHeaders().set("Content-Encoding", "gzip");
                responseContext.setResponseBody(compressed);
            }
        }
    }

    /**
     * Compresses a byte array using GZIP.
     *
     * @param responseBody the uncompressed body
     * @return compressed bytes
     * @throws InterceptorException if an I/O error occurs during compression
     */
    private byte[] compressResponseBody(byte[] responseBody) throws InterceptorException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream)) {
            gzipOutputStream.write(responseBody);
            gzipOutputStream.finish();
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new InterceptorException("Exception trying to gzip response body!");
        }
    }
}
