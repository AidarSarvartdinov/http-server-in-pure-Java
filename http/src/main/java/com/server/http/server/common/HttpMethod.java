package com.server.http.server.common;

import java.util.Arrays;

public enum HttpMethod {
    GET("get"),
    POST("post");

    private final String type;

    HttpMethod(String type) {
        this.type = type;
    }

    public  String getType() {
        return  type;
    }

    /**
     * Converts a case-insensitive string (e.g., "GET", "Post") into the corresponding {@code HttpMethod}.
     *
     * @param type the HTTP method as string
     * @return the matching enum constant
     * @throws IllegalArgumentException if the string does not match any known method
     */
    public static HttpMethod fromType(String type) {
        return Arrays.stream(values())
                .filter(it -> it.type.equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Exception on get http method"));
    }
}