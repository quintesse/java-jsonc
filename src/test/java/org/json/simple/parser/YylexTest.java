package org.json.simple.parser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

public class YylexTest {

    @Test
    public void testString() throws Exception {
        testLexOk("\"\\/\"", Yytoken.TYPE_VALUE, "/");
    }

    @Test
    public void testStringEscapes() throws Exception {
        testLexOk("\"abc\\/\\r\\b\\n\\t\\f\\\\\"", Yytoken.TYPE_VALUE, "abc/\r\b\n\t\f\\");
    }

    @Test
    public void testIntegerZero() throws Exception {
        testLexOk("0", Yytoken.TYPE_VALUE, 0L);
    }

    @Test
    public void testIntegerPositive() throws Exception {
        testLexOk("42", Yytoken.TYPE_VALUE, 42L);
    }

    @Test
    public void testIntegerNegative() throws Exception {
        testLexOk("-123456789", Yytoken.TYPE_VALUE, -123456789L);
    }

    @Test
    public void testIntegerInvalid() throws Exception {
        testLexError("01", ParseException.ERROR_UNEXPECTED_CHAR, '\b', 0L);
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
        testLexError("\b{", ParseException.ERROR_UNEXPECTED_CHAR, '\b', 0L);
    }

    @Test
    public void testInvalidValue() throws Exception {
        testLexError("{a : b}", ParseException.ERROR_UNEXPECTED_CHAR, 'a', 1L);
    }

    private void testLexOk(String input, Object... expectedTokens) throws Exception {
        System.out.println("Lexing: " + input);
        StringReader in = new StringReader(input);
        Yylex lexer = new Yylex(in);
        for (int i = 0; i < expectedTokens.length; i++) {
            Object expectedToken = expectedTokens[i];
            Yytoken token = lexer.yylex();
            assertThat(token.type, equalTo(expectedToken));
            if (expectedToken.equals(Yytoken.TYPE_VALUE)) {
                Object expectedValue = expectedTokens[++i];
                assertThat(token.value, equalTo(expectedValue));
            }
        }
    }

    private void testLexError(
            String input, int expectedErrorType, Object unexpectedObject, long expectedPosition)
            throws Exception {
        System.out.println("Lexing: " + input);
        StringReader in = new StringReader(input);
        Yylex lexer = new Yylex(in);
        ParseException err = null;
        try {
            while (!lexer.yyatEOF()) {
                lexer.yylex();
            }
        } catch (ParseException e) {
            err = e;
            System.out.println("expected error:" + err);
            assertThat(e.getErrorType(), equalTo(expectedErrorType));
            assertThat(e.getUnexpectedObject(), equalTo(unexpectedObject));
            assertThat(e.getPosition(), equalTo(expectedPosition));
        } catch (IOException ie) {
            fail();
        }
        assertTrue(err != null);
    }
}
