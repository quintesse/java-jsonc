package org.codejive.jsonc;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/** A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface. */
public class JsonObject extends HashMap<String, Object> implements JsonElement {
    private static final long serialVersionUID = -503443796854799292L;

    public JsonObject() {
        super();
    }

    /**
     * Allows creation of a JSONObject from a Map. After that, both the generated JSONObject and the
     * Map can be modified independently.
     *
     * @param map
     */
    public JsonObject(Map<String, Object> map) {
        super(map);
    }

    public void writeJSONString(Writer out) throws IOException {
        Jsonc.writeJSONString(this, out, true);
    }

    @Override
    public String toJSONString() {
        return Jsonc.toJSONString(this, true);
    }

    public String toString() {
        return Jsonc.toJSONString(this, false);
    }
}
