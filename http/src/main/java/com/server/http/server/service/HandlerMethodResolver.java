package com.server.http.server.service;

import com.server.http.server.bind.HandlerHolder;
import com.server.http.server.bind.HandlerMethod;
import com.server.http.server.request.RequestContext;

public class HandlerMethodResolver {
    public HandlerMethod resolve(RequestContext context) {
        return HandlerHolder.getInstance().getHandlerMethods()
                .stream()
                .filter(it -> context.getMethod() == it.getMethod() &&
                        PathPattern.path(it.getPath()).match(context.getPath()))
                .findFirst()
                .orElseGet(() -> {
                    System.out.println("Handler by context not found: " + context);
                    return null;
                });
    }
}
