package org.json.simple.parser;

%%

%{
private StringBuilder sb = new StringBuilder();

long getPosition(){
    return yychar;
}

private char unicodeChar() throws ParseException {
    try {
        int hex = Integer.parseInt(yytext().substring(2), 16);
        return (char)hex;
    } catch(Exception e){
        throw new ParseException(yychar, ParseException.ERROR_UNEXPECTED_EXCEPTION, e);
    }
}
%}

%unicode
%state STRING

%yylexthrow ParseException
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
    {num_int}           { return new Yytoken(Yytoken.TYPE_VALUE, Long.valueOf(yytext())); }
    {num_float}         { return new Yytoken(Yytoken.TYPE_VALUE, Double.valueOf(yytext())); }
    {boolean}           { return new Yytoken(Yytoken.TYPE_VALUE, Boolean.valueOf(yytext())); }
    {null}              { return new Yytoken(Yytoken.TYPE_VALUE, null); }
    "{"                 { return new Yytoken(Yytoken.TYPE_LEFT_BRACE,null); }
    "}"                 { return new Yytoken(Yytoken.TYPE_RIGHT_BRACE,null); }
    "["                 { return new Yytoken(Yytoken.TYPE_LEFT_SQUARE,null); }
    "]"                 { return new Yytoken(Yytoken.TYPE_RIGHT_SQUARE,null); }
    ","                 { return new Yytoken(Yytoken.TYPE_COMMA,null); }
    ":"                 { return new Yytoken(Yytoken.TYPE_COLON,null); }
    {white_space}+      {}
    .                   { throw new ParseException(yychar, ParseException.ERROR_UNEXPECTED_CHAR, new Character(yycharat(0))); }
}

<STRING> {
    \"                  { yybegin(YYINITIAL); return new Yytoken(Yytoken.TYPE_VALUE, sb.toString()); }
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
    \\                  { sb.append('\\'); }
}
