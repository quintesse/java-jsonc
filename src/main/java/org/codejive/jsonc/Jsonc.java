package org.codejive.jsonc;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
// import java.util.List;
import java.util.Iterator;
import java.util.Map;
import org.codejive.jsonc.parser.JsonParseException;
import org.codejive.jsonc.parser.JsonParser;

public class Jsonc {

    public static JsonPrimitive string(String s) {
        return new JsonPrimitive(JsonPrimitive.Type.STRING, s);
    }

    public static JsonPrimitive number(int n) {
        return new JsonPrimitive(JsonPrimitive.Type.INTEGER, Integer.toString(n));
    }

    public static JsonPrimitive number(long l) {
        return new JsonPrimitive(JsonPrimitive.Type.INTEGER, Long.toString(l));
    }

    public static JsonPrimitive number(BigInteger bi) {
        return new JsonPrimitive(JsonPrimitive.Type.INTEGER, bi.toString());
    }

    public static JsonPrimitive number(float f) {
        return new JsonPrimitive(JsonPrimitive.Type.REAL, Float.toString(f));
    }

    public static JsonPrimitive number(double d) {
        return new JsonPrimitive(JsonPrimitive.Type.REAL, Double.toString(d));
    }

    public static JsonPrimitive number(BigDecimal bd) {
        return new JsonPrimitive(JsonPrimitive.Type.REAL, bd.toString());
    }

    public static JsonPrimitive bool(boolean b) {
        return new JsonPrimitive(JsonPrimitive.Type.BOOLEAN, Boolean.toString(b));
    }

    public static JsonPrimitive nil() {
        return new JsonPrimitive(JsonPrimitive.Type.NULL, "null");
    }

    /**
     * Parse JSON text into java object from the input source. Please use parseWithException() if
     * you don't want to ignore the exception.
     *
     * @see JsonParser#parse(Reader)
     * @see #parseWithException(Reader)
     * @param in
     * @return Instance of the following: org.json.simple.JSONObject, org.json.simple.JSONArray,
     *     java.lang.String, java.lang.Number, java.lang.Boolean, null
     * @deprecated this method may throw an {@code Error} instead of returning {@code null}; please
     *     use {@link Jsonc#parseWithException(Reader)} instead
     */
    public static Object parse(Reader in) {
        try {
            JsonParser parser = new JsonParser();
            return parser.parse(in);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Parse JSON text into java object from the given string. Please use parseWithException() if
     * you don't want to ignore the exception.
     *
     * @see JsonParser#parse(Reader)
     * @see #parseWithException(Reader)
     * @param s
     * @return Instance of the following: org.json.simple.JSONObject, org.json.simple.JSONArray,
     *     java.lang.String, java.lang.Number, java.lang.Boolean, null
     * @deprecated this method may throw an {@code Error} instead of returning {@code null}; please
     *     use {@link Jsonc#parseWithException(String)} instead
     */
    public static Object parse(String s) {
        StringReader in = new StringReader(s);
        return parse(in);
    }

    /**
     * Parse JSON text into java object from the input source.
     *
     * @see JsonParser
     * @param in
     * @return Instance of the following: org.json.simple.JSONObject, org.json.simple.JSONArray,
     *     java.lang.String, java.lang.Number, java.lang.Boolean, null
     * @throws IOException
     * @throws JsonParseException
     */
    public static Object parseWithException(Reader in) throws IOException, JsonParseException {
        JsonParser parser = new JsonParser();
        return parser.parse(in);
    }

    public static Object parseWithException(String s) throws JsonParseException {
        JsonParser parser = new JsonParser();
        return parser.parse(s);
    }

    /**
     * Encode an object into JSON text and write it to out.
     *
     * @param value
     * @param out
     */
    public static void writeJSONString(Object value, Writer out, boolean raw) throws IOException {
        if (value == null) {
            out.write("null");
            return;
        }

        if (value instanceof JsonPrimitive) {
            writeJSONString((JsonPrimitive) value, out, raw);
            return;
        }

        if (value instanceof String) {
            out.write('\"');
            out.write(escape((String) value));
            out.write('\"');
            return;
        }

        if (value instanceof Double) {
            if (((Double) value).isInfinite() || ((Double) value).isNaN()) out.write("null");
            else out.write(value.toString());
            return;
        }

        if (value instanceof Float) {
            if (((Float) value).isInfinite() || ((Float) value).isNaN()) out.write("null");
            else out.write(value.toString());
            return;
        }

        if (value instanceof Number) {
            out.write(value.toString());
            return;
        }

        if (value instanceof Boolean) {
            out.write(value.toString());
            return;
        }

        if (value instanceof Map) {
            writeJSONString((Map) value, out, raw);
            return;
        }

        if (value instanceof Collection) {
            writeJSONString((Collection) value, out, raw);
            return;
        }

        out.write(value.toString());
    }

    /** Convert an object to JSON text. */
    public static String toJSONString(Object value, boolean raw) {
        final StringWriter writer = new StringWriter();

        try {
            writeJSONString(value, writer, raw);
            return writer.toString();
        } catch (IOException e) {
            // This should never happen for a StringWriter
            throw new RuntimeException(e);
        }
    }

    /** Convert an object to JSON text. */
    public static String toJSONString(Object value) {
        return toJSONString(value, true);
    }

    /** Convert an object to JSON text. */
    public static String toString(Object value) {
        return toJSONString(value, false);
    }

    /**
     * Escape quotes, \, /, \r, \n, \b, \f, \t and other control characters (U+0000 through U+001F).
     *
     * @param s
     * @return
     */
    public static String escape(String s) {
        if (s == null) return null;
        StringBuffer sb = new StringBuffer();
        escape(s, sb);
        return sb.toString();
    }

    /**
     * @param s - Must not be null.
     * @param sb
     */
    static void escape(String s, StringBuffer sb) {
        final int len = s.length();
        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                default:
                    // Reference: http://www.unicode.org/versions/Unicode5.1.0/
                    if ((ch >= '\u0000' && ch <= '\u001F')
                            || (ch >= '\u007F' && ch <= '\u009F')
                            || (ch >= '\u2000' && ch <= '\u20FF')) {
                        String ss = Integer.toHexString(ch);
                        sb.append("\\u");
                        for (int k = 0; k < 4 - ss.length(); k++) {
                            sb.append('0');
                        }
                        sb.append(ss.toUpperCase());
                    } else {
                        sb.append(ch);
                    }
            }
        } // for
    }

    /**
     * Encode a list into JSON text and write it to out. If this list is also a JSONStreamAware or a
     * JSONAware, JSONStreamAware and JSONAware specific behaviours will be ignored at this top
     * level.
     *
     * @see Jsonc#writeJSONString(Object, Writer, boolean)
     * @param collection
     * @param out
     * @param raw
     */
    public static void writeJSONString(Collection<Object> collection, Writer out, boolean raw)
            throws IOException {
        if (collection == null) {
            out.write("null");
            return;
        }

        boolean first = true;
        Iterator<Object> iter = collection.iterator();

        out.write('[');
        while (iter.hasNext()) {
            if (first) first = false;
            else out.write(',');

            Object value = iter.next();
            if (value == null) {
                out.write("null");
                continue;
            }

            writeJSONString(value, out, raw);
        }
        out.write(']');
    }

    /**
     * Convert a list to JSON text. The result is a JSON array. If this list is also a JSONAware,
     * JSONAware specific behaviours will be omitted at this top level.
     *
     * @see Jsonc#toJSONString(Object)
     * @param collection
     * @return JSON text, or "null" if list is null.
     */
    public static String toJSONString(Collection<Object> collection, boolean raw) {
        final StringWriter writer = new StringWriter();

        try {
            writeJSONString(collection, writer, raw);
            return writer.toString();
        } catch (IOException e) {
            // This should never happen for a StringWriter
            throw new RuntimeException(e);
        }
    }

    /**
     * Encode a map into JSON text and write it to out. If this map is also a JSONAware or
     * JSONStreamAware, JSONAware or JSONStreamAware specific behaviours will be ignored at this top
     * level.
     *
     * @param map
     * @param out
     */
    public static void writeJSONString(Map<String, Object> map, Writer out, boolean raw)
            throws IOException {
        if (map == null) {
            out.write("null");
            return;
        }

        boolean first = true;
        Iterator<Map.Entry<String, Object>> iter = map.entrySet().iterator();

        out.write('{');
        while (iter.hasNext()) {
            if (first) first = false;
            else out.write(',');
            Map.Entry<String, Object> entry = iter.next();
            out.write('\"');
            out.write(escape(String.valueOf(entry.getKey())));
            out.write('\"');
            out.write(':');
            writeJSONString(entry.getValue(), out, raw);
        }
        out.write('}');
    }

    /**
     * Convert a map to JSON text. The result is a JSON object. If this map is also a JSONAware,
     * JSONAware specific behaviours will be omitted at this top level.
     *
     * @param map
     * @return JSON text, or "null" if map is null.
     */
    public static String toJSONString(Map<String, Object> map, boolean raw) {
        final StringWriter writer = new StringWriter();

        try {
            writeJSONString(map, writer, raw);
            return writer.toString();
        } catch (IOException e) {
            // This should never happen with a StringWriter
            throw new RuntimeException(e);
        }
    }

    public static void writeJSONString(JsonPrimitive p, Writer out, boolean raw)
            throws IOException {
        String val = raw ? p.rawValue : p.value;
        if (p.isString()) {
            out.write("\"");
            out.write(val);
            out.write("\"");
        } else {
            out.write(val);
        }
    }

    public static String toJSONString(JsonPrimitive p, boolean raw) {
        final StringWriter writer = new StringWriter();

        try {
            writeJSONString(p, writer, raw);
            return writer.toString();
        } catch (IOException e) {
            // This should never happen with a StringWriter
            throw new RuntimeException(e);
        }
    }
}
