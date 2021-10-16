package org.codejive.jsonc.parser;

/**
 * ParseException explains why and where the error occurs in source JSON text.
 *
 * @author FangYidong<fangyidong@yahoo.com.cn>
 */
public class JsonParseException extends Exception {
    public static final int ERROR_UNEXPECTED_CHAR = 0;
    public static final int ERROR_UNEXPECTED_TOKEN = 1;
    public static final int ERROR_UNEXPECTED_EXCEPTION = 2;

    private int errorType;
    private long position;

    public static JsonParseException unexpectedChar(long position, char unexpected) {
        return new JsonParseException(
                position,
                ERROR_UNEXPECTED_CHAR,
                "Unexpected character '" + unexpected + "' @" + position,
                null);
    }

    public static JsonParseException unexpectedToken(long position, Yytoken unexpected) {
        return new JsonParseException(
                position,
                ERROR_UNEXPECTED_TOKEN,
                "Unexpected token '" + unexpected + "' @" + position,
                null);
    }

    public static JsonParseException error(long position, Exception cause) {
        return new JsonParseException(
                position, ERROR_UNEXPECTED_EXCEPTION, "Error @" + position, cause);
    }

    public JsonParseException(long position, int errorType, String message, Exception cause) {
        super(message, cause);
        this.position = position;
        this.errorType = errorType;
    }

    public int getErrorType() {
        return errorType;
    }

    /**
     * @see JsonParser#getPosition()
     * @return The character position (starting with 0) of the input where the error occurs.
     */
    public long getPosition() {
        return position;
    }
}
