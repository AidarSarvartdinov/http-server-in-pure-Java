package com.server.http.json;

/**
 * A JSON string value.
 * <p>
 * Immutable wrapper around a Java {@link String}.
 * </p>
 */
public class JsonString implements JsonValue {
    private final String value;

    public JsonString(String value) {
        this.value = value;
    }

    @Override
    public String asString() {
        return value;
    }

    @Override
    public boolean isString() {
        return true;
    }
}
