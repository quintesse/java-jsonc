package org.codejive.jsonc.parser;

public class JsonParserConfig {
    private boolean allowToplevelValues;

    public static JsonParserConfig defaults() {
        return new JsonParserConfig()
                .allowToplevelValues(true);
    }

    public static JsonParserConfig strictJson() {
        return new JsonParserConfig();
    }

    public static JsonParserConfig lenientJson() {
        return new JsonParserConfig()
                .allowToplevelValues(true);
    }

    public boolean allowToplevelValues() {
        return allowToplevelValues;
    }

    public JsonParserConfig allowToplevelValues(boolean allowToplevelValues) {
        this.allowToplevelValues = allowToplevelValues;
        return this;
    }

}
