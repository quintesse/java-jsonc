package org.codejive.jsonc.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.codejive.jsonc.JsonArray;
import org.codejive.jsonc.JsonObject;

/**
 * Parser for JSON text. Please note that JSONParser is NOT thread-safe.
 *
 * @author FangYidong<fangyidong@yahoo.com.cn>
 */
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
        return parse(s, new DefaultContainerFactory());
    }

    public Object parse(String s, ContainerFactory containerFactory) throws JsonParseException {
        DefaultContentHandler dch = new DefaultContentHandler(containerFactory);
        parse(s, dch);
        return dch.getResult();
    }

    public Object parse(Reader in) throws IOException, JsonParseException {
        DefaultContentHandler dch = new DefaultContentHandler(new DefaultContainerFactory());
        parse(in, dch);
        return dch.getResult();
    }

    static class DefaultContainerFactory implements ContainerFactory {
        @Override
        public Map<String, Object> createObjectContainer() {
            return new JsonObject();
        }

        @Override
        public List<Object> creatArrayContainer() {
            return new JsonArray();
        }
    }

    static class DefaultContentHandler implements ContentHandler {
        private final ContainerFactory containerFactory;

        private LinkedList<Object> valueStack;
        private Object result;

        public DefaultContentHandler(ContainerFactory containerFactory) {
            this.containerFactory = containerFactory;
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
            Object obj = containerFactory.createObjectContainer();
            addElement(status, obj);
            valueStack.addFirst(obj);
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
            Object arr = containerFactory.creatArrayContainer();
            addElement(status, arr);
            valueStack.addFirst(arr);
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
        public boolean primitive(Status status, Object value) {
            addElement(status, value);
            return true;
        }

        private void addElement(Status status, Object element) {
            switch (status) {
                case TOPLEVEL:
                    valueStack.addFirst(element);
                    break;
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

        LinkedList<Status> statusStack = handlerStatusStack;

        try {
            nextToken();
            do {
                switch (status) {
                    case INIT:
                        contentHandler.startJSON();
                        if (token instanceof Yytoken.YyPrimitiveToken) {
                            if (config.allowToplevelValues()) {
                                status = Status.IN_FINISHED_VALUE;
                                statusStack.addFirst(status);
                                if (!contentHandler.primitive(
                                        ContentHandler.Status.TOPLEVEL, token.value)) return;
                            } else {
                                status = Status.IN_ERROR;
                                throw JsonParseException.unexpectedToken(getPosition(), token);
                            }
                            nextToken();
                        } else if (Yytoken.TYPE_LEFT_BRACE == token) {
                            status = Status.IN_OBJECT;
                            statusStack.addFirst(status);
                            if (!contentHandler.startObject(ContentHandler.Status.TOPLEVEL)) return;
                            nextToken();
                            handleMissingObjectKeyValue();
                        } else if (Yytoken.TYPE_LEFT_SQUARE == token) {
                            status = Status.IN_ARRAY;
                            statusStack.addFirst(status);
                            if (!contentHandler.startArray(ContentHandler.Status.TOPLEVEL)) return;
                            nextToken();
                            if (handleMissingArrayValue(contentHandler)) return;
                        } else {
                            status = Status.IN_ERROR;
                        } // inner switch
                        break;

                    case IN_FINISHED_VALUE:
                        status = Status.IN_ERROR;
                        break;

                    case IN_OBJECT:
                        if (Yytoken.TYPE_COMMA == token) {
                            nextToken();
                            handleMissingObjectKeyValue();
                            handleTrailingSeparator();
                        } else if (token instanceof Yytoken.YyPrimitiveToken) {
                            if (config.allowObjectPrimitiveKeys() || token.value instanceof String) {
                                String key = Objects.toString(token.value);
                                status = Status.PASSED_PAIR_KEY;
                                statusStack.addFirst(status);
                                if (!contentHandler.startObjectEntry(key)) return;
                                nextToken();
                            } else {
                                status = Status.IN_ERROR;
                            }
                        } else if (Yytoken.TYPE_RIGHT_BRACE == token) {
                            if (statusStack.size() > 1) {
                                statusStack.removeFirst();
                                status = peekStatus(statusStack);
                            } else {
                                status = Status.IN_FINISHED_VALUE;
                            }
                            if (!contentHandler.endObject(toHandlerStatus(status))) return;
                            nextToken();
                        } else {
                            status = Status.IN_ERROR;
                        } // inner switch
                        break;

                    case PASSED_PAIR_KEY:
                        if (Yytoken.TYPE_COLON != token) {
                            // Missing colon
                            status = Status.IN_ERROR;
                        }
                        nextToken();
                        if (token instanceof Yytoken.YyPrimitiveToken) {
                            statusStack.removeFirst();
                            status = peekStatus(statusStack);
                            if (!contentHandler.primitive(
                                    ContentHandler.Status.OBJECT, token.value)) return;
                            if (!contentHandler.endObjectEntry()) return;
                            nextToken();
                        } else if (Yytoken.TYPE_LEFT_SQUARE == token) {
                            statusStack.removeFirst();
                            statusStack.addFirst(Status.IN_PAIR_VALUE);
                            status = Status.IN_ARRAY;
                            statusStack.addFirst(status);
                            if (!contentHandler.startArray(ContentHandler.Status.OBJECT)) return;
                            nextToken();
                            if (handleMissingArrayValue(contentHandler)) return;
                        } else if (Yytoken.TYPE_LEFT_BRACE == token) {
                            statusStack.removeFirst();
                            statusStack.addFirst(Status.IN_PAIR_VALUE);
                            status = Status.IN_OBJECT;
                            statusStack.addFirst(status);
                            if (!contentHandler.startObject(ContentHandler.Status.OBJECT)) return;
                            nextToken();
                            handleMissingObjectKeyValue();
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
                        if (Yytoken.TYPE_COMMA == token) {
                            nextToken();
                            if (handleMissingArrayValue(contentHandler)) return;
                            handleTrailingSeparator();
                        } else if (token instanceof Yytoken.YyPrimitiveToken) {
                            if (!contentHandler.primitive(ContentHandler.Status.ARRAY, token.value))
                                return;
                            nextToken();
                        } else if (Yytoken.TYPE_RIGHT_SQUARE == token) {
                            if (statusStack.size() > 1) {
                                statusStack.removeFirst();
                                status = peekStatus(statusStack);
                            } else {
                                status = Status.IN_FINISHED_VALUE;
                            }
                            if (!contentHandler.endArray(toHandlerStatus(status))) return;
                            nextToken();
                        } else if (Yytoken.TYPE_LEFT_BRACE == token) {
                            status = Status.IN_OBJECT;
                            statusStack.addFirst(status);
                            if (!contentHandler.startObject(ContentHandler.Status.ARRAY)) return;
                            nextToken();
                            handleMissingObjectKeyValue();
                        } else if (Yytoken.TYPE_LEFT_SQUARE == token) {
                            status = Status.IN_ARRAY;
                            statusStack.addFirst(status);
                            if (!contentHandler.startArray(ContentHandler.Status.ARRAY)) return;
                            nextToken();
                            if (handleMissingArrayValue(contentHandler)) return;
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

        if (status == Status.IN_FINISHED_VALUE && token == Yytoken.TYPE_EOF) {
            contentHandler.endJSON();
            status = Status.END;
            return;
        }

        status = Status.IN_ERROR;
        throw JsonParseException.unexpectedToken(getPosition(), token);
    }

    private boolean handleMissingArrayValue(ContentHandler contentHandler) throws JsonParseException, IOException {
        if (token == Yytoken.TYPE_COMMA) {
            if (config.allowMissingArrayValues()) {
                // Missing values are allowed, so we add a `null`
                if (!contentHandler.primitive(ContentHandler.Status.ARRAY, null))
                    return true;
            } else {
                // Missing values are not allowed
                status = Status.IN_ERROR;
            }
        }
        return false;
    }

    private boolean handleMissingObjectKeyValue() {
        if (token == Yytoken.TYPE_COMMA) {
            // Missing key:values are not allowed
            status = Status.IN_ERROR;
        }
        return false;
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
