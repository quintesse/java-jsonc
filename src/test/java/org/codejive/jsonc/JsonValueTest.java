package org.codejive.jsonc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;

public class JsonValueTest {
    @Test
    public void testByteArrayToString() throws IOException {
        assertEquals("null", JsonValue.toJSONString((byte[]) null));
        assertEquals("[]", JsonValue.toJSONString(new byte[0]));
        assertEquals("[12]", JsonValue.toJSONString(new byte[] {12}));
        assertEquals("[-7,22,86,-99]", JsonValue.toJSONString(new byte[] {-7, 22, 86, -99}));

        StringWriter writer;

        writer = new StringWriter();
        JsonValue.writeJSONString((byte[]) null, writer);
        assertEquals("null", writer.toString());

        writer = new StringWriter();
        JsonValue.writeJSONString(new byte[0], writer);
        assertEquals("[]", writer.toString());

        writer = new StringWriter();
        JsonValue.writeJSONString(new byte[] {12}, writer);
        assertEquals("[12]", writer.toString());

        writer = new StringWriter();
        JsonValue.writeJSONString(new byte[] {-7, 22, 86, -99}, writer);
        assertEquals("[-7,22,86,-99]", writer.toString());
    }

    @Test
    public void testShortArrayToString() throws IOException {
        assertEquals("null", JsonValue.toJSONString((short[]) null));
        assertEquals("[]", JsonValue.toJSONString(new short[0]));
        assertEquals("[12]", JsonValue.toJSONString(new short[] {12}));
        assertEquals("[-7,22,86,-99]", JsonValue.toJSONString(new short[] {-7, 22, 86, -99}));

        StringWriter writer;

        writer = new StringWriter();
        JsonValue.writeJSONString((short[]) null, writer);
        assertEquals("null", writer.toString());

        writer = new StringWriter();
        JsonValue.writeJSONString(new short[0], writer);
        assertEquals("[]", writer.toString());

        writer = new StringWriter();
        JsonValue.writeJSONString(new short[] {12}, writer);
        assertEquals("[12]", writer.toString());

        writer = new StringWriter();
        JsonValue.writeJSONString(new short[] {-7, 22, 86, -99}, writer);
        assertEquals("[-7,22,86,-99]", writer.toString());
    }

    @Test
    public void testIntArrayToString() throws IOException {
        assertEquals("null", JsonValue.toJSONString((int[]) null));
        assertEquals("[]", JsonValue.toJSONString(new int[0]));
        assertEquals("[12]", JsonValue.toJSONString(new int[] {12}));
        assertEquals("[-7,22,86,-99]", JsonValue.toJSONString(new int[] {-7, 22, 86, -99}));

        StringWriter writer;

        writer = new StringWriter();
        JsonValue.writeJSONString((int[]) null, writer);
        assertEquals("null", writer.toString());

        writer = new StringWriter();
        JsonValue.writeJSONString(new int[0], writer);
        assertEquals("[]", writer.toString());

        writer = new StringWriter();
        JsonValue.writeJSONString(new int[] {12}, writer);
        assertEquals("[12]", writer.toString());

        writer = new StringWriter();
        JsonValue.writeJSONString(new int[] {-7, 22, 86, -99}, writer);
        assertEquals("[-7,22,86,-99]", writer.toString());
    }

    @Test
    public void testLongArrayToString() throws IOException {
        assertEquals("null", JsonValue.toJSONString((long[]) null));
        assertEquals("[]", JsonValue.toJSONString(new long[0]));
        assertEquals("[12]", JsonValue.toJSONString(new long[] {12}));
        assertEquals(
                "[-7,22,9223372036854775807,-99]",
                JsonValue.toJSONString(new long[] {-7, 22, 9223372036854775807L, -99}));

        StringWriter writer;

        writer = new StringWriter();
        JsonValue.writeJSONString((long[]) null, writer);
        assertEquals("null", writer.toString());

        writer = new StringWriter();
        JsonValue.writeJSONString(new long[0], writer);
        assertEquals("[]", writer.toString());

        writer = new StringWriter();
        JsonValue.writeJSONString(new long[] {12}, writer);
        assertEquals("[12]", writer.toString());

        writer = new StringWriter();
        JsonValue.writeJSONString(new long[] {-7, 22, 86, -99}, writer);
        assertEquals("[-7,22,86,-99]", writer.toString());
    }

    @Test
    public void testFloatArrayToString() throws IOException {
        assertEquals("null", JsonValue.toJSONString((float[]) null));
        assertEquals("[]", JsonValue.toJSONString(new float[0]));
        assertEquals("[12.8]", JsonValue.toJSONString(new float[] {12.8f}));
        assertEquals(
                "[-7.1,22.234,86.7,-99.02]",
                JsonValue.toJSONString(new float[] {-7.1f, 22.234f, 86.7f, -99.02f}));

        StringWriter writer;

        writer = new StringWriter();
        JsonValue.writeJSONString((float[]) null, writer);
        assertEquals("null", writer.toString());

        writer = new StringWriter();
        JsonValue.writeJSONString(new float[0], writer);
        assertEquals("[]", writer.toString());

        writer = new StringWriter();
        JsonValue.writeJSONString(new float[] {12.8f}, writer);
        assertEquals("[12.8]", writer.toString());

        writer = new StringWriter();
        JsonValue.writeJSONString(new float[] {-7.1f, 22.234f, 86.7f, -99.02f}, writer);
        assertEquals("[-7.1,22.234,86.7,-99.02]", writer.toString());
    }

    @Test
    public void testDoubleArrayToString() throws IOException {
        assertEquals("null", JsonValue.toJSONString((double[]) null));
        assertEquals("[]", JsonValue.toJSONString(new double[0]));
        assertEquals("[12.8]", JsonValue.toJSONString(new double[] {12.8}));
        assertEquals(
                "[-7.1,22.234,86.7,-99.02]",
                JsonValue.toJSONString(new double[] {-7.1, 22.234, 86.7, -99.02}));

        StringWriter writer;

        writer = new StringWriter();
        JsonValue.writeJSONString((double[]) null, writer);
        assertEquals("null", writer.toString());

        writer = new StringWriter();
        JsonValue.writeJSONString(new double[0], writer);
        assertEquals("[]", writer.toString());

        writer = new StringWriter();
        JsonValue.writeJSONString(new double[] {12.8}, writer);
        assertEquals("[12.8]", writer.toString());

        writer = new StringWriter();
        JsonValue.writeJSONString(new double[] {-7.1, 22.234, 86.7, -99.02}, writer);
        assertEquals("[-7.1,22.234,86.7,-99.02]", writer.toString());
    }

    @Test
    public void testBooleanArrayToString() throws IOException {
        assertEquals("null", JsonValue.toJSONString((boolean[]) null));
        assertEquals("[]", JsonValue.toJSONString(new boolean[0]));
        assertEquals("[true]", JsonValue.toJSONString(new boolean[] {true}));
        assertEquals(
                "[true,false,true]", JsonValue.toJSONString(new boolean[] {true, false, true}));

        StringWriter writer;

        writer = new StringWriter();
        JsonValue.writeJSONString((boolean[]) null, writer);
        assertEquals("null", writer.toString());

        writer = new StringWriter();
        JsonValue.writeJSONString(new boolean[0], writer);
        assertEquals("[]", writer.toString());

        writer = new StringWriter();
        JsonValue.writeJSONString(new boolean[] {true}, writer);
        assertEquals("[true]", writer.toString());

        writer = new StringWriter();
        JsonValue.writeJSONString(new boolean[] {true, false, true}, writer);
        assertEquals("[true,false,true]", writer.toString());
    }

    @Test
    public void testCharArrayToString() throws IOException {
        assertEquals("null", JsonValue.toJSONString((char[]) null));
        assertEquals("[]", JsonValue.toJSONString(new char[0]));
        assertEquals("[\"a\"]", JsonValue.toJSONString(new char[] {'a'}));
        assertEquals("[\"a\",\"b\",\"c\"]", JsonValue.toJSONString(new char[] {'a', 'b', 'c'}));

        StringWriter writer;

        writer = new StringWriter();
        JsonValue.writeJSONString((char[]) null, writer);
        assertEquals("null", writer.toString());

        writer = new StringWriter();
        JsonValue.writeJSONString(new char[0], writer);
        assertEquals("[]", writer.toString());

        writer = new StringWriter();
        JsonValue.writeJSONString(new char[] {'a'}, writer);
        assertEquals("[\"a\"]", writer.toString());

        writer = new StringWriter();
        JsonValue.writeJSONString(new char[] {'a', 'b', 'c'}, writer);
        assertEquals("[\"a\",\"b\",\"c\"]", writer.toString());
    }

    @Test
    public void testObjectArrayToString() throws IOException {
        assertEquals("null", JsonValue.toJSONString((Object[]) null));
        assertEquals("[]", JsonValue.toJSONString(new Object[0]));
        assertEquals("[\"Hello\"]", JsonValue.toJSONString(new Object[] {"Hello"}));
        assertEquals(
                "[\"Hello\",12,[1,2,3]]",
                JsonValue.toJSONString(
                        new Object[] {"Hello", new Integer(12), new int[] {1, 2, 3}}));

        StringWriter writer;

        writer = new StringWriter();
        JsonValue.writeJSONString((Object[]) null, writer);
        assertEquals("null", writer.toString());

        writer = new StringWriter();
        JsonValue.writeJSONString(new Object[0], writer);
        assertEquals("[]", writer.toString());

        writer = new StringWriter();
        JsonValue.writeJSONString(new Object[] {"Hello"}, writer);
        assertEquals("[\"Hello\"]", writer.toString());

        writer = new StringWriter();
        JsonValue.writeJSONString(
                new Object[] {"Hello", new Integer(12), new int[] {1, 2, 3}}, writer);
        assertEquals("[\"Hello\",12,[1,2,3]]", writer.toString());
    }

    @Test
    public void testArraysOfArrays() throws IOException {

        StringWriter writer;

        final int[][][] nestedIntArray = new int[][][] {{{1}, {5}}, {{2}, {6}}};
        final String expectedNestedIntString = "[[[1],[5]],[[2],[6]]]";

        assertEquals(expectedNestedIntString, JsonValue.toJSONString(nestedIntArray));

        writer = new StringWriter();
        JsonValue.writeJSONString(nestedIntArray, writer);
        assertEquals(expectedNestedIntString, writer.toString());

        final String[][] nestedStringArray = new String[][] {{"a", "b"}, {"c", "d"}};
        final String expectedNestedStringString = "[[\"a\",\"b\"],[\"c\",\"d\"]]";

        assertEquals(expectedNestedStringString, JsonValue.toJSONString(nestedStringArray));

        writer = new StringWriter();
        JsonValue.writeJSONString(nestedStringArray, writer);
        assertEquals(expectedNestedStringString, writer.toString());
    }

    private void assertEquals(Object expected, Object val) {
        assertThat(val, equalTo(expected));
    }
}
