package org.codejive.jsonc;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;

/** A JSON array. JSONObject supports java.util.List interface. */
public class JsonArray extends ArrayList<Object> implements JsonElement {
    private static final long serialVersionUID = 3957988303675231981L;

    /** Constructs an empty JSONArray. */
    public JsonArray() {
        super();
    }

    /**
     * Constructs a JSONArray containing the elements of the specified collection, in the order they
     * are returned by the collection's iterator.
     *
     * @param c the collection whose elements are to be placed into this JSONArray
     */
    public JsonArray(Collection c) {
        super(c);
    }

    public void writeJSONString(Writer out) throws IOException {
        Jsonc.writeJSONString(this, out, true);
    }

    @Override
    public String toJSONString() {
        return Jsonc.toJSONString(this, true);
    }

    /**
     * Returns a string representation of this array. This is equivalent to calling {@link
     * JsonArray#toJSONString()}.
     */
    public String toString() {
        return Jsonc.toJSONString(this, false);
    }
}
