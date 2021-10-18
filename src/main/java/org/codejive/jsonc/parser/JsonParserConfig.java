package org.codejive.jsonc.parser;

public class JsonParserConfig {
    private boolean allowToplevelValues;
    private boolean allowTrailingSeparator;
    private boolean allowMissingArrayValues;
    private boolean allowObjectPrimitiveKeys;

    public static JsonParserConfig defaults() {
        return new JsonParserConfig()
                .allowToplevelValues(true)
                .allowTrailingSeparator(true)
                .allowObjectPrimitiveKeys(true);
    }

    public static JsonParserConfig strictJson() {
        return new JsonParserConfig();
    }

    public static JsonParserConfig lenientJson() {
        return new JsonParserConfig()
                .allowToplevelValues(true)
                .allowTrailingSeparator(true)
                .allowMissingArrayValues(true)
                .allowObjectPrimitiveKeys(true);
    }

    public boolean allowToplevelValues() {
        return allowToplevelValues;
    }

    public JsonParserConfig allowToplevelValues(boolean allowToplevelValues) {
        this.allowToplevelValues = allowToplevelValues;
        return this;
    }

    public boolean allowTrailingSeparator() {
        return allowTrailingSeparator;
    }

    public JsonParserConfig allowTrailingSeparator(boolean allowTrailingSeparator) {
        this.allowTrailingSeparator = allowTrailingSeparator;
        return this;
    }

    public boolean allowMissingArrayValues() {
        return allowMissingArrayValues;
    }

    public JsonParserConfig allowMissingArrayValues(boolean allowMissingValues) {
        this.allowMissingArrayValues = allowMissingValues;
        return this;
    }

    public boolean allowObjectPrimitiveKeys() {
        return allowObjectPrimitiveKeys;
    }

    public JsonParserConfig allowObjectPrimitiveKeys(boolean allowObjectPrimitiveKeys) {
        this.allowObjectPrimitiveKeys = allowObjectPrimitiveKeys;
        return this;
    }
}
