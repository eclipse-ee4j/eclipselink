package org.eclipse.persistence.json.bind.defaultmapping.collections;

import org.junit.Before;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Roman Grigoriadi
 */
public class ArrayTest {

    private Jsonb jsonb;

    @Before
    public void before() {
        jsonb = JsonbBuilder.create();
    }

    @Test
    public void testStringArray() {
        String[] stringArray = new String[3];
        stringArray[0] = "first";
        stringArray[1] = "second";
        stringArray[2] = "third";

        String expected = "[\"first\",\"second\",\"third\"]";

        assertEquals(expected, jsonb.toJson(stringArray));

        String[] result = jsonb.fromJson(expected, stringArray.getClass());
        assertEquals("first", result[0]);
        assertEquals("second", result[1]);
        assertEquals("third", result[2]);
    }

    @Test
    public void testObjectArrayOfObjects() {
        Object[] objectArray = new Object[2];
        objectArray[0] = new KeyValue("first");
        objectArray[1] = new KeyValue("second");

        String expected = "[{\"field\":\"first\"},{\"field\":\"second\"}]";

        assertEquals(expected, jsonb.toJson(objectArray));
        Object[] result = jsonb.fromJson(expected, objectArray.getClass());
        assertEquals(HashMap.class, result[0].getClass());
        assertEquals(HashMap.class, result[1].getClass());
        assertEquals("first", ((Map) result[0]).get("field"));
        assertEquals("second", ((Map) result[1]).get("field"));
    }

    @Test
    public void testListOfArrays() {
        List<String[]> listOfArrays = new ArrayList<>();
        String[] stringArray = new String[2];
        stringArray[0] = "first";
        stringArray[1] = "second";
        listOfArrays.add(stringArray);
        listOfArrays.add(stringArray);

        String expected = "[[\"first\",\"second\"],[\"first\",\"second\"]]";
        assertEquals(expected, jsonb.toJson(listOfArrays));
        List<String[]> result = jsonb.fromJson(expected, new ArrayList<String[]>() {}.getClass().getGenericSuperclass());
        assertEquals("first", result.get(0)[0]);
        assertEquals("second", result.get(0)[1]);
        assertEquals("first", result.get(1)[0]);
        assertEquals("second", result.get(1)[1]);
    }

    @Test
    public void testMultidimensionalArrays() {
        String[][] multi = new String[2][2];
        multi[0][0] = "[0],[0]";
        multi[0][1] = "[0],[1]";
        multi[1][0] = "[1],[0]";
        multi[1][1] = "[1],[1]";
        String expected = "[[\"[0],[0]\",\"[0],[1]\"],[\"[1],[0]\",\"[1],[1]\"]]";
        assertEquals(expected, jsonb.toJson(multi));
        String[][] result = jsonb.fromJson(expected, multi.getClass());
        assertEquals("[0],[0]", result[0][0]);
        assertEquals("[0],[1]", result[0][1]);
        assertEquals("[1],[0]", result[1][0]);
        assertEquals("[1],[1]", result[1][1]);
    }

    @Test
    public void testDeserializeJsonArrayIntoObject() {
        String json = "[\"first\",\"second\",\"third\"]";
        Object result = jsonb.fromJson(json, Object.class);
        assertTrue(result instanceof List);
        assertEquals("first", ((List)result).get(0));
        assertEquals("second", ((List)result).get(1));
        assertEquals("third", ((List)result).get(2));
    }

    @Test
    public void testDeserializeJsonObjectIntoListOfMaps() {
        String json = "[{\"first\":1,\"second\":10}]";
        Object result = jsonb.fromJson(json, Object.class);
        assertTrue(result instanceof List);
        assertEquals(BigDecimal.ONE, ((Map) ((List) result).get(0)).get("first"));
        assertEquals(BigDecimal.TEN, ((Map) ((List) result).get(0)).get("second"));
    }

    @Test
    public void testUnmarshallMapWithArrayValue() {
        Map<String, String[]> arrayValueMap = new HashMap<>();
        String[] strings = new String[2];
        strings[0] = "zero";
        strings[1] = "one";
        arrayValueMap.put("first", strings);
        String expected = "{\"first\":[\"zero\",\"one\"]}";
        assertEquals(expected, jsonb.toJson(arrayValueMap));
        Map<String, String[]> result = jsonb.fromJson(expected, new HashMap<String, String[]>() {}.getClass());
        assertEquals("zero", result.get("first")[0]);
        assertEquals("one", result.get("first")[1]);
    }

    @Test
    public void testArrayOfNulls() {
        String[] nulls = new String[2];
        String expected = "[null,null]";
        assertEquals(expected, jsonb.toJson(nulls));
        String[] result = jsonb.fromJson(expected, nulls.getClass());
        assertTrue(result.length == 2);
        assertNull(result[0]);
        assertNull(result[1]);
    }

    public static class KeyValue {
        public String field;

        public KeyValue(String field) {
            this.field = field;
        }
    }
}
