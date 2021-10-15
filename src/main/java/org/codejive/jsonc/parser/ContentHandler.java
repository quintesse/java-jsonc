package org.codejive.jsonc.parser;

import java.io.IOException;

/**
 * A simplified and stoppable SAX-like content handler for stream processing of JSON text.
 *
 * @see org.xml.sax.ContentHandler
 * @see JsonParser#parse(java.io.Reader, ContentHandler, boolean)
 * @author FangYidong<fangyidong@yahoo.com.cn>
 */
public interface ContentHandler {
    /**
     * Receive notification of the beginning of JSON processing. The parser will invoke this method
     * only once.
     *
     * @throws JsonParseException - JSONParser will stop and throw the same exception to the caller
     *     when receiving this exception.
     */
    void startJSON() throws JsonParseException, IOException;

    /**
     * Receive notification of the end of JSON processing.
     *
     * @throws JsonParseException
     */
    void endJSON() throws JsonParseException, IOException;

    /**
     * Receive notification of the beginning of a JSON object.
     *
     * @return false if the handler wants to stop parsing after return.
     * @throws JsonParseException - JSONParser will stop and throw the same exception to the caller
     *     when receiving this exception.
     * @see #endJSON
     */
    boolean startObject() throws JsonParseException, IOException;

    /**
     * Receive notification of the end of a JSON object.
     *
     * @return false if the handler wants to stop parsing after return.
     * @throws JsonParseException
     * @see #startObject
     */
    boolean endObject() throws JsonParseException, IOException;

    /**
     * Receive notification of the beginning of a JSON object entry.
     *
     * @param key - Key of a JSON object entry.
     * @return false if the handler wants to stop parsing after return.
     * @throws JsonParseException
     * @see #endObjectEntry
     */
    boolean startObjectEntry(String key) throws JsonParseException, IOException;

    /**
     * Receive notification of the end of the value of previous object entry.
     *
     * @return false if the handler wants to stop parsing after return.
     * @throws JsonParseException
     * @see #startObjectEntry
     */
    boolean endObjectEntry() throws JsonParseException, IOException;

    /**
     * Receive notification of the beginning of a JSON array.
     *
     * @return false if the handler wants to stop parsing after return.
     * @throws JsonParseException
     * @see #endArray
     */
    boolean startArray() throws JsonParseException, IOException;

    /**
     * Receive notification of the end of a JSON array.
     *
     * @return false if the handler wants to stop parsing after return.
     * @throws JsonParseException
     * @see #startArray
     */
    boolean endArray() throws JsonParseException, IOException;

    /**
     * Receive notification of the JSON primitive values: java.lang.String, java.lang.Number,
     * java.lang.Boolean null
     *
     * @param value - Instance of the following: java.lang.String, java.lang.Number,
     *     java.lang.Boolean null
     * @return false if the handler wants to stop parsing after return.
     * @throws JsonParseException
     */
    boolean primitive(Object value) throws JsonParseException, IOException;
}
