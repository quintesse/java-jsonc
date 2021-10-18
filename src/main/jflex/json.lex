package org.codejive.jsonc.parser;

%%

%{
private StringBuilder string = new StringBuilder();
private StringBuilder rawString = new StringBuilder();
private boolean hasEscapes = false;

long getPosition(){
    return yychar;
}

private void appendEscape(char escape) {
    if (!hasEscapes) {
        rawString.append(string);
        hasEscapes = true;
    }
    string.append(escape);
    rawString.append(yytext());
}

private char decodeUnicodeChar() throws JsonParseException {
    try {
        int hex = Integer.parseInt(yytext().substring(2), 16);
        return (char)hex;
    } catch (Exception e){
        throw JsonParseException.error(yychar, e);
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

integer = -?{digits}
fraction = \.{digits}
exponent = [eE][-+]?{digits}

real = {integer}{fraction}?{exponent}?

boolean = "true" | "false"

null = "null"

white_space = [ \t\r\n\f]
UNESCAPED_CH = [^\"\\]

%%

<YYINITIAL> {
    \"                  { string.setLength(0); rawString.setLength(0); hasEscapes = false; yybegin(STRING); }
    {integer}           { return Yytoken.integer(yytext()); }
    {real}              { return Yytoken.real(yytext()); }
    {boolean}           { return Yytoken.bool(yytext()); }
    {null}              { return Yytoken.nil(); }
    "{"                 { return Yytoken.TYPE_LEFT_BRACE; }
    "}"                 { return Yytoken.TYPE_RIGHT_BRACE; }
    "["                 { return Yytoken.TYPE_LEFT_SQUARE; }
    "]"                 { return Yytoken.TYPE_RIGHT_SQUARE; }
    ","                 { return Yytoken.TYPE_ITEM_SEPARATOR; }
    ":"                 { return Yytoken.TYPE_PAIR_SEPARATOR; }
    {white_space}+      {}
    .                   { throw JsonParseException.unexpectedChar(yychar, yycharat(0)); }
}

<STRING> {
    \"                  { yybegin(YYINITIAL); return Yytoken.string(string.toString(), hasEscapes ? rawString.toString() : string.toString()); }
    {UNESCAPED_CH}+     { string.append(yytext()); if (hasEscapes) rawString.append(yytext()); }
    \\\"                { appendEscape('"'); }
    \\\\                { appendEscape('\\'); }
    \\\/                { appendEscape('/'); }
    \\b                 { appendEscape('\b'); }
    \\f                 { appendEscape('\f'); }
    \\n                 { appendEscape('\n'); }
    \\r                 { appendEscape('\r'); }
    \\t                 { appendEscape('\t'); }
    \\u{hex_digit}{4}   { appendEscape(decodeUnicodeChar()); }
    \\.                 { throw JsonParseException.unexpectedChar(yychar, yycharat(1)); }
}
