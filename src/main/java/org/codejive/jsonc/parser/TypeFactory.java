package org.codejive.jsonc.parser;

import java.util.List;
import java.util.Map;
import org.codejive.jsonc.JsonPrimitive;

/** Type factory for creating the objects that represent/contain JSON values. */
public interface TypeFactory {
    /**
     * Returns an empty Map object
     *
     * @return A Map object
     */
    Map<String, Object> createObjectContainer();

    /**
     * Returns an empty List object
     *
     * @return A List object
     */
    List<Object> createArrayContainer();

    /**
     * Returns an object representing the provided primitive.
     *
     * @return An object
     */
    Object createPrimitive(JsonPrimitive.Type type, String value, String rawValue);
}
