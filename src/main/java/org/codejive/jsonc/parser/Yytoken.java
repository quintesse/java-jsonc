package org.codejive.jsonc.parser;

import org.codejive.jsonc.JsonPrimitive;

public class Yytoken {
    public final String value;
    public final String rawValue;

    public static final Yytoken TYPE_LEFT_BRACE = new Yytoken("{");
    public static final Yytoken TYPE_RIGHT_BRACE = new Yytoken("}");
    public static final Yytoken TYPE_LEFT_SQUARE = new Yytoken("[");
    public static final Yytoken TYPE_RIGHT_SQUARE = new Yytoken("]");
    public static final Yytoken TYPE_ITEM_SEPARATOR = new Yytoken(",");
    public static final Yytoken TYPE_PAIR_SEPARATOR = new Yytoken(":");
    public static final Yytoken TYPE_EOF = new Yytoken("<EOF>");

    private Yytoken(String value) {
        this(value, value);
    }

    private Yytoken(String value, String rawValue) {
        this.value = value;
        this.rawValue = rawValue;
    }

    abstract static class YyPrimitiveToken extends Yytoken {
        public final JsonPrimitive.Type type;

        private YyPrimitiveToken(JsonPrimitive.Type type, String value, String rawValue) {
            super(value, rawValue);
            this.type = type;
        }
    }

    static class YyStringToken extends YyPrimitiveToken {
        private YyStringToken(String value, String rawValue) {
            super(JsonPrimitive.Type.STRING, value, rawValue);
        }
    }

    public static YyStringToken string(String value, String rawValue) {
        return new YyStringToken(value, rawValue);
    }

    static class YyIntegerToken extends YyPrimitiveToken {
        private YyIntegerToken(String value) {
            super(JsonPrimitive.Type.INTEGER, value, value);
        }
    }

    public static YyIntegerToken integer(String value) {
        return new YyIntegerToken(value);
    }

    static class YyRealToken extends YyPrimitiveToken {
        private YyRealToken(String value) {
            super(JsonPrimitive.Type.REAL, value, value);
        }
    }

    public static YyRealToken real(String value) {
        return new YyRealToken(value);
    }

    static class YyBooleanToken extends YyPrimitiveToken {
        private YyBooleanToken(String value) {
            super(JsonPrimitive.Type.BOOLEAN, value, value);
        }
    }

    public static YyBooleanToken bool(String value) {
        return new YyBooleanToken(value);
    }

    static class YyNullToken extends YyPrimitiveToken {
        private YyNullToken() {
            super(JsonPrimitive.Type.NULL, "null", "null");
        }
    }

    public static YyNullToken nil() {
        return new YyNullToken();
    }

    public String toString() {
        return value;
    }
}
