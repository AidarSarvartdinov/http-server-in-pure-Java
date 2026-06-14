package com.server.http.json;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represents a JSON object: an unordered collection of key‑value pairs.
 * <p>
 * Keys are strings, values are any {@link JsonValue}. This class provides
 * typed accessors that throw {@link RuntimeException} if the key is missing
 * or the value is of the wrong type.
 * </p>
 *
 * @author Your Name
 * @version 1.0
 */
public class JsonObject implements JsonValue {
    private final Map<String, JsonValue> object;

    public JsonObject() {
        this.object = new HashMap<>();
    }

    @Override
    public JsonObject asObject() {
        return this;
    }

    @Override
    public boolean isObject() {
        return true;
    }

    public void addEntry(Entry<String, JsonValue> entry) {
        object.put(entry.getKey(), entry.getValue());
    }

    public void put(String key, JsonValue value) {
        object.put(key, value);
    }

    public JsonValue get(String key) {
        return object.get(key);
    }

    public String getString(String key) {
        JsonValue jsonValue = get(key);

        if (jsonValue == null ) {
            throw new RuntimeException("Missing key: " + key);
        }

        if (!jsonValue.isString()) {
            throw new RuntimeException("Expected string at key: " + key);
        }
        return jsonValue.asString();
    }

    public int getInt(String key) {
        JsonValue jsonValue = get(key);

        if (jsonValue == null ) {
            throw new RuntimeException("Missing key: " + key);
        }
        
        if (!jsonValue.isNumber()) {
            throw new RuntimeException("Expected integer at key: " + key);
        }
        return jsonValue.asInt();
    }

    public long getLong(String key) {
        JsonValue jsonValue = get(key);

        if (jsonValue == null ) {
            throw new RuntimeException("Missing key: " + key);
        }
        
        if (!jsonValue.isNumber()) {
            throw new RuntimeException("Expected long at key: " + key);
        }
        return jsonValue.asLong();
    }

    public double getDouble(String key) {
        JsonValue jsonValue = get(key);

        if (jsonValue == null ) {
            throw new RuntimeException("Missing key: " + key);
        }
        
        if (!jsonValue.isNumber()) {
            throw new RuntimeException("Expected double at key: " + key);
        }
        return jsonValue.asDouble();
    }
}
