package com.server.http.json;

public interface JsonValue {
    default boolean isObject() {
        return false;
    }

    default boolean isNumber() {
        return false;
    }

    default boolean isString() {
        return false;
    }

    default String asString() {
        throw new UnsupportedOperationException("Not a string");
    }

    default Number asNumber() {
        throw new UnsupportedOperationException("Not a number");
    }

    default JsonObject asObject() {
        throw new UnsupportedOperationException("Not an object");
    }

    default int asInt() {
        throw new UnsupportedOperationException("Cannot convert " + getClass().getSimpleName() + " to int");
    }

    default long asLong() {
        throw new UnsupportedOperationException("Cannot convert " + getClass().getSimpleName() + " to Long");
    }

    default double asDouble() {
        throw new UnsupportedOperationException("Cannot convert " + getClass().getSimpleName() + " Double");
    }
}
