package com.server.http.json;

public class JsonNumber implements JsonValue {
    private Number number;

    public JsonNumber(Number number) {
        this.number = number;
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public Number asNumber() {
        return number;
    }

    @Override
    public int asInt() {
        return number.intValue();
    }

    @Override
    public long asLong() {
        return number.longValue();
    }

    @Override
    public double asDouble() {
        return number.doubleValue();
    }
}
