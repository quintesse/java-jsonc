package org.codejive.jsonc.parser;

public class JsonParserConfig {
    private boolean allowToplevelValues;
    private boolean allowTrailingSeparator;
    private boolean allowMissingArrayValues;
    private boolean allowObjectPrimitiveKeys;
    private boolean allowObjectValuesAsKeys;
    private boolean allowSingleQuotedStrings;
    private boolean allowUnquotedStrings;
    private boolean enableComments;

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
                .allowObjectPrimitiveKeys(true)
                .allowObjectValuesAsKeys(true)
                .allowSingleQuotedStrings(true)
                .allowUnquotedStrings(true);
    }

    public static JsonParserConfig strictJsonc() {
        return strictJson().enableComments(true);
    }

    public static JsonParserConfig lenientJsonc() {
        return lenientJson().enableComments(true);
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

    public boolean allowObjectValuesAsKeys() {
        return allowObjectValuesAsKeys;
    }

    public JsonParserConfig allowObjectValuesAsKeys(boolean allowObjectValuesAsKeys) {
        this.allowObjectValuesAsKeys = allowObjectValuesAsKeys;
        return this;
    }

    public boolean allowSingleQuotedStrings() {
        return allowSingleQuotedStrings;
    }

    public JsonParserConfig allowSingleQuotedStrings(boolean allowSingleQuotedStrings) {
        this.allowSingleQuotedStrings = allowSingleQuotedStrings;
        return this;
    }

    public boolean allowUnquotedStrings() {
        return allowUnquotedStrings;
    }

    public JsonParserConfig allowUnquotedStrings(boolean allowUnquotedStrings) {
        this.allowUnquotedStrings = allowUnquotedStrings;
        return this;
    }

    public boolean enableComments() {
        return enableComments;
    }

    public JsonParserConfig enableComments(boolean enableComments) {
        this.enableComments = enableComments;
        return this;
    }
}
