package org.codejive.jsonc.parser;

import java.io.IOException;
import org.codejive.jsonc.JsonPrimitive;

/** A simplified and stoppable SAX-like content handler for stream processing of JSON text. */
public interface ContentHandler {
    enum Status {
        TOPLEVEL,
        OBJECT,
        ARRAY
    };

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
     * @param status - The current parse status
     * @return false if the handler wants to stop parsing after return.
     * @throws JsonParseException - JSONParser will stop and throw the same exception to the caller
     *     when receiving this exception.
     * @see #endJSON
     */
    boolean startObject(Status status) throws JsonParseException, IOException;

    /**
     * Receive notification of the end of a JSON object.
     *
     * @param status - The current parse status
     * @return false if the handler wants to stop parsing after return.
     * @throws JsonParseException
     * @see #startObject
     */
    boolean endObject(Status status) throws JsonParseException, IOException;

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
     * @param status - The current parse status
     * @return false if the handler wants to stop parsing after return.
     * @throws JsonParseException
     * @see #endArray
     */
    boolean startArray(Status status) throws JsonParseException, IOException;

    /**
     * Receive notification of the end of a JSON array.
     *
     * @param status - The current parse status
     * @return false if the handler wants to stop parsing after return.
     * @throws JsonParseException
     * @see #startArray
     */
    boolean endArray(Status status) throws JsonParseException, IOException;

    /**
     * Receive notification of the JSON primitive values: java.lang.String, java.lang.Number,
     * java.lang.Boolean null
     *
     * @param status - The current parse status
     * @param type - Indicates the type of primitive
     * @param value - The value of the type as a string
     * @param rawValue - The value of the type as a raw string. This is only useful for STRING types
     *     containing escape sequences. In which case escape sequences will NOT have been parsed and
     *     replaced.
     * @return false if the handler wants to stop parsing after return.
     * @throws JsonParseException
     */
    boolean primitive(Status status, JsonPrimitive.Type type, String value, String rawValue)
            throws JsonParseException, IOException;
}
