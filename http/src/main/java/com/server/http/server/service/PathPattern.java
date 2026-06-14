package com.server.http.server.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Path pattern matcher that supports constant segments and parameter
 * placeholders.
 * <p>
 * A parameter is denoted by curly braces, e.g., {@code "/users/{id}"} and
 * matches
 * any single path segment.
 * </p>
 */
public class PathPattern {
    private final String[] patternSegments;

    private final String originalPath;

    private PathPattern(String path) {
        this.originalPath = path;
        String[] parts = path.split("/");

        List<String> list = new ArrayList<>();
        for (String part : parts) {
            if (!part.isEmpty()) {
                list.add(part);
            }
        }
        this.patternSegments = list.toArray(new String[list.size()]);
    }

    public static PathPattern path(String path) {
        return new PathPattern(path);
    }

    /**
     * Matches an input path against this pattern.
     * <p>
     * Empty segments (e.g., from leading/trailing or double slashes)
     * are ignored during matching.
     * </p>
     * <p>
     * Examples:
     * <ul>
     * <li>{@code PathPattern.path("/users/123").match("/users/123")} →
     * {@code true}</li>
     * <li>{@code PathPattern.path("/users/{id}").match("/users/456")} →
     * {@code true}</li>
     * <li>{@code PathPattern.path("/users/{id}").match("/users/456/extra")} →
     * {@code false}</li>
     * <li>{@code PathPattern.path("/").match("/")} → {@code true}</li>
     * <li>{@code PathPattern.path("/").match("/anything")} → {@code false}</li>
     * </ul>
     * </p>
     *
     * @param inputPath the actual request path (may be {@code null})
     * @return {@code true} if the path matches, {@code false} otherwise
     */
    public boolean match(String inputPath) {
        String[] inputParts = inputPath.split("/");
        List<String> inputSegments = new ArrayList<>();
        for (String part : inputParts) {
            if (!part.isEmpty()) {
                inputSegments.add(part);
            }
        }

        if (inputSegments.size() != patternSegments.length) {
            return false;
        }

        for (int i = 0; i < patternSegments.length; i++) {
            String pattern = patternSegments[i];
            String input = inputSegments.get(i);

            if (pattern.startsWith("{") && pattern.endsWith("}"))
                continue;

            if (!pattern.equals(input)) {
                return false;
            }
        }
        return true;
    }
}
