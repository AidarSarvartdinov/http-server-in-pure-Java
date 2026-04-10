package com.server.http.server.service;

import java.util.logging.Logger;

import com.server.http.server.bind.HandlerHolder;
import com.server.http.server.bind.HandlerMethod;
import com.server.http.server.request.RequestContext;

public class HandlerMethodResolver {
    private static final Logger log = Logger.getLogger(HandlerMethodResolver.class.getName());
    public HandlerMethod resolve(RequestContext context) {
        return HandlerHolder.getInstance().getHandlerMethods()
                .stream()
                .filter(it -> context.getMethod() == it.getMethod() &&
                        PathPattern.path(it.getPath()).match(context.getPath()))
                .findFirst()
                .orElseGet(() -> {
                    // System.out.println("Handler by context not found: " + context);
                    log.warning("Handler by context not found: " + context);
                    return null;
                });
    }
}
