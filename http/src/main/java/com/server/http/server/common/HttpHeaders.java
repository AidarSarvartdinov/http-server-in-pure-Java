package com.server.http.server.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents HTTP headers as a multimap (key → list of values).
 * <p>
 * A header may have multiple values (e.g., several {@code Accept} lines or
 * a comma‑separated value).
 * </p>
 * <p>
 * Instances can be created from a raw list of header lines or from a simple
 * key‑value map. The class provides methods to retrieve the first value,
 * add or set values, and produce a string representation suitable for HTTP responses.
 * </p>
 */
public class HttpHeaders {
    private final Map<String, List<String>> headers;

    public HttpHeaders(Map<String, List<String>> headers) {
        this.headers = new HashMap<>(headers);
    }

    /**
     * Creates an {@code HttpHeaders} instance from a list of raw header lines.
     * <p>
     * Each line is expected to be in the format {@code "name: value"}. If a line does not
     * contain a colon, it is silently ignored.
     * </p>
     *
     * @param headerList list of header strings (e.g., {@code "Content-Type: text/html"})
     * @return a new {@code HttpHeaders} instance containing the parsed headers
     */
    public static HttpHeaders fromHeaderList(List<String> headerList) {
        Map<String, List<String>> headers = new HashMap<>();

        headerList.forEach(line -> {
            int colonIndex = line.indexOf(":");
            if (colonIndex != -1) {
                String header = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();

                headers.computeIfAbsent(header, k -> new ArrayList<>()).add(value);
            }
        });

        return new HttpHeaders(headers);
    }

    /**
     * Creates an {@code HttpHeaders} instance from a simple key‑value map.
     * <p>
     * Each entry becomes a header with a single value. The resulting internal structure
     * stores the value as a list containing that single element.
     * </p>
     *
     * @param headerMap a map where each key is a header name and each value is its value
     * @return a new {@code HttpHeaders} instance
     */
    public static HttpHeaders fromHeaderMap(Map<String, String> headerMap) {
        Map<String, List<String>> map = new HashMap<>();
        headerMap.forEach((key, value) -> map.put(key, new ArrayList<>(List.of(value))));
        return new HttpHeaders(map);
    }

    /**
     * Returns the first value associated with the given header name.
     * <p>
     * If the header has multiple values, only the first one is returned.
     * </p>
     *
     * @param key the header name (case‑sensitive)
     * @return the first value, or {@code null} if the header does not exist
     */
    public String getFirst(String key) {
        List<String> values = headers.get(key);
        return values != null && !values.isEmpty() ? values.get(0) : null;
    }

    /**
     * Returns all values associated with the given header name.
     *
     * @param key the header name (case‑sensitive)
     * @return the list of values
     */
    public List<String> get(String key) {
        List<String> values = headers.get(key);
        if (values == null) return List.of();

        return new ArrayList<>(values);
    }

    /**
     * Adds a value to the specified header.
     * <p>
     * If the header already exists, the new value is appended to its list.
     * Otherwise, a new header with a single value is created.
     * </p>
     *
     * @param key   the header name (case‑sensitive)
     * @param value the value to add
     */
    public void add(String key, String value) {
        List<String> currentValues = headers.computeIfAbsent(key, k -> new ArrayList<>());
        currentValues.add(value);
    }

    /**
     * Adds multiple values to the specified header.
     * <p>
     * If the header already exists, the new values are appended to its list.
     * If the {@code values} list is {@code null}, the method does nothing.
     * </p>
     *
     * @param key    the header name (case‑sensitive)
     * @param values the list of values to add
     */
    public void addAll(String key, List<String> values) {
        if (values == null) return;

        List<String> currentValues = headers.computeIfAbsent(key, k -> new ArrayList<>());
        currentValues.addAll(values);
    }

    /**
     * Adds all headers from the given map to this instance.
     *
     * @param newValues a map of header names to lists of values
     */
    public void addAll(Map<String, List<String>> newValues) {
        newValues.forEach(this::addAll);
    }

    /**
     * Sets a header to a single value, replacing any existing values.
     *
     * @param key   the header name (case‑sensitive)
     * @param value the new value
     */
    public void set(String key, String value) {
        List<String> newValueList = new ArrayList<>();
        newValueList.add(value);
        headers.put(key, newValueList);
    }

    /**
     * Sets multiple headers from a simple key‑value map.
     * <p>
     * If the {@code values} map is {@code null}, the method does nothing.
     * </p>
     *
     * @param values a map of header names to single values
     */
    public void setAll(Map<String, String> values) {
        if (values == null) return;

        values.forEach(this::set);
    }

    /**
     * Returns a string representation of the headers suitable for HTTP responses.
     * @return a properly formatted HTTP header block
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (var entry : headers.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();

            stringBuilder.append(key).append(": ");

            for (int i = 0; i < values.size(); i++) {
                stringBuilder.append(values.get(i));
                if (i < values.size() - 1) {
                    stringBuilder.append(", ");
                }
            }

            stringBuilder.append("\r\n");
        }
        stringBuilder.append("\r\n");

        return stringBuilder.toString();
    }
}
