package com.server.http.json;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class JsonObject implements JsonValue {
    private Map<String, JsonValue> object;

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

    public JsonValue get(String key) {
        return object.get(key);
    }

    public String getString(String key) {
        JsonValue jsonValue = get(key);

        if (jsonValue == null ) return null;

        if (!jsonValue.isString()) {
            throw new RuntimeException("Excpeted string at key: " + key);
        }
        return jsonValue.asString();
    }

    public int getInt(String key) {
        JsonValue jsonValue = get(key);

        if (jsonValue == null ) {
            throw new RuntimeException("Missing or null key: " + key);
        }
        
        if (!jsonValue.isNumber()) {
            throw new RuntimeException("Excpeted integer at key: " + key);
        }
        return jsonValue.asInt();
    }

    public long getLong(String key) {
        JsonValue jsonValue = get(key);

        if (jsonValue == null ) {
            throw new RuntimeException("Missing or null key: " + key);
        }
        
        if (!jsonValue.isNumber()) {
            throw new RuntimeException("Excpeted long at key: " + key);
        }
        return jsonValue.asLong();
    }

    public double getDouble(String key) {
        JsonValue jsonValue = get(key);

        if (jsonValue == null ) {
            throw new RuntimeException("Missing or null key: " + key);
        }
        
        if (!jsonValue.isNumber()) {
            throw new RuntimeException("Excpeted double at key: " + key);
        }
        return jsonValue.asDouble();
    }
}
