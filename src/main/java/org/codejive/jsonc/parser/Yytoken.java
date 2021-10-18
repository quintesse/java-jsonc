package org.codejive.jsonc.parser;

import java.util.Objects;

/** @author FangYidong<fangyidong@yahoo.com.cn> */
public class Yytoken {
    public static final Yytoken TYPE_LEFT_BRACE = new Yytoken("{");
    public static final Yytoken TYPE_RIGHT_BRACE = new Yytoken("}");
    public static final Yytoken TYPE_LEFT_SQUARE = new Yytoken("[");
    public static final Yytoken TYPE_RIGHT_SQUARE = new Yytoken("]");
    public static final Yytoken TYPE_ITEM_SEPARATOR = new Yytoken(",");
    public static final Yytoken TYPE_PAIR_SEPARATOR = new Yytoken(":");
    ;
    public static final Yytoken TYPE_EOF = new Yytoken("<EOF>");
    ; // end of file

    // JSON primitive value: string,number,boolean,null
    static class YyPrimitiveToken extends Yytoken {
        private YyPrimitiveToken(Object value) {
            super(value);
        }
    }

    public static YyPrimitiveToken primitive(Object value) {
        return new YyPrimitiveToken(value);
    }

    public final Object value;

    private Yytoken(Object value) {
        this.value = value;
    }

    public String toString() {
        return Objects.toString(value);
    }
}
