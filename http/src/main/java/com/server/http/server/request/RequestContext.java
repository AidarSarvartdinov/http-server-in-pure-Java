package com.server.http.server.request;

import com.server.http.server.common.HttpHeaders;
import com.server.http.server.common.HttpMethod;
import com.server.http.server.exception.RequestContextException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequestContext {
    private final HttpMethod method;

    private final String path;

    private final List<String> pathParts;

    private final HttpHeaders headers;

    private String body;

    public RequestContext(HttpMethod method, String path, HttpHeaders headers) {
        this.path = path;
        this.method = method;
        this.pathParts = Arrays.stream(path.split("/")).toList();
        this.headers = headers;
    }

    public static RequestContext buildContext(BufferedReader reader) {
        try {
            String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isBlank()) {
                return null;
            }

            AbstractMap.SimpleEntry<HttpMethod, String> methodWithPath = extractMethodAndPath(requestLine);
            List<String> headers = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                headers.add(line);
            }

            HttpHeaders httpHeaders = HttpHeaders.fromHeaderList(headers);
            System.out.println("HTTP request headers: " + headers);
            var requestContext = new RequestContext(methodWithPath.getKey(), methodWithPath.getValue(), httpHeaders);

            var contentLength = httpHeaders.getFirst("Content-Length");
            if (contentLength != null && !contentLength.isBlank()) {
                int bodySize = Integer.parseInt(contentLength);
                char[] bodyBuffer = new char[bodySize];
                reader.read(bodyBuffer);
                requestContext.setBody(new String(bodyBuffer));
            }

            return requestContext;

        } catch (IOException e) {
            System.out.println("Exception trying to build request context");
            throw new RequestContextException(e);
        }
    }

    private static AbstractMap.SimpleEntry<HttpMethod, String> extractMethodAndPath(String requestLine) {
        String[] parts = requestLine.split(" ");
        return new AbstractMap.SimpleEntry<>(HttpMethod.fromType(parts[0]), parts[1]);
    }

    public boolean hasPath() {
        return path != null && !path.isBlank();
    }

    public String getPart(int index) {
        if (pathParts.size() < index) {
            return null;
        }

        return  pathParts.get(index);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public boolean pathsIsEqualsTo(String actualPath) {
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
