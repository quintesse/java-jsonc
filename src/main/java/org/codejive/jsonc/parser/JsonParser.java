package org.codejive.jsonc.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.codejive.jsonc.JsonArray;
import org.codejive.jsonc.JsonObject;

/**
 * Parser for JSON text. Please note that JSONParser is NOT thread-safe.
 *
 * @author FangYidong<fangyidong@yahoo.com.cn>
 */
public class JsonParser {
    public static final int S_INIT = 0;
    public static final int S_IN_FINISHED_VALUE = 1; // string,number,boolean,null,object,array
    public static final int S_IN_OBJECT = 2;
    public static final int S_IN_ARRAY = 3;
    public static final int S_PASSED_PAIR_KEY = 4;
    public static final int S_IN_PAIR_VALUE = 5;
    public static final int S_END = 6;
    public static final int S_IN_ERROR = -1;

    private LinkedList handlerStatusStack;
    private Yylex lexer = new Yylex((Reader) null);
    private Yytoken token = null;
    private int status = S_INIT;

    private int peekStatus(LinkedList statusStack) {
        if (statusStack.size() == 0) return -1;
        Integer status = (Integer) statusStack.getFirst();
        return status.intValue();
    }

    /** Reset the parser to the initial state without resetting the underlying reader. */
    public void reset() {
        token = null;
        status = S_INIT;
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
        public Map createObjectContainer() {
            return new JsonObject();
        }

        @Override
        public List creatArrayContainer() {
            return new JsonArray();
        }
    }

    static class DefaultContentHandler implements ContentHandler {
        private ContainerFactory containerFactory;

        private LinkedList valueStack;
        private Object result;

        public DefaultContentHandler(ContainerFactory containerFactory) {
            this.containerFactory = containerFactory;
        }

        public Object getResult() {
            return result;
        }

        @Override
        public void startJSON() {
            valueStack = new LinkedList();
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
                    Map parent = (Map) valueStack.getFirst();
                    parent.put(key, element);
                    break;
                case ARRAY:
                    List val = (List) valueStack.getFirst();
                    val.add(element);
                    break;
            }
        }
    }

    private void nextToken() throws JsonParseException, IOException {
        token = lexer.yylex();
        if (token == null) token = new Yytoken(Yytoken.TYPE_EOF, null);
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
            throw new JsonParseException(-1, JsonParseException.ERROR_UNEXPECTED_EXCEPTION, ie);
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
            handlerStatusStack = new LinkedList();
        } else {
            if (handlerStatusStack == null) {
                isResume = false;
                reset(in);
                handlerStatusStack = new LinkedList();
            }
        }

        LinkedList statusStack = handlerStatusStack;

        try {
            do {
                switch (status) {
                    case S_INIT:
                        contentHandler.startJSON();
                        nextToken();
                        switch (token.type) {
                            case Yytoken.TYPE_VALUE:
                                status = S_IN_FINISHED_VALUE;
                                statusStack.addFirst(new Integer(status));
                                if (!contentHandler.primitive(
                                        ContentHandler.Status.TOPLEVEL, token.value)) return;
                                break;
                            case Yytoken.TYPE_LEFT_BRACE:
                                status = S_IN_OBJECT;
                                statusStack.addFirst(new Integer(status));
                                if (!contentHandler.startObject(ContentHandler.Status.TOPLEVEL))
                                    return;
                                break;
                            case Yytoken.TYPE_LEFT_SQUARE:
                                status = S_IN_ARRAY;
                                statusStack.addFirst(new Integer(status));
                                if (!contentHandler.startArray(ContentHandler.Status.TOPLEVEL))
                                    return;
                                break;
                            default:
                                status = S_IN_ERROR;
                        } // inner switch
                        break;

                    case S_IN_FINISHED_VALUE:
                        nextToken();
                        if (token.type == Yytoken.TYPE_EOF) {
                            contentHandler.endJSON();
                            status = S_END;
                            return;
                        } else {
                            status = S_IN_ERROR;
                            throw new JsonParseException(
                                    getPosition(),
                                    JsonParseException.ERROR_UNEXPECTED_TOKEN,
                                    token);
                        }

                    case S_IN_OBJECT:
                        nextToken();
                        switch (token.type) {
                            case Yytoken.TYPE_COMMA:
                                break;
                            case Yytoken.TYPE_VALUE:
                                if (token.value instanceof String) {
                                    String key = (String) token.value;
                                    status = S_PASSED_PAIR_KEY;
                                    statusStack.addFirst(new Integer(status));
                                    if (!contentHandler.startObjectEntry(key)) return;
                                } else {
                                    status = S_IN_ERROR;
                                }
                                break;
                            case Yytoken.TYPE_RIGHT_BRACE:
                                if (statusStack.size() > 1) {
                                    statusStack.removeFirst();
                                    status = peekStatus(statusStack);
                                } else {
                                    status = S_IN_FINISHED_VALUE;
                                }
                                if (!contentHandler.endObject(toHandlerStatus(status))) return;
                                break;
                            default:
                                status = S_IN_ERROR;
                                break;
                        } // inner switch
                        break;

                    case S_PASSED_PAIR_KEY:
                        nextToken();
                        switch (token.type) {
                            case Yytoken.TYPE_COLON:
                                break;
                            case Yytoken.TYPE_VALUE:
                                statusStack.removeFirst();
                                status = peekStatus(statusStack);
                                if (!contentHandler.primitive(
                                        ContentHandler.Status.OBJECT, token.value)) return;
                                if (!contentHandler.endObjectEntry()) return;
                                break;
                            case Yytoken.TYPE_LEFT_SQUARE:
                                statusStack.removeFirst();
                                statusStack.addFirst(new Integer(S_IN_PAIR_VALUE));
                                status = S_IN_ARRAY;
                                statusStack.addFirst(new Integer(status));
                                if (!contentHandler.startArray(ContentHandler.Status.OBJECT))
                                    return;
                                break;
                            case Yytoken.TYPE_LEFT_BRACE:
                                statusStack.removeFirst();
                                statusStack.addFirst(new Integer(S_IN_PAIR_VALUE));
                                status = S_IN_OBJECT;
                                statusStack.addFirst(new Integer(status));
                                if (!contentHandler.startObject(ContentHandler.Status.OBJECT))
                                    return;
                                break;
                            default:
                                status = S_IN_ERROR;
                        }
                        break;

                    case S_IN_PAIR_VALUE:
                        /*
                         * S_IN_PAIR_VALUE is just a marker to indicate the end of an object entry, it
                         * doesn't proccess any token, therefore delay consuming token until next round.
                         */
                        statusStack.removeFirst();
                        status = peekStatus(statusStack);
                        if (!contentHandler.endObjectEntry()) return;
                        break;

                    case S_IN_ARRAY:
                        nextToken();
                        switch (token.type) {
                            case Yytoken.TYPE_COMMA:
                                break;
                            case Yytoken.TYPE_VALUE:
                                if (!contentHandler.primitive(
                                        ContentHandler.Status.ARRAY, token.value)) return;
                                break;
                            case Yytoken.TYPE_RIGHT_SQUARE:
                                if (statusStack.size() > 1) {
                                    statusStack.removeFirst();
                                    status = peekStatus(statusStack);
                                } else {
                                    status = S_IN_FINISHED_VALUE;
                                }
                                if (!contentHandler.endArray(toHandlerStatus(status))) return;
                                break;
                            case Yytoken.TYPE_LEFT_BRACE:
                                status = S_IN_OBJECT;
                                statusStack.addFirst(new Integer(status));
                                if (!contentHandler.startObject(ContentHandler.Status.ARRAY))
                                    return;
                                break;
                            case Yytoken.TYPE_LEFT_SQUARE:
                                status = S_IN_ARRAY;
                                statusStack.addFirst(new Integer(status));
                                if (!contentHandler.startArray(ContentHandler.Status.ARRAY)) return;
                                break;
                            default:
                                status = S_IN_ERROR;
                        } // inner switch
                        break;

                    case S_END:
                        return;

                    case S_IN_ERROR:
                        throw new JsonParseException(
                                getPosition(), JsonParseException.ERROR_UNEXPECTED_TOKEN, token);
                } // switch
                if (status == S_IN_ERROR) {
                    throw new JsonParseException(
                            getPosition(), JsonParseException.ERROR_UNEXPECTED_TOKEN, token);
                }
            } while (token.type != Yytoken.TYPE_EOF);
        } catch (IOException ie) {
            status = S_IN_ERROR;
            throw ie;
        } catch (JsonParseException pe) {
            status = S_IN_ERROR;
            throw pe;
        } catch (RuntimeException re) {
            status = S_IN_ERROR;
            throw re;
        } catch (Error e) {
            status = S_IN_ERROR;
            throw e;
        }

        status = S_IN_ERROR;
        throw new JsonParseException(
                getPosition(), JsonParseException.ERROR_UNEXPECTED_TOKEN, token);
    }

    private ContentHandler.Status toHandlerStatus(int status) {
        if (status == S_IN_OBJECT || status == S_PASSED_PAIR_KEY || status == S_IN_PAIR_VALUE) {
            return ContentHandler.Status.OBJECT;
        } else if (status == S_IN_ARRAY) {
            return ContentHandler.Status.ARRAY;
        } else {
            return ContentHandler.Status.TOPLEVEL;
        }
    }
}
