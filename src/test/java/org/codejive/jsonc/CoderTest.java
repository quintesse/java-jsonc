package org.codejive.jsonc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.codejive.jsonc.parser.ContentHandler;
import org.codejive.jsonc.parser.JsonParseException;
import org.codejive.jsonc.parser.JsonParser;
import org.codejive.jsonc.parser.JsonParserConfig;
import org.codejive.jsonc.parser.TypeFactory;
import org.junit.jupiter.api.Test;

public class CoderTest {

    @Test
    public void testTypes() throws Exception {
        String s = "[\"aap\", 42, true, null]";
        JsonArray array = (JsonArray) Jsonc.parseWithException(s);
        assertThat(
                array,
                contains(Jsonc.string("aap"), Jsonc.number(42L), Jsonc.bool(true), Jsonc.nil()));
    }

    @Test
    public void testDecodeNested() throws Exception {
        System.out.println("=======decode=======");

        String s = "[0,{\"1\":{\"2\":{\"3\":{\"4\":[5,{\"6\":7}]}}}}]";
        Object obj = Jsonc.parseWithException(s);
        JsonArray array = (JsonArray) obj;
        System.out.println("======the 2nd element of array======");
        System.out.println(array.get(1));
        System.out.println();
        assertEquals("{\"1\":{\"2\":{\"3\":{\"4\":[5,{\"6\":7}]}}}}", array.get(1).toString());

        JsonObject obj2 = (JsonObject) array.get(1);
        System.out.println("======field \"1\"==========");
        System.out.println(obj2.get("1"));
        assertEquals("{\"2\":{\"3\":{\"4\":[5,{\"6\":7}]}}}", obj2.get("1").toString());
    }

    @Test
    public void testDecodeEmpty() throws Exception {
        String s = "{}";
        Object obj = Jsonc.parseWithException(s);
        assertEquals(s, obj.toString());
    }

    @Test
    public void testDecodeExtraArrayComma() throws Exception {
        String s = "[5,]";
        Object obj = (new JsonParser(JsonParserConfig.lenientJson())).parse(s);
        assertEquals("[5,]", obj.toString());
        assertThrows(
                JsonParseException.class,
                () -> {
                    (new JsonParser(JsonParserConfig.strictJson())).parse(s);
                });
    }

    @Test
    public void testDecodeMissingArrayValue() throws Exception {
        String s = "[5,,2]";
        JsonArray obj = (JsonArray) (new JsonParser(JsonParserConfig.lenientJson())).parse(s);
        assertEquals(s, obj.toJSONString());
        assertThrows(
                JsonParseException.class,
                () -> {
                    (new JsonParser(JsonParserConfig.strictJson())).parse(s);
                });
    }

    @Test
    public void testDecodeEscapeSequences() throws Exception {
        String s = "[\"hello\\bworld\\\"abc\\tdef\\\\ghi\\rjkl\\n123\\u4e2d\"]";
        JsonArray obj = (JsonArray) Jsonc.parseWithException(s);
        assertEquals(s, obj.toJSONString());
        assertEquals("hello\bworld\"abc\tdef\\ghi\rjkl\n123中", obj.get(0).toString());
    }

    @Test
    public void testDecodeIncompleteObject() {
        try {
            String s = "{\"name\":";
            Object obj = Jsonc.parseWithException(s);
        } catch (JsonParseException pe) {
            assertEquals(JsonParseException.ERROR_UNEXPECTED_TOKEN, pe.getErrorType());
            assertEquals(8L, pe.getPosition());
        }
    }

    @Test
    public void testDecodeMissingOjectValue() {
        try {
            String s = "{\"name\":}";
            Object obj = Jsonc.parseWithException(s);
        } catch (JsonParseException pe) {
            assertEquals(JsonParseException.ERROR_UNEXPECTED_TOKEN, pe.getErrorType());
            assertEquals(8L, pe.getPosition());
        }
    }

    @Test
    public void testDecodeIncompleteObject2() {
        try {
            String s = "{\"name";
            Object obj = Jsonc.parseWithException(s);
        } catch (JsonParseException pe) {
            assertEquals(JsonParseException.ERROR_UNEXPECTED_TOKEN, pe.getErrorType());
            assertEquals(6L, pe.getPosition());
        }
    }

    @Test
    public void testMissingObjectEntry() {
        try {
            String s = "{,\"name\":\"value\"}";
            Object obj = Jsonc.parseWithException(s);
        } catch (JsonParseException pe) {
            assertEquals(JsonParseException.ERROR_UNEXPECTED_TOKEN, pe.getErrorType());
            assertEquals(1L, pe.getPosition());
        }
    }

    @Test
    public void testDecodeMismatchedBrackets() {
        try {
            String s = "[[null, 123.45, \"a \tb c\"}, true]";
            Object obj = Jsonc.parseWithException(s);
        } catch (JsonParseException pe) {
            assertEquals(24L, pe.getPosition());
            System.out.println(pe.getMessage());
        }
    }

    @Test
    public void testDecodeWithContainerFactory() throws Exception {
        String s = "{\"first\": 123, \"second\": [4, 5, 6], \"third\": 78.9}";
        TypeFactory typeFactory =
                new TypeFactory() {
                    @Override
                    public List createArrayContainer() {
                        return new LinkedList();
                    }

                    @Override
                    public Map createObjectContainer() {
                        return new LinkedHashMap();
                    }

                    @Override
                    public Object createPrimitive(
                            JsonPrimitive.Type type, String value, String rawValue) {
                        switch (type) {
                            case INTEGER:
                                return Long.parseLong(value);
                            case REAL:
                                return Double.parseDouble(value);
                            case BOOLEAN:
                                return Boolean.parseBoolean(value);
                        }
                        return value;
                    }
                };

        try {
            JsonParser parser = new JsonParser();
            Map json = (Map) parser.parse(s, typeFactory);
            Iterator iter = json.entrySet().iterator();
            System.out.println("==iterate result==");
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                System.out.println(entry.getKey() + "=>" + entry.getValue());
            }

            System.out.println("==toJSONString()==");
            System.out.println(Jsonc.toJSONString(json));
            assertEquals(
                    "{\"first\":123,\"second\":[4,5,6],\"third\":78.9}", Jsonc.toJSONString(json));
        } catch (JsonParseException pe) {
            pe.printStackTrace();
        }
    }

    @Test
    public void testDecodeWithContentHandler() throws Exception {
        String s =
                "{\"first\": 123, \"second\": [{\"s1\":{\"s11\":\"v11\"}}, 4, 5, 6], \"third\": 789}";
        ContentHandler myHandler =
                new ContentHandler() {

                    public boolean endArray(Status status) throws JsonParseException {
                        System.out.println("endArray()");
                        return true;
                    }

                    public void endJSON() throws JsonParseException {
                        System.out.println("endJSON()");
                    }

                    public boolean endObject(Status status) throws JsonParseException {
                        System.out.println("endObject()");
                        return true;
                    }

                    public boolean endObjectEntry() throws JsonParseException {
                        System.out.println("endObjectEntry()");
                        return true;
                    }

                    @Override
                    public boolean primitive(
                            Status status, JsonPrimitive.Type type, String value, String rawValue)
                            throws JsonParseException, IOException {
                        System.out.println("primitive(" + type + ") : " + value);
                        return true;
                    }

                    public boolean startArray(Status status) throws JsonParseException {
                        System.out.println("startArray()");
                        return true;
                    }

                    public void startJSON() throws JsonParseException {
                        System.out.println("startJSON()");
                    }

                    public boolean startObject(Status status) throws JsonParseException {
                        System.out.println("startObject()");
                        return true;
                    }

                    public boolean startObjectEntry(String key) throws JsonParseException {
                        System.out.println("startObjectEntry(), key:" + key);
                        return true;
                    }
                };
        try {
            JsonParser parser = new JsonParser();
            parser.parse(s, myHandler);
        } catch (JsonParseException pe) {
            pe.printStackTrace();
        }
    }

    @Test
    public void testDecodeWithKeyFinder() throws Exception {
        class KeyFinder implements ContentHandler {
            private Object value;
            private boolean found = false;
            private boolean end = false;
            private String key;
            private String matchKey;

            public void setMatchKey(String matchKey) {
                this.matchKey = matchKey;
            }

            public Object getValue() {
                return value;
            }

            public boolean isEnd() {
                return end;
            }

            public void setFound(boolean found) {
                this.found = found;
            }

            public boolean isFound() {
                return found;
            }

            public void startJSON() {
                found = false;
                end = false;
            }

            public void endJSON() {
                end = true;
            }

            public boolean primitive(
                    Status status, JsonPrimitive.Type type, String value, String rawValue) {
                if (key != null) {
                    if (key.equals(matchKey)) {
                        found = true;
                        this.value = value;
                        key = null;
                        return false;
                    }
                }
                return true;
            }

            public boolean startArray(Status status) {
                return true;
            }

            public boolean startObject(Status status) {
                return true;
            }

            public boolean startObjectEntry(String key) {
                this.key = key;
                return true;
            }

            public boolean endArray(Status status) {
                return false;
            }

            public boolean endObject(Status status) {
                return true;
            }

            public boolean endObjectEntry() {
                return true;
            }
        }
        ;

        KeyFinder keyFinder = new KeyFinder();
        keyFinder.setMatchKey("id");
        int i = 0;
        try {
            while (!keyFinder.isEnd()) {
                JsonParser parser = new JsonParser();
                String s =
                        "{\"first\": 123, \"second\": [{\"k1\":{\"id\":\"id1\"}}, 4, 5, 6, {\"id\": 123}], \"third\": 789, \"id\": null}";
                parser.parse(s, keyFinder, true);
                if (keyFinder.isFound()) {
                    i++;
                    keyFinder.setFound(false);
                    System.out.println("found id:");
                    System.out.println(keyFinder.getValue());
                    if (i == 1) assertEquals("id1", keyFinder.getValue());
                    if (i == 2) {
                        assertTrue(keyFinder.getValue() instanceof Number);
                        assertEquals("123", String.valueOf(keyFinder.getValue()));
                    }
                    if (i == 3) assertTrue(null == keyFinder.getValue());
                }
            }
        } catch (JsonParseException pe) {
            pe.printStackTrace();
        }
    }

    @Test
    public void testEncode() throws Exception {
        System.out.println("=======encode=======");

        JsonArray array1 = new JsonArray();
        array1.add("abc\u0010a/");
        array1.add(new Integer(123));
        array1.add(new Double(222.123));
        array1.add(new Boolean(true));
        System.out.println("======array1==========");
        System.out.println(array1);
        System.out.println();
        assertEquals("[\"abc\\u0010a\\/\",123,222.123,true]", array1.toString());

        JsonObject obj1 = new JsonObject();
        obj1.put("array1", array1);
        System.out.println("======obj1 with array1===========");
        System.out.println(obj1);
        System.out.println();
        assertEquals("{\"array1\":[\"abc\\u0010a\\/\",123,222.123,true]}", obj1.toString());

        obj1.remove("array1");
        array1.add(obj1);
        System.out.println("======array1 with obj1========");
        System.out.println(array1);
        System.out.println();
        assertEquals("[\"abc\\u0010a\\/\",123,222.123,true,{}]", array1.toString());

        List list = new ArrayList();
        list.add("abc\u0010a/");
        list.add(new Integer(123));
        list.add(new Double(222.123));
        list.add(new Boolean(true));
        list.add(null);
        System.out.println("======list==========");
        System.out.println(Jsonc.toJSONString(list));
        System.out.println();
        assertEquals("[\"abc\\u0010a\\/\",123,222.123,true,null]", Jsonc.toJSONString(list));

        Map map = new HashMap();
        map.put("array1", list);
        System.out.println("======map with list===========");
        System.out.println(map);
        System.out.println();
        assertEquals(
                "{\"array1\":[\"abc\\u0010a\\/\",123,222.123,true,null]}", Jsonc.toJSONString(map));

        Map m1 = new LinkedHashMap();
        Map m2 = new LinkedHashMap();
        List l1 = new LinkedList();

        m1.put("k11", "v11");
        m1.put("k12", "v12");
        m1.put("k13", "v13");
        m2.put("k21", "v21");
        m2.put("k22", "v22");
        m2.put("k23", "v23");
        l1.add(m1);
        l1.add(m2);
        String jsonString = Jsonc.toJSONString(l1);
        System.out.println(jsonString);
        assertEquals(
                "[{\"k11\":\"v11\",\"k12\":\"v12\",\"k13\":\"v13\"},{\"k21\":\"v21\",\"k22\":\"v22\",\"k23\":\"v23\"}]",
                jsonString);

        StringWriter out = new StringWriter();
        Jsonc.writeJSONString(l1, out, true);
        jsonString = out.toString();
        System.out.println(jsonString);
        assertEquals(
                "[{\"k11\":\"v11\",\"k12\":\"v12\",\"k13\":\"v13\"},{\"k21\":\"v21\",\"k22\":\"v22\",\"k23\":\"v23\"}]",
                jsonString);

        List l2 = new LinkedList();
        Map m3 = new LinkedHashMap();
        m3.put("k31", "v3");
        m3.put("k32", new Double(123.45));
        m3.put("k33", new Boolean(false));
        m3.put("k34", null);
        l2.add("vvv");
        l2.add("1.23456789123456789");
        l2.add(new Boolean(true));
        l2.add(null);
        m3.put("k35", l2);
        m1.put("k14", m3);
        out = new StringWriter();
        Jsonc.writeJSONString(l1, out, true);
        jsonString = out.toString();
        System.out.println(jsonString);
        assertEquals(
                "[{\"k11\":\"v11\",\"k12\":\"v12\",\"k13\":\"v13\",\"k14\":{\"k31\":\"v3\",\"k32\":123.45,\"k33\":false,\"k34\":null,\"k35\":[\"vvv\",\"1.23456789123456789\",true,null]}},{\"k21\":\"v21\",\"k22\":\"v22\",\"k23\":\"v23\"}]",
                jsonString);
    }

    private void assertEquals(Object expected, Object val) {
        assertThat(val, equalTo(expected));
    }
}
