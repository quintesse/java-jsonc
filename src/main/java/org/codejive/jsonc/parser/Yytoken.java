package org.codejive.jsonc.parser;

import java.util.Objects;

/** @author FangYidong<fangyidong@yahoo.com.cn> */
public class Yytoken {
    public static final Yytoken TYPE_LEFT_BRACE = new Yytoken("{");
    public static final Yytoken TYPE_RIGHT_BRACE = new Yytoken("}");
    public static final Yytoken TYPE_LEFT_SQUARE = new Yytoken("[");
    public static final Yytoken TYPE_RIGHT_SQUARE = new Yytoken("]");
    public static final Yytoken TYPE_COMMA = new Yytoken(",");
    public static final Yytoken TYPE_COLON = new Yytoken(":");
    ;
    public static final Yytoken TYPE_EOF = new Yytoken("<EOF>");
    ; // end of file

    // JSON primitive value: string,number,boolean,null
    static class YyValueToken extends Yytoken {
        private YyValueToken(Object value) {
            super(value);
        }
    }

    public static YyValueToken value(Object value) {
        return new YyValueToken(value);
    }

    public final Object value;

    private Yytoken(Object value) {
        this.value = value;
    }

    public String toString() {
        return Objects.toString(value);
    }
}
