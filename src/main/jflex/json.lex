package org.codejive.jsonc.parser;

%%

%{
private StringBuilder sb = new StringBuilder();

long getPosition(){
    return yychar;
}

private char unicodeChar() throws JsonParseException {
    try {
        int hex = Integer.parseInt(yytext().substring(2), 16);
        return (char)hex;
    } catch(Exception e){
        throw new JsonParseException(yychar, JsonParseException.ERROR_UNEXPECTED_EXCEPTION, e);
    }
}
%}

%unicode
%state STRING

%yylexthrow JsonParseException
%char

digit = [0-9]
digits = {digit}+
non_zero_digit = [1-9]
hex_digit = [0-9a-fA-F]

integer = {digits}
fraction = \.{digits}
exponent = [eE][-+]?{digits}

num_int = -?{integer}
num_float = {num_int}{fraction}?{exponent}?

boolean = "true" | "false"

null = "null"

white_space = [ \t\r\n\f]
UNESCAPED_CH = [^\"\\]

%%

<YYINITIAL> {
    \"                  { sb.setLength(0); yybegin(STRING); }
    {num_int}           { return Yytoken.value(Long.valueOf(yytext())); }
    {num_float}         { return Yytoken.value(Double.valueOf(yytext())); }
    {boolean}           { return Yytoken.value(Boolean.valueOf(yytext())); }
    {null}              { return Yytoken.value(null); }
    "{"                 { return Yytoken.TYPE_LEFT_BRACE; }
    "}"                 { return Yytoken.TYPE_RIGHT_BRACE; }
    "["                 { return Yytoken.TYPE_LEFT_SQUARE; }
    "]"                 { return Yytoken.TYPE_RIGHT_SQUARE; }
    ","                 { return Yytoken.TYPE_COMMA; }
    ":"                 { return Yytoken.TYPE_COLON; }
    {white_space}+      {}
    .                   { throw new JsonParseException(yychar, JsonParseException.ERROR_UNEXPECTED_CHAR, new Character(yycharat(0))); }
}

<STRING> {
    \"                  { yybegin(YYINITIAL); return Yytoken.value(sb.toString()); }
    {UNESCAPED_CH}+     { sb.append(yytext()); }
    \\\"                { sb.append('"'); }
    \\\\                { sb.append('\\'); }
    \\\/                { sb.append('/'); }
    \\b                 { sb.append('\b'); }
    \\f                 { sb.append('\f'); }
    \\n                 { sb.append('\n'); }
    \\r                 { sb.append('\r'); }
    \\t                 { sb.append('\t'); }
    \\u{hex_digit}{4}   { sb.append(unicodeChar()); }
    \\.                 { throw new JsonParseException(yychar, JsonParseException.ERROR_UNEXPECTED_CHAR, new Character(yycharat(1))); }
}
