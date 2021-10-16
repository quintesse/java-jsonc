package org.codejive.jsonc.parser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

public class YylexTest {

    @Test
    public void testString() throws Exception {
        testLexOk("\"\\/\"", Yytoken.value("/"));
    }

    @Test
    public void testEscapes() throws Exception {
        testLexOk("\"abc\\/\\r\\b\\n\\t\\f\\\\\"", Yytoken.value("abc/\r\b\n\t\f\\"));
    }

    @Test
    public void testEscapeInvalid() throws Exception {
        testLexError("\"abc\\x\"", JsonParseException.ERROR_UNEXPECTED_CHAR, 'x', 4L);
    }

    @Test
    public void testIntegerZero() throws Exception {
        testLexOk("0", Yytoken.value(0L));
    }

    @Test
    public void testIntegerPositive() throws Exception {
        testLexOk("42", Yytoken.value(42L));
    }

    @Test
    public void testIntegerNegative() throws Exception {
        testLexOk("-123456789", Yytoken.value(-123456789L));
    }

    @Test
    public void testBrackets() throws Exception {
        testLexOk("[]", Yytoken.TYPE_LEFT_SQUARE, Yytoken.TYPE_RIGHT_SQUARE);
    }

    @Test
    public void testBraces() throws Exception {
        testLexOk("{}", Yytoken.TYPE_LEFT_BRACE, Yytoken.TYPE_RIGHT_BRACE);
    }

    @Test
    public void testWhitespace() throws Exception {
        testLexOk("\t \f\n\r\n}", Yytoken.TYPE_RIGHT_BRACE);
    }

    @Test
    public void testUnexpectedBackspace() throws Exception {
        testLexError("\b{", JsonParseException.ERROR_UNEXPECTED_CHAR, '\b', 0L);
    }

    @Test
    public void testInvalidValue() throws Exception {
        testLexError("{a : b}", JsonParseException.ERROR_UNEXPECTED_CHAR, 'a', 1L);
    }

    private void testLexOk(String input, Yytoken... expectedTokens) throws Exception {
        System.out.println("Lexing: " + input);
        StringReader in = new StringReader(input);
        Yylex lexer = new Yylex(in);
        for (Object expectedToken : expectedTokens) {
            Yytoken token = lexer.yylex();
            assertThat(token.getClass(), equalTo(expectedToken.getClass()));
            if (expectedToken instanceof Yytoken.YyValueToken) {
                assertThat(token.value, equalTo(((Yytoken.YyValueToken) expectedToken).value));
            }
        }
    }

    private void testLexError(
            String input, int expectedErrorType, Object unexpectedObject, long expectedPosition) {
        System.out.println("Lexing: " + input);
        StringReader in = new StringReader(input);
        Yylex lexer = new Yylex(in);
        JsonParseException err = null;
        try {
            while (!lexer.yyatEOF()) {
                lexer.yylex();
            }
        } catch (JsonParseException e) {
            err = e;
            System.out.println("expected error:" + err);
            assertThat(e.getErrorType(), equalTo(expectedErrorType));
            assertThat(e.getMessage(), containsString(unexpectedObject.toString()));
            assertThat(e.getPosition(), equalTo(expectedPosition));
        } catch (IOException ie) {
            fail();
        }
        assertTrue(err != null);
    }
}
