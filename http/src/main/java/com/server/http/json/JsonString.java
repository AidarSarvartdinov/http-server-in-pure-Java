package com.server.http.json;

public class JsonString implements JsonValue {
    private String value;

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
