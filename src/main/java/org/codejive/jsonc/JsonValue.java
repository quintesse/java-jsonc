package org.codejive.jsonc;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
// import java.util.List;
import java.util.Map;
import org.codejive.jsonc.parser.JsonParseException;
import org.codejive.jsonc.parser.JsonParser;

/** @author FangYidong<fangyidong@yahoo.com.cn> */
public class JsonValue {
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
     *     use {@link JsonValue#parseWithException(Reader)} instead
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
     *     use {@link JsonValue#parseWithException(String)} instead
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
     * <p>If this object is a Map or a List, and it's also a JSONStreamAware or a JSONAware,
     * JSONStreamAware or JSONAware will be considered firstly.
     *
     * <p>DO NOT call this method from writeJSONString(Writer) of a class that implements both
     * JSONStreamAware and (Map or List) with "this" as the first parameter, use
     * JSONObject.writeJSONString(Map, Writer) or JSONArray.writeJSONString(List, Writer) instead.
     *
     * @see JsonObject#writeJSONString(Map, Writer)
     * @see JsonArray#writeJSONString(Collection, Writer)
     * @param value
     * @param out
     */
    public static void writeJSONString(Object value, Writer out) throws IOException {
        if (value == null) {
            out.write("null");
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

        if ((value instanceof JsonStreamAware)) {
            ((JsonStreamAware) value).writeJSONString(out);
            return;
        }

        if ((value instanceof JsonAware)) {
            out.write(((JsonAware) value).toJSONString());
            return;
        }

        if (value instanceof Map) {
            JsonObject.writeJSONString((Map) value, out);
            return;
        }

        if (value instanceof Collection) {
            JsonArray.writeJSONString((Collection) value, out);
            return;
        }

        if (value instanceof byte[]) {
            JsonArray.writeJSONString((byte[]) value, out);
            return;
        }

        if (value instanceof short[]) {
            JsonArray.writeJSONString((short[]) value, out);
            return;
        }

        if (value instanceof int[]) {
            JsonArray.writeJSONString((int[]) value, out);
            return;
        }

        if (value instanceof long[]) {
            JsonArray.writeJSONString((long[]) value, out);
            return;
        }

        if (value instanceof float[]) {
            JsonArray.writeJSONString((float[]) value, out);
            return;
        }

        if (value instanceof double[]) {
            JsonArray.writeJSONString((double[]) value, out);
            return;
        }

        if (value instanceof boolean[]) {
            JsonArray.writeJSONString((boolean[]) value, out);
            return;
        }

        if (value instanceof char[]) {
            JsonArray.writeJSONString((char[]) value, out);
            return;
        }

        if (value instanceof Object[]) {
            JsonArray.writeJSONString((Object[]) value, out);
            return;
        }

        out.write(value.toString());
    }

    /**
     * Convert an object to JSON text.
     *
     * <p>If this object is a Map or a List, and it's also a JSONAware, JSONAware will be considered
     * firstly.
     *
     * <p>DO NOT call this method from toJSONString() of a class that implements both JSONAware and
     * Map or List with "this" as the parameter, use JSONObject.toJSONString(Map) or
     * JSONArray.toJSONString(List) instead.
     *
     * @see JsonObject#toJSONString(Map)
     * @see JsonArray#toJSONString(List)
     * @param value
     * @return JSON text, or "null" if value is null or it's an NaN or an INF number.
     */
    public static String toJSONString(Object value) {
        final StringWriter writer = new StringWriter();

        try {
            writeJSONString(value, writer);
            return writer.toString();
        } catch (IOException e) {
            // This should never happen for a StringWriter
            throw new RuntimeException(e);
        }
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
}
