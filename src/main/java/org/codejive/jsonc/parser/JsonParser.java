package org.codejive.jsonc.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.codejive.jsonc.JsonArray;
import org.codejive.jsonc.JsonObject;
import org.codejive.jsonc.JsonPrimitive;

/** Parser for JSON text. Please note that JSONParser is NOT thread-safe. */
public class JsonParser {
    public enum Status {
        INIT,
        IN_FINISHED_VALUE,
        IN_OBJECT,
        IN_ARRAY,
        PASSED_PAIR_KEY,
        IN_PAIR_VALUE,
        END,
        IN_ERROR
    }

    private final JsonParserConfig config;

    private final Yylex lexer = new Yylex(null);

    private Yytoken token = null;
    private LinkedList<Status> handlerStatusStack;
    LinkedList<Status> statusStack;
    private Status status = Status.INIT;

    public JsonParser() {
        this(JsonParserConfig.defaults());
    }

    public JsonParser(JsonParserConfig config) {
        this.config = config;
    }

    private Status peekStatus(LinkedList<Status> statusStack) {
        if (statusStack.isEmpty()) return Status.IN_ERROR;
        return statusStack.getFirst();
    }

    /** Reset the parser to the initial state without resetting the underlying reader. */
    public void reset() {
        token = null;
        status = Status.INIT;
        handlerStatusStack = null;
    }

    /**
     * Reset the parser to the initial state with a new character reader.
     *
     * @param in - The new character reader.
     * @throws IOException
     * @throws JsonParseException
     */
    public void reset(Reader in) {
        lexer.yyreset(in);
        reset();
    }

    /** @return The position of the beginning of the current token. */
    public long getPosition() {
        return lexer.getPosition();
    }

    public Object parse(String s) throws JsonParseException {
        return parse(s, new DefaultTypeFactory());
    }

    public Object parse(String s, TypeFactory typeFactory) throws JsonParseException {
        DefaultContentHandler dch = new DefaultContentHandler(typeFactory);
        parse(s, dch);
        return dch.getResult();
    }

    public Object parse(Reader in) throws IOException, JsonParseException {
        DefaultContentHandler dch = new DefaultContentHandler(new DefaultTypeFactory());
        parse(in, dch);
        return dch.getResult();
    }

    static class DefaultTypeFactory implements TypeFactory {
        @Override
        public JsonObject createObjectContainer() {
            return new JsonObject();
        }

        @Override
        public JsonArray createArrayContainer() {
            return new JsonArray();
        }

        @Override
        public Object createPrimitive(JsonPrimitive.Type type, String value, String rawValue) {
            return new JsonPrimitive(type, value, rawValue);
        }
    }

    static class DefaultContentHandler implements ContentHandler {
        private final TypeFactory typeFactory;

        private LinkedList<Object> valueStack;
        private Object result;

        public DefaultContentHandler(TypeFactory typeFactory) {
            this.typeFactory = typeFactory;
        }

        public Object getResult() {
            return result;
        }

        @Override
        public void startJSON() {
            valueStack = new LinkedList<>();
            result = null;
        }

        @Override
        public void endJSON() {
            result = valueStack.removeFirst();
        }

        @Override
        public boolean startObject(Status status) {
            Object obj = typeFactory.createObjectContainer();
            addElement(status, obj, false);
            return true;
        }

        @Override
        public boolean endObject(Status status) {
            if (valueStack.size() > 1) {
                valueStack.removeFirst();
            }
            return true;
        }

        @Override
        public boolean startObjectEntry(String key) {
            valueStack.addFirst(key);
            return true;
        }

        @Override
        public boolean endObjectEntry() {
            return true;
        }

        @Override
        public boolean startArray(Status status) {
            Object arr = typeFactory.createArrayContainer();
            addElement(status, arr, false);
            return true;
        }

        @Override
        public boolean endArray(Status status) {
            if (valueStack.size() > 1) {
                valueStack.removeFirst();
            }
            return true;
        }

        @Override
        public boolean primitive(
                Status status, JsonPrimitive.Type type, String value, String rawValue) {
            addElement(status, typeFactory.createPrimitive(type, value, rawValue), true);
            return true;
        }

        private void addElement(Status status, Object element, boolean isPrimitive) {
            switch (status) {
                case OBJECT:
                    String key = (String) valueStack.removeFirst();
                    Map<String, Object> parent = (Map<String, Object>) valueStack.getFirst();
                    parent.put(key, element);
                    break;
                case ARRAY:
                    List<Object> val = (List<Object>) valueStack.getFirst();
                    val.add(element);
                    break;
            }
            if (!isPrimitive || status == Status.TOPLEVEL) {
                valueStack.addFirst(element);
            }
        }
    }

    private void nextToken() throws JsonParseException, IOException {
        token = lexer.yylex();
        if (token == null) token = Yytoken.TYPE_EOF;
    }

    public void parse(String s, ContentHandler contentHandler) throws JsonParseException {
        parse(s, contentHandler, false);
    }

    public void parse(String s, ContentHandler contentHandler, boolean isResume)
            throws JsonParseException {
        StringReader in = new StringReader(s);
        try {
            parse(in, contentHandler, isResume);
        } catch (IOException ie) {
            /*
             * Actually it will never happen.
             */
            throw JsonParseException.error(-1, ie);
        }
    }

    public void parse(Reader in, ContentHandler contentHandler)
            throws IOException, JsonParseException {
        parse(in, contentHandler, false);
    }

    /**
     * Stream processing of JSON text.
     *
     * @see ContentHandler
     * @param in
     * @param contentHandler
     * @param isResume - Indicates if it continues previous parsing operation. If set to true,
     *     resume parsing the old stream, and parameter 'in' will be ignored. If this method is
     *     called for the first time in this instance, isResume will be ignored.
     * @throws IOException
     * @throws JsonParseException
     */
    public void parse(Reader in, ContentHandler contentHandler, boolean isResume)
            throws IOException, JsonParseException {
        if (!isResume) {
            reset(in);
            handlerStatusStack = new LinkedList<>();
        } else {
            if (handlerStatusStack == null) {
                isResume = false;
                reset(in);
                handlerStatusStack = new LinkedList<>();
            }
        }

        statusStack = handlerStatusStack;

        try {
            nextToken();
            do {
                switch (status) {
                    case INIT:
                        contentHandler.startJSON();
                        if (token instanceof Yytoken.YyPrimitiveToken) {
                            if (config.allowToplevelValues()) {
                                if (handlePrimitiveValue(contentHandler)) return;
                                status = Status.IN_FINISHED_VALUE;
                                statusStack.addFirst(status);
                            } else {
                                status = Status.IN_ERROR;
                                throw JsonParseException.unexpectedToken(getPosition(), token);
                            }
                        } else if (Yytoken.TYPE_LEFT_BRACE == token) {
                            handleObjectStart(contentHandler);
                        } else if (Yytoken.TYPE_LEFT_SQUARE == token) {
                            if (handleArrayStart(contentHandler)) return;
                        } else {
                            status = Status.IN_ERROR;
                        } // inner switch
                        break;

                    case IN_FINISHED_VALUE:
                        status = Status.IN_ERROR;
                        break;

                    case IN_OBJECT:
                        if (Yytoken.TYPE_ITEM_SEPARATOR == token) {
                            nextToken();
                            handleMissingObjectItem();
                            handleTrailingSeparator();
                        } else if (token instanceof Yytoken.YyPrimitiveToken) {
                            if (config.allowObjectPrimitiveKeys()
                                    || token instanceof Yytoken.YyStringToken) {
                                if (handleObjectKey(contentHandler)) return;
                            } else {
                                status = Status.IN_ERROR;
                            }
                        } else if (Yytoken.TYPE_RIGHT_BRACE == token) {
                            if (handleObjectEnd(contentHandler)) return;
                        } else {
                            status = Status.IN_ERROR;
                        } // inner switch
                        break;

                    case PASSED_PAIR_KEY:
                        nextToken();
                        if (token instanceof Yytoken.YyPrimitiveToken) {
                            if (handlePrimitiveValue(contentHandler)) return;
                            if (!contentHandler.endObjectEntry()) return;
                            statusStack.removeFirst();
                            status = peekStatus(statusStack);
                        } else if (Yytoken.TYPE_LEFT_SQUARE == token) {
                            statusStack.removeFirst();
                            statusStack.addFirst(Status.IN_PAIR_VALUE);
                            if (handleArrayStart(contentHandler)) return;
                        } else if (Yytoken.TYPE_LEFT_BRACE == token) {
                            statusStack.removeFirst();
                            statusStack.addFirst(Status.IN_PAIR_VALUE);
                            handleObjectStart(contentHandler);
                        } else {
                            status = Status.IN_ERROR;
                        }
                        break;

                    case IN_PAIR_VALUE:
                        /*
                         * Status.IN_PAIR_VALUE is just a marker to indicate the end of an object entry, it
                         * doesn't process any token, therefore delay consuming token until next round.
                         */
                        statusStack.removeFirst();
                        status = peekStatus(statusStack);
                        if (!contentHandler.endObjectEntry()) return;
                        break;

                    case IN_ARRAY:
                        if (Yytoken.TYPE_ITEM_SEPARATOR == token) {
                            nextToken();
                            if (handleMissingArrayValue(contentHandler)) return;
                            handleTrailingSeparator();
                        } else if (token instanceof Yytoken.YyPrimitiveToken) {
                            if (handlePrimitiveValue(contentHandler)) return;
                        } else if (Yytoken.TYPE_RIGHT_SQUARE == token) {
                            handleArrayEnd(contentHandler);
                        } else if (Yytoken.TYPE_LEFT_BRACE == token) {
                            handleObjectStart(contentHandler);
                        } else if (Yytoken.TYPE_LEFT_SQUARE == token) {
                            if (handleArrayStart(contentHandler)) return;
                        } else {
                            status = Status.IN_ERROR;
                        } // inner switch
                        break;

                    case END:
                        return;

                    case IN_ERROR:
                        throw JsonParseException.unexpectedToken(getPosition(), token);
                } // switch
                if (status == Status.IN_ERROR) {
                    throw JsonParseException.unexpectedToken(getPosition(), token);
                }
            } while (token != Yytoken.TYPE_EOF);
        } catch (IOException | JsonParseException | RuntimeException | Error ie) {
            status = Status.IN_ERROR;
            throw ie;
        }

        if (status == Status.IN_FINISHED_VALUE) {
            contentHandler.endJSON();
            status = Status.END;
            return;
        }

        status = Status.IN_ERROR;
        throw JsonParseException.unexpectedToken(getPosition(), token);
    }

    private boolean handleArrayStart(ContentHandler contentHandler)
            throws JsonParseException, IOException {
        if (!contentHandler.startArray(toHandlerStatus(status))) return true;
        status = Status.IN_ARRAY;
        statusStack.addFirst(status);
        nextToken();
        return handleMissingArrayValue(contentHandler);
    }

    private boolean handleArrayEnd(ContentHandler contentHandler)
            throws JsonParseException, IOException {
        if (statusStack.size() > 1) {
            statusStack.removeFirst();
            status = peekStatus(statusStack);
        } else {
            status = Status.IN_FINISHED_VALUE;
        }
        if (!contentHandler.endArray(toHandlerStatus(status))) return true;
        nextToken();
        return false;
    }

    private void handleObjectStart(ContentHandler contentHandler)
            throws JsonParseException, IOException {
        if (!contentHandler.startObject(toHandlerStatus(status))) return;
        status = Status.IN_OBJECT;
        statusStack.addFirst(status);
        nextToken();
        handleMissingObjectItem();
    }

    private boolean handleObjectEnd(ContentHandler contentHandler)
            throws JsonParseException, IOException {
        if (statusStack.size() > 1) {
            statusStack.removeFirst();
            status = peekStatus(statusStack);
        } else {
            status = Status.IN_FINISHED_VALUE;
        }
        if (!contentHandler.endObject(toHandlerStatus(status))) return true;
        nextToken();
        return false;
    }

    private boolean handleObjectKey(ContentHandler contentHandler)
            throws JsonParseException, IOException {
        Yytoken keyToken = token;
        if (!contentHandler.startObjectEntry(keyToken.value)) return true;
        status = Status.PASSED_PAIR_KEY;
        statusStack.addFirst(status);
        nextToken();
        handleMissingObjectValue(contentHandler, keyToken.value, keyToken.rawValue);
        return false;
    }

    private boolean handlePrimitiveValue(ContentHandler contentHandler)
            throws JsonParseException, IOException {
        Yytoken.YyPrimitiveToken ptoken = (Yytoken.YyPrimitiveToken) token;
        if (!contentHandler.primitive(
                toHandlerStatus(status), ptoken.type, token.value, token.rawValue)) return true;
        nextToken();
        return false;
    }

    private boolean handleMissingArrayValue(ContentHandler contentHandler)
            throws JsonParseException, IOException {
        if (token == Yytoken.TYPE_ITEM_SEPARATOR) {
            if (config.allowMissingArrayValues()) {
                // Missing values are allowed, so we add a `null`
                return !contentHandler.primitive(
                        // NB the raw value below is empty ON PURPOSE!
                        // If the value gets turned into a string again we want to be
                        // sure that it will not be represented as "null" but as the
                        // missing value that it start out as.
                        ContentHandler.Status.ARRAY, JsonPrimitive.Type.NULL, "null", "");
            } else {
                // Missing values are not allowed
                status = Status.IN_ERROR;
            }
        }
        return false;
    }

    private void handleMissingObjectItem() {
        if (token == Yytoken.TYPE_ITEM_SEPARATOR) {
            // Missing key:values are not allowed
            status = Status.IN_ERROR;
        }
    }

    private void handleMissingObjectValue(
            ContentHandler contentHandler, String keyValue, String rawKeyValue)
            throws JsonParseException, IOException {
        if (token != Yytoken.TYPE_PAIR_SEPARATOR) {
            if (config.allowObjectValuesAsKeys()) {
                // Missing values are allowed, so we add a value equal to the key
                contentHandler.primitive(
                        ContentHandler.Status.OBJECT,
                        JsonPrimitive.Type.STRING,
                        keyValue,
                        rawKeyValue);
                statusStack.removeFirst();
                status = peekStatus(statusStack);
            } else {
                // Missing colon
                status = Status.IN_ERROR;
            }
        }
    }

    private void handleTrailingSeparator() {
        if (!config.allowTrailingSeparator() && !isValueToken(token)) {
            // Trailing separators are not allowed
            status = Status.IN_ERROR;
        }
    }

    private boolean isValueToken(Yytoken token) {
        return token == Yytoken.TYPE_LEFT_BRACE
                || token == Yytoken.TYPE_LEFT_SQUARE
                || token instanceof Yytoken.YyPrimitiveToken;
    }

    private ContentHandler.Status toHandlerStatus(Status status) {
        if (status == Status.IN_OBJECT
                || status == Status.PASSED_PAIR_KEY
                || status == Status.IN_PAIR_VALUE) {
            return ContentHandler.Status.OBJECT;
        } else if (status == Status.IN_ARRAY) {
            return ContentHandler.Status.ARRAY;
        } else {
            return ContentHandler.Status.TOPLEVEL;
        }
    }
}
