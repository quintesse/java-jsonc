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
        testSet.add("First item");
        testSet.add("Second item");

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
        testSet.add("First item");
        testSet.add("Second item");

        final JsonArray jsonArray = new JsonArray(testSet);

        final JsonParser parser = new JsonParser();
        final JsonArray parsedArray = (JsonArray) parser.parse(jsonArray.toJSONString());

        assertTrue(parsedArray.containsAll(jsonArray));
        assertTrue(jsonArray.containsAll(parsedArray));
        assertEquals(2, jsonArray.size());
    }

    @Test
    public void testByteArrayToString() throws IOException {
        assertEquals("null", JsonArray.toJSONString((byte[]) null));
        assertEquals("[]", JsonArray.toJSONString(new byte[0]));
        assertEquals("[12]", JsonArray.toJSONString(new byte[] {12}));
        assertEquals("[-7,22,86,-99]", JsonArray.toJSONString(new byte[] {-7, 22, 86, -99}));

        StringWriter writer;

        writer = new StringWriter();
        JsonArray.writeJSONString((byte[]) null, writer);
        assertEquals("null", writer.toString());

        writer = new StringWriter();
        JsonArray.writeJSONString(new byte[0], writer);
        assertEquals("[]", writer.toString());

        writer = new StringWriter();
        JsonArray.writeJSONString(new byte[] {12}, writer);
        assertEquals("[12]", writer.toString());

        writer = new StringWriter();
        JsonArray.writeJSONString(new byte[] {-7, 22, 86, -99}, writer);
        assertEquals("[-7,22,86,-99]", writer.toString());
    }

    @Test
    public void testShortArrayToString() throws IOException {
        assertEquals("null", JsonArray.toJSONString((short[]) null));
        assertEquals("[]", JsonArray.toJSONString(new short[0]));
        assertEquals("[12]", JsonArray.toJSONString(new short[] {12}));
        assertEquals("[-7,22,86,-99]", JsonArray.toJSONString(new short[] {-7, 22, 86, -99}));

        StringWriter writer;

        writer = new StringWriter();
        JsonArray.writeJSONString((short[]) null, writer);
        assertEquals("null", writer.toString());

        writer = new StringWriter();
        JsonArray.writeJSONString(new short[0], writer);
        assertEquals("[]", writer.toString());

        writer = new StringWriter();
        JsonArray.writeJSONString(new short[] {12}, writer);
        assertEquals("[12]", writer.toString());

        writer = new StringWriter();
        JsonArray.writeJSONString(new short[] {-7, 22, 86, -99}, writer);
        assertEquals("[-7,22,86,-99]", writer.toString());
    }

    @Test
    public void testIntArrayToString() throws IOException {
        assertEquals("null", JsonArray.toJSONString((int[]) null));
        assertEquals("[]", JsonArray.toJSONString(new int[0]));
        assertEquals("[12]", JsonArray.toJSONString(new int[] {12}));
        assertEquals("[-7,22,86,-99]", JsonArray.toJSONString(new int[] {-7, 22, 86, -99}));

        StringWriter writer;

        writer = new StringWriter();
        JsonArray.writeJSONString((int[]) null, writer);
        assertEquals("null", writer.toString());

        writer = new StringWriter();
        JsonArray.writeJSONString(new int[0], writer);
        assertEquals("[]", writer.toString());

        writer = new StringWriter();
        JsonArray.writeJSONString(new int[] {12}, writer);
        assertEquals("[12]", writer.toString());

        writer = new StringWriter();
        JsonArray.writeJSONString(new int[] {-7, 22, 86, -99}, writer);
        assertEquals("[-7,22,86,-99]", writer.toString());
    }

    @Test
    public void testLongArrayToString() throws IOException {
        assertEquals("null", JsonArray.toJSONString((long[]) null));
        assertEquals("[]", JsonArray.toJSONString(new long[0]));
        assertEquals("[12]", JsonArray.toJSONString(new long[] {12}));
        assertEquals(
                "[-7,22,9223372036854775807,-99]",
                JsonArray.toJSONString(new long[] {-7, 22, 9223372036854775807L, -99}));

        StringWriter writer;

        writer = new StringWriter();
        JsonArray.writeJSONString((long[]) null, writer);
        assertEquals("null", writer.toString());

        writer = new StringWriter();
        JsonArray.writeJSONString(new long[0], writer);
        assertEquals("[]", writer.toString());

        writer = new StringWriter();
        JsonArray.writeJSONString(new long[] {12}, writer);
        assertEquals("[12]", writer.toString());

        writer = new StringWriter();
        JsonArray.writeJSONString(new long[] {-7, 22, 86, -99}, writer);
        assertEquals("[-7,22,86,-99]", writer.toString());
    }

    @Test
    public void testFloatArrayToString() throws IOException {
        assertEquals("null", JsonArray.toJSONString((float[]) null));
        assertEquals("[]", JsonArray.toJSONString(new float[0]));
        assertEquals("[12.8]", JsonArray.toJSONString(new float[] {12.8f}));
        assertEquals(
                "[-7.1,22.234,86.7,-99.02]",
                JsonArray.toJSONString(new float[] {-7.1f, 22.234f, 86.7f, -99.02f}));

        StringWriter writer;

        writer = new StringWriter();
        JsonArray.writeJSONString((float[]) null, writer);
        assertEquals("null", writer.toString());

        writer = new StringWriter();
        JsonArray.writeJSONString(new float[0], writer);
        assertEquals("[]", writer.toString());

        writer = new StringWriter();
        JsonArray.writeJSONString(new float[] {12.8f}, writer);
        assertEquals("[12.8]", writer.toString());

        writer = new StringWriter();
        JsonArray.writeJSONString(new float[] {-7.1f, 22.234f, 86.7f, -99.02f}, writer);
        assertEquals("[-7.1,22.234,86.7,-99.02]", writer.toString());
    }

    @Test
    public void testDoubleArrayToString() throws IOException {
        assertEquals("null", JsonArray.toJSONString((double[]) null));
        assertEquals("[]", JsonArray.toJSONString(new double[0]));
        assertEquals("[12.8]", JsonArray.toJSONString(new double[] {12.8}));
        assertEquals(
                "[-7.1,22.234,86.7,-99.02]",
                JsonArray.toJSONString(new double[] {-7.1, 22.234, 86.7, -99.02}));

        StringWriter writer;

        writer = new StringWriter();
        JsonArray.writeJSONString((double[]) null, writer);
        assertEquals("null", writer.toString());

        writer = new StringWriter();
        JsonArray.writeJSONString(new double[0], writer);
        assertEquals("[]", writer.toString());

        writer = new StringWriter();
        JsonArray.writeJSONString(new double[] {12.8}, writer);
        assertEquals("[12.8]", writer.toString());

        writer = new StringWriter();
        JsonArray.writeJSONString(new double[] {-7.1, 22.234, 86.7, -99.02}, writer);
        assertEquals("[-7.1,22.234,86.7,-99.02]", writer.toString());
    }

    @Test
    public void testBooleanArrayToString() throws IOException {
        assertEquals("null", JsonArray.toJSONString((boolean[]) null));
        assertEquals("[]", JsonArray.toJSONString(new boolean[0]));
        assertEquals("[true]", JsonArray.toJSONString(new boolean[] {true}));
        assertEquals(
                "[true,false,true]", JsonArray.toJSONString(new boolean[] {true, false, true}));

        StringWriter writer;

        writer = new StringWriter();
        JsonArray.writeJSONString((boolean[]) null, writer);
        assertEquals("null", writer.toString());

        writer = new StringWriter();
        JsonArray.writeJSONString(new boolean[0], writer);
        assertEquals("[]", writer.toString());

        writer = new StringWriter();
        JsonArray.writeJSONString(new boolean[] {true}, writer);
        assertEquals("[true]", writer.toString());

        writer = new StringWriter();
        JsonArray.writeJSONString(new boolean[] {true, false, true}, writer);
        assertEquals("[true,false,true]", writer.toString());
    }

    @Test
    public void testCharArrayToString() throws IOException {
        assertEquals("null", JsonArray.toJSONString((char[]) null));
        assertEquals("[]", JsonArray.toJSONString(new char[0]));
        assertEquals("[\"a\"]", JsonArray.toJSONString(new char[] {'a'}));
        assertEquals("[\"a\",\"b\",\"c\"]", JsonArray.toJSONString(new char[] {'a', 'b', 'c'}));

        StringWriter writer;

        writer = new StringWriter();
        JsonArray.writeJSONString((char[]) null, writer);
        assertEquals("null", writer.toString());

        writer = new StringWriter();
        JsonArray.writeJSONString(new char[0], writer);
        assertEquals("[]", writer.toString());

        writer = new StringWriter();
        JsonArray.writeJSONString(new char[] {'a'}, writer);
        assertEquals("[\"a\"]", writer.toString());

        writer = new StringWriter();
        JsonArray.writeJSONString(new char[] {'a', 'b', 'c'}, writer);
        assertEquals("[\"a\",\"b\",\"c\"]", writer.toString());
    }

    @Test
    public void testObjectArrayToString() throws IOException {
        assertEquals("null", JsonArray.toJSONString((Object[]) null));
        assertEquals("[]", JsonArray.toJSONString(new Object[0]));
        assertEquals("[\"Hello\"]", JsonArray.toJSONString(new Object[] {"Hello"}));
        assertEquals(
                "[\"Hello\",12,[1,2,3]]",
                JsonArray.toJSONString(
                        new Object[] {"Hello", new Integer(12), new int[] {1, 2, 3}}));

        StringWriter writer;

        writer = new StringWriter();
        JsonArray.writeJSONString((Object[]) null, writer);
        assertEquals("null", writer.toString());

        writer = new StringWriter();
        JsonArray.writeJSONString(new Object[0], writer);
        assertEquals("[]", writer.toString());

        writer = new StringWriter();
        JsonArray.writeJSONString(new Object[] {"Hello"}, writer);
        assertEquals("[\"Hello\"]", writer.toString());

        writer = new StringWriter();
        JsonArray.writeJSONString(
                new Object[] {"Hello", new Integer(12), new int[] {1, 2, 3}}, writer);
        assertEquals("[\"Hello\",12,[1,2,3]]", writer.toString());
    }

    private void assertEquals(Object expected, Object val) {
        assertThat(val, equalTo(expected));
    }
}
