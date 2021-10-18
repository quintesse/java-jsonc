package org.codejive.jsonc;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

public class JsonPrimitive implements JsonElement {
    public enum Type {
        STRING,
        INTEGER,
        REAL,
        BOOLEAN,
        NULL
    };

    final Type type;
    final String value;
    final String rawValue;

    public JsonPrimitive(Type type, String value) {
        this(type, value, value);
    }

    public JsonPrimitive(Type type, String value, String rawValue) {
        this.type = type;
        this.value = value;
        this.rawValue = rawValue;
    }

    boolean isString() {
        return type == Type.STRING;
    }

    boolean isNumber() {
        return isInteger() || isReal();
    }

    boolean isInteger() {
        return type == Type.INTEGER;
    }

    boolean isReal() {
        return type == Type.REAL;
    }

    boolean isNull() {
        return type == Type.NULL;
    }

    boolean isBoolean() {
        return type == Type.BOOLEAN;
    }

    public Long toLong() {
        return Long.parseLong(value);
    }

    public double toDouble() {
        return Double.parseDouble(value);
    }

    public boolean toBoolean() {
        return Boolean.parseBoolean(value);
    }

    public Object value() {
        if (isString()) {
            return toString();
        } else if (isInteger()) {
            return toLong();
        } else if (isReal()) {
            return toDouble();
        } else if (isBoolean()) {
            return toBoolean();
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonPrimitive that = (JsonPrimitive) o;
        return type == that.type && rawValue.equals(that.rawValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, rawValue);
    }

    public void writeJSONString(Writer out) throws IOException {
        Jsonc.writeJSONString(this, out, true);
    }

    @Override
    public String toJSONString() {
        return Jsonc.toJSONString(this, true);
    }

    @Override
    public String toString() {
        return value;
    }
}
