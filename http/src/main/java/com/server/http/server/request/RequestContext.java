package com.server.http.server.request;

import com.server.http.server.common.HttpMethod;

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

    public RequestContext(HttpMethod method, String path) {
        this.path = path;
        this.method = method;
        this.pathParts = Arrays.stream(path.split("/")).toList();
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
            System.out.println("HTTP request headers: " + headers);
            var requestContext = new RequestContext(methodWithPath.getKey(), methodWithPath.getValue());
            return requestContext;

        } catch (IOException e) {
            System.out.println("Exception trying to build request context");
            throw new RuntimeException(e);
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
