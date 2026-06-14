package com.server.http.server.request;

import com.server.http.server.common.HttpHeaders;
import com.server.http.server.common.HttpMethod;
import com.server.http.server.exception.RequestContextException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents an HTTP request context containing method, path, headers, and body.
 * <p>
 * Parses a raw HTTP request from a {@link BufferedReader}. Supports reading the
 * body based on the {@code Content-Length} header. Provides utility methods to
 * access path segments and compare paths.
 * </p>
 */
public class RequestContext {
    private final HttpMethod method;

    private final String path;

    private final List<String> pathParts;

    private final HttpHeaders headers;

    private String body;

    private static final Logger log = Logger.getLogger(RequestContext.class.getName());

    public RequestContext(HttpMethod method, String path, HttpHeaders headers) {
        this.path = path;
        this.method = method;
        this.pathParts = Arrays.stream(path.split("/")).filter(part -> !part.isEmpty()).toList();
        this.headers = headers;
    }

    /**
     * Parses an HTTP request from the given reader.
     * <p>
     * Reads the request line, then headers until an empty line. If {@code Content-Length}
     * is present, reads exactly that many characters into the body. Malformed request
     * lines (missing path) cause a {@link RequestContextException}.
     * </p>
     *
     * @param reader the buffered reader from the client socket
     * @return a populated RequestContext, or {@code null} if the request line is empty
     * @throws RequestContextException if the request is malformed or an I/O error occurs
     */
    public static RequestContext buildContext(BufferedReader reader) throws RequestContextException {
        try {
            String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isBlank()) {
                return null;
            }

            SimpleEntry<HttpMethod, String> methodWithPath = extractMethodAndPath(requestLine);
            List<String> headers = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                headers.add(line);
            }

            HttpHeaders httpHeaders = HttpHeaders.fromHeaderList(headers);
            var requestContext = new RequestContext(methodWithPath.getKey(), methodWithPath.getValue(), httpHeaders);

            var contentLength = httpHeaders.getFirst("Content-Length");
            if (contentLength != null && !contentLength.isBlank()) {
                int bodySize = Integer.parseInt(contentLength);
                int totalRead = 0;
                char[] bodyBuffer = new char[bodySize];
                while (totalRead < bodySize) {
                    int read = reader.read(bodyBuffer, totalRead, bodySize - totalRead);
                    if (read == - 1) break;
                    totalRead += read;
                }
                
                requestContext.setBody(new String(bodyBuffer, 0, totalRead));
            }

            return requestContext;

        } catch (IOException e) {
            log.log(Level.WARNING, "Exception trying to build request context", e);
            throw new RequestContextException(e);
        }
    }

    /**
     * Extracts HTTP method and path from the request line.
     *
     * @param requestLine e.g. "GET /index.html HTTP/1.1"
     * @return a SimpleEntry containing method and path
     * @throws RequestContextException if the line does not contain a path
     */
    private static SimpleEntry<HttpMethod, String> extractMethodAndPath(String requestLine) throws RequestContextException {
        String[] parts = requestLine.split(" ");
        if (parts.length < 2) {
            throw new RequestContextException("Malformed request line: " + requestLine);
        }
        return new SimpleEntry<>(HttpMethod.fromType(parts[0]), parts[1]);
    }

    public boolean hasPath() {
        return path != null && !path.isBlank();
    }

    public String getPart(int index) {
        if (index < 0 || index >= pathParts.size()) {
            return null;
        }

        return  pathParts.get(index);
    }

    public String getLastPart() {
        return getPart(pathParts.size() - 1);
    }

    public HttpHeaders getHeaders() {
        return this.headers;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public HttpMethod getMethod()  {
        return method;
    }

    public String getPath() {
        return path;
    }

    /**
     * Compares the request path to the given string (case‑sensitive).
     *
     * @param actualPath the path to compare with
     * @return true if both paths are equal and non‑blank
     */
    public boolean pathEquals(String actualPath) {
        return hasPath() && path.equals(actualPath);
    }

    @Override
    public String toString() {
        return "RequestContext{" +
                "method=" + method +
                ", path='" + path + '\'' +
                '}';
    }
}
