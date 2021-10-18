package org.codejive.jsonc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import org.codejive.jsonc.parser.JsonParseException;
import org.codejive.jsonc.parser.JsonParser;
import org.junit.jupiter.api.Test;

public class JsonArrayTest {

    @Test
    public void testJSONArray() {
        final JsonArray jsonArray = new JsonArray();

        assertEquals("[]", jsonArray.toJSONString());
    }

    @Test
    public void testJSONArrayCollection() {
        final ArrayList testList = new ArrayList();
        testList.add("First item");
        testList.add("Second item");

        final JsonArray jsonArray = new JsonArray(testList);

        assertEquals("[\"First item\",\"Second item\"]", jsonArray.toJSONString());
    }

    @Test
    public void testWriteJSONStringCollectionWriter() throws IOException, JsonParseException {
        final HashSet testSet = new HashSet();
        testSet.add(Jsonc.string("First item"));
        testSet.add(Jsonc.string("Second item"));

        final JsonArray jsonArray = new JsonArray(testSet);
        final StringWriter writer = new StringWriter();

        jsonArray.writeJSONString(writer);

        final JsonParser parser = new JsonParser();
        final JsonArray parsedArray = (JsonArray) parser.parse(writer.toString());

        assertTrue(parsedArray.containsAll(jsonArray));
        assertTrue(jsonArray.containsAll(parsedArray));
        assertEquals(2, jsonArray.size());
    }

    @Test
    public void testToJSONStringCollection() throws JsonParseException {
        final HashSet testSet = new HashSet();
        testSet.add(Jsonc.string("First item"));
        testSet.add(Jsonc.string("Second item"));

        final JsonArray jsonArray = new JsonArray(testSet);

        final JsonParser parser = new JsonParser();
        final JsonArray parsedArray = (JsonArray) parser.parse(jsonArray.toJSONString());

        assertTrue(parsedArray.containsAll(jsonArray));
        assertTrue(jsonArray.containsAll(parsedArray));
        assertEquals(2, jsonArray.size());
    }

    private void assertEquals(Object expected, Object val) {
        assertThat(val, equalTo(expected));
    }
}
