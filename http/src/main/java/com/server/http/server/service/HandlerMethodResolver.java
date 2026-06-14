package com.server.http.server.service;

import java.util.logging.Logger;

import com.server.http.server.bind.HandlerHolder;
import com.server.http.server.bind.HandlerMethod;
import com.server.http.server.request.RequestContext;

/**
 * Resolves a {@link HandlerMethod} for a given HTTP request context.
 * <p>
 * Uses the singleton {@link HandlerHolder} to obtain all registered handler methods,
 * then filters them by HTTP method and path pattern matching.
 * </p>
 */
public class HandlerMethodResolver {
    private static final Logger log = Logger.getLogger(HandlerMethodResolver.class.getName());

    /**
     * Resolves a handler method for the given request context.
     * <p>
     * Iterates through all registered handlers, finds the first one where:
     * <ul>
     *   <li>the HTTP method matches, and</li>
     *   <li>the handler's path pattern matches the request path (via {@link PathPattern}).</li>
     * </ul>
     * If no matching handler is found, logs a warning and returns {@code null}.
     * </p>
     *
     * @param context the incoming request context (contains method and path)
     * @return a matching {@code HandlerMethod}, or {@code null} if none found
     */
    public HandlerMethod resolve(RequestContext context) {
        return HandlerHolder.getInstance().getHandlerMethods()
                .stream()
                .filter(it -> context.getMethod() == it.getMethod() &&
                        PathPattern.path(it.getPath()).match(context.getPath()))
                .findFirst()
                .orElseGet(() -> {
                    log.warning("Handler by context not found: " + context);
                    return null;
                });
    }
}
