package com.server.http.json;

import java.util.Map;

public class JsonParser {
    private String json;
    private int pos;

    public JsonParser(String json) {
        this.json = json;
        pos = 0;
    }

    public JsonObject parseObject() {
        expect('{');
        skipWhitespace();

        JsonObject jsonObject = new JsonObject();

        while (peek() != '}') {
            skipWhitespace();
            Map.Entry<String, JsonValue> keyValue = parseKeyValue();
            jsonObject.addEntry(keyValue);
            if (peek() == ',') {
                next();
            }
            skipWhitespace();
        }
        next();

        return jsonObject;
    }

    private Map.Entry<String, JsonValue> parseKeyValue() {
        StringBuilder keyBuilder = new StringBuilder();
        expect('"');

        while (peek() != '"') {
            keyBuilder.append(next());
        }
        next();
        skipWhitespace();

        expect(':');

        skipWhitespace();

        JsonValue value = null;

        if (peek() == '"') {
            value = parseString();
        } else if (Character.isDigit(peek()) || peek() == '-') {
            value = parseNumber();
        } else if (peek() == '{') {
            value = parseObject();
        }

        return Map.entry(keyBuilder.toString(), value);
    }

    private JsonString parseString() {
        expect('"');
  
        StringBuilder builder = new StringBuilder();
        while (peek() != '"') {
            builder.append(next());
        }
        next();

        return new JsonString(builder.toString());
    }

    private JsonNumber parseNumber() {
        StringBuilder builder = new StringBuilder();

        while (pos < json.length()) {
            char c = peek();

            if (Character.isDigit(c) || c == '-' || c == '.') {
                builder.append(next());
            } else {
                break;
            }
        }

        String string = builder.toString();

        try {
            Number number = string.contains(".") ? Double.parseDouble(string) : Long.parseLong(string);
            return new JsonNumber(number);
        } catch (NumberFormatException ex) {
            throw new RuntimeException("Invalid number: " + string);
        }
    }

    private char peek() {
        if (pos >= json.length())
            return 0;
        return json.charAt(pos);
    }

    private char next() {
        if (pos >= json.length()) {
            throw new RuntimeException("Illegal JSON");
        }
        return json.charAt(pos++);
    }

    private void expect(char expected) {
        char actual = json.charAt(pos);

        if (expected != actual) {
            throw new RuntimeException("Expected '" + expected + "' but found '" + actual + "' at position " + pos);
        }
        pos++;
    }

    private void skipWhitespace() {
        while (pos < json.length() && Character.isWhitespace(peek())) {
            pos++;
        }
    }
}
