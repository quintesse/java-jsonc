package org.codejive.jsonc.parser;

public class JsonParserConfig {
    private boolean allowToplevelValues;
    private boolean allowTrailingSeparator;

    public static JsonParserConfig defaults() {
        return new JsonParserConfig()
                .allowToplevelValues(true)
                .allowTrailingSeparator(true);
    }

    public static JsonParserConfig strictJson() {
        return new JsonParserConfig();
    }

    public static JsonParserConfig lenientJson() {
        return new JsonParserConfig()
                .allowToplevelValues(true)
                .allowTrailingSeparator(true);
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

}
