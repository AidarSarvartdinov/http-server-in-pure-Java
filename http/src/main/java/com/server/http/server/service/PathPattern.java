package com.server.http.server.service;

public class PathPattern {
    private final Segment[] segments;

    private final String originalPath;

    private PathPattern(String path) {
        this.originalPath = path;
        String[] inSegments = path.split("/");

        segments = new Segment[inSegments.length];

        for (int i = 0; i < segments.length; i++) {
            if (inSegments[i].startsWith("{") && inSegments[i].endsWith("}")) {
                segments[i] = new Segment(true, inSegments[i].substring(1, inSegments[i].length() - 1));
            } else {
                segments[i] = new Segment(false, inSegments[i]);
            }
        }
    }

    public static PathPattern path(String path) {
        return new PathPattern(path);
    }

    public boolean match(String input) {
        if (input.equals("/") && !originalPath.equals("/")) {
            return false;
        }
        if (originalPath.equals("/") && !input.equals("/")) {
            return false;
        }
        if (originalPath.equals(input)) {
            return true;
        }
        for (int segmentIndex = 0; segmentIndex < segments.length; segmentIndex++) {
            int i = input.indexOf('/');
            int j = i + 1;
            if (i == -1) {
                i = input.length();
                j = input.length();

                if (segmentIndex != segments.length - 1) {
                    return false;
                }

            } else if (segmentIndex == segments.length - 1) {
                return false;
            }

            Segment segment = segments[segmentIndex];

            if (!segment.isParam && !input.substring(0, i).equals(segment.constant)) {
                return false;
            }

            input = input.substring(j);
        }
        
        return true;
    }

    private static class Segment {
        private final boolean isParam;
        private final String param;
        private final String constant;

        public Segment(boolean isParam, String value) {
            this.isParam = isParam;
            if (isParam) {
                param =value;
                constant = null;
            } else {
                constant = value;
                param = null;
            }
        }
    }
}
