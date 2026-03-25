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

    public static HttpMethod fromType(String type) {
        return Arrays.stream(values())
                .filter(it -> it.type.equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Exception on get http method"));
    }
}