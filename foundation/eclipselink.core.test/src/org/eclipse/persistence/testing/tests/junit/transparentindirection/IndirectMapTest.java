/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.junit.transparentindirection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.eclipse.persistence.descriptors.changetracking.CollectionChangeEvent;
import org.eclipse.persistence.descriptors.changetracking.MapChangeEvent;
import org.eclipse.persistence.indirection.IndirectCollectionsFactory;
import org.eclipse.persistence.indirection.IndirectMap;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.indirection.QueryBasedValueHolder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.testing.tests.transparentindirection.ZTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test a simple IndirectMap.
 * @author: Big Country
 */
@RunWith(Parameterized.class)
public class IndirectMapTest {

    public static final class M<K, V> extends IndirectMap<K, V> {}

    private Hashtable<String, String> map;
    private IndirectMap<String, String> testMap;
    private Listener testMapLsn;
    private Class<? extends IndirectMap> cls;
    private boolean useListener;

    @Parameters(name = "{0}, {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                 { IndirectMap.class, true }, { IndirectMap.class, false }, { M.class, true }, { M.class, false }
           });
    }

    public IndirectMapTest(Class<? extends IndirectMap> cls, boolean useListener) {
        this.cls = cls;
        this.useListener = useListener;
    }

    /**
     * Assert that the elements in two dictionaries are equal. If they are not,
     * throw an AssertionFailedError. Order of the elements is significant.
     * @param message the error message
     * @param expected the expected value of a Map
     * @param actual the actual value of a Map
     */
    protected void assertElementsEqual(String message, Map expected, Map actual) {
        if (expected == actual) {
            return;
        }
        if (expected.size() != actual.size()) {
            assertTrue(ZTestCase.notEqualsMessage(message, expected, actual), false);
        }
        for (Iterator stream = expected.keySet().iterator(); stream.hasNext();) {
            Object key = stream.next();
            if (!expected.get(key).equals(actual.get(key))) {
                assertTrue(ZTestCase.notEqualsMessage(message, expected, actual), false);
            }
        }
    }

    /**
     * Assert that the elements in two dictionaries are equal. If they are not,
     * throw an AssertionFailedError.
     * @param expected the expected value of a Map
     * @param actual the actual value of a Map
     */
    protected void assertElementsEqual(Map expected, Map actual) {
        assertElementsEqual("", expected, actual);
    }

    /**
     * set up the test fixture:
     * 1. an IndirectMap based on a Hashtable
     */
    @Before
    public void setUp() {
        map = this.setUpMap();
        Object temp = new Hashtable(map);

        ValueHolderInterface vh = new QueryBasedValueHolder(new ReadAllQuery(), new DatabaseRecord(), new TestSession(temp));
        if (cls == null) {
            testMap = IndirectCollectionsFactory.createIndirectMap();
        } else {
            try {
                testMap = cls.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        testMap.setValueHolder(vh);
        if (useListener) {
            testMapLsn = new Listener();
            testMap._persistence_setPropertyChangeListener(testMapLsn);
        }
    }

    protected Hashtable<String, String> setUpMap() {
        Hashtable<String, String> result = new Hashtable<>();
        result.put("zero", "000");
        result.put("one", "111");
        result.put("two", "222");
        result.put("three", "333");
        result.put("four", "444");
        result.put("five", "555");
        result.put("six", "666");
        result.put("seven", "777");
        result.put("eight", "888");
        result.put("nine", "999");
        return result;
    }

    /**
     * nothing for now...
     */
    @After
    public void tearDown() {
        if (useListener) {
            testMapLsn.events.clear();
        }
    }

    @Test
    public void testClear() {
        int originalSize = testMap.size();
        map.clear();
        testMap.clear();

        assertEquals(map, testMap);
        assertTrue(testMap.isEmpty());
        assertRemoveEvents(originalSize);
    }

    @Test
    public void testContains() {
        assertTrue(testMap.contains(map.elements().nextElement()));
        assertNoEvents();
    }

    @Test
    public void testContainsKey() {
        assertTrue(testMap.containsKey(map.keys().nextElement()));
        assertNoEvents();
    }

    @Test
    public void testContainsValue() {
        assertTrue(testMap.containsValue(map.elements().nextElement()));
        assertNoEvents();
    }

    @Test
    public void testElements() {
        assertTrue("Map Does not contain elements from test", map.contains(testMap.elements().nextElement()));
        assertNoEvents();
    }

    @Test
    public void testEntrySet() {
        assertEquals(map.entrySet(), testMap.entrySet());
        assertNoEvents();
    }

    @Test
    public void testEntrySetAdd() {
        Set<Map.Entry<String, String>> entrySet = map.entrySet();
        Set<Map.Entry<String, String>> testEntrySet = testMap.entrySet();
        UnsupportedOperationException uoe = null;
        try {
            entrySet.add(new MapEntry("sampleKey", "sampleValue"));
        } catch (UnsupportedOperationException ex) {
            uoe = ex;
        }
        assertNotNull(uoe);
        try {
            testEntrySet.add(new MapEntry("sampleKey", "sampleValue"));
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            assertEquals(uoe.getClass(), ex.getClass());
            assertEquals(uoe.getMessage(), ex.getMessage());
        }
        assertEquals(map, testMap);
        assertNoEvents();
    }

    @Test
    public void testEntrySetAddAll() {
        Collection<Map.Entry<String, String>> collection = new HashSet<>();
        collection.add(new MapEntry("sampleKey1", "sampleValue1"));
        collection.add(new MapEntry("sampleKey2", "sampleValue2"));
        Set<Map.Entry<String, String>> entrySet = map.entrySet();
        Set<Map.Entry<String, String>> testEntrySet = testMap.entrySet();
        UnsupportedOperationException uoe = null;
        try {
            entrySet.addAll(collection);
        } catch (UnsupportedOperationException ex) {
            uoe = ex;
        }
        assertNotNull(uoe);
        try {
            testEntrySet.addAll(collection);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            assertEquals(uoe.getClass(), ex.getClass());
            assertEquals(uoe.getMessage(), ex.getMessage());
        }
        assertEquals(map, testMap);
        assertNoEvents();
    }

    @Test
    public void testEntrySetClear() {
        Set<Map.Entry<String, String>> entrySet = map.entrySet();
        Set<Map.Entry<String, String>> testEntrySet = testMap.entrySet();
        entrySet.clear();
        testEntrySet.clear();
        assertEquals(map, testMap);
        assertNull(testMap.get("one"));
        assertRemoveEvents(10);
    }

    @Test
    public void testEntrySetSize() {
        Set<Map.Entry<String, String>> entrySet = map.entrySet();
        Set<Map.Entry<String, String>> testEntrySet = testMap.entrySet();
        assertEquals(map, testMap);
        assertEquals(entrySet.size(), testEntrySet.size());
        assertNoEvents();
    }

    @Test
    public void testEntrySetRemove() {
        Set<Map.Entry<String, String>> entrySet = map.entrySet();
        Set<Map.Entry<String, String>> testEntrySet = testMap.entrySet();
        Map.Entry<String, String> toRemove = null;
        Iterator<Entry<String, String>> iterator = entrySet.iterator();
        while (iterator.hasNext()) {
            Entry<String, String> next = iterator.next();
            if ("one".equals(next.getKey())) {
                toRemove = next;
                break;
            }
        }
        boolean result1 = entrySet.remove(toRemove);
        boolean result2 = testEntrySet.remove(toRemove);
        assertEquals(map, testMap);
        assertEquals(result1, result2);
        assertNull(testMap.get("one"));
        assertRemoveEvents(1);
    }

    @Test
    public void testEntrySetRemoveAll() {
        Set<Map.Entry<String, String>> entrySet = map.entrySet();
        Set<Map.Entry<String, String>> testEntrySet = testMap.entrySet();
        Collection<Map.Entry<String, String>> toRemove = new HashSet<>();
        Iterator<Entry<String, String>> iterator = entrySet.iterator();
        while (iterator.hasNext()) {
            Entry<String, String> next = iterator.next();
            if ("one".equals(next.getKey()) || "two".equals(next.getKey())) {
                toRemove.add(next);
            }
            if (toRemove.size() == 2) {
                break;
            }
        }
        boolean result1 = entrySet.removeAll(toRemove);
        boolean result2 = testEntrySet.removeAll(toRemove);
        assertEquals(map, testMap);
        assertEquals(result1, result2);
        assertNull(testMap.get("one"));
        assertRemoveEvents(2);
    }

    @Test
    public void testEntrySetRemoveIf() {
        Set<Map.Entry<String, String>> entrySet = map.entrySet();
        Set<Map.Entry<String, String>> testEntrySet = testMap.entrySet();
        boolean result1 = entrySet.removeIf(entry -> "one".equals(entry.getKey()));
        boolean result2 = testEntrySet.removeIf(entry -> "one".equals(entry.getKey()));
        assertEquals(map, testMap);
        assertEquals(result1, result2);
        assertNull(testMap.get("one"));
        assertRemoveEvents(1);
    }

    @Test
    public void testEntrySetRetainAll() {
        Set<Map.Entry<String, String>> entrySet = map.entrySet();
        Set<Map.Entry<String, String>> testEntrySet = testMap.entrySet();
        Collection<Map.Entry<String, String>> toRetain = new HashSet<>();
        Iterator<Entry<String, String>> iterator = entrySet.iterator();
        while (iterator.hasNext()) {
            Entry<String, String> next = iterator.next();
            if ("one".equals(next.getKey()) || "two".equals(next.getKey())) {
                toRetain.add(next);
            }
            if (toRetain.size() == 2) {
                break;
            }
        }
        boolean result1 = entrySet.retainAll(toRetain);
        boolean result2 = testEntrySet.retainAll(toRetain);
        assertEquals(map, testMap);
        assertNotNull(testMap.get("one"));
        assertEquals(result1, result2);
        assertNull(testMap.get("six"));
        assertRemoveEvents(8);
    }

    @Test
    public void testEquals() {
        assertFalse(testMap == map);
        assertTrue(testMap.equals(map));
        assertNoEvents();
    }

    @Test
    public void testGet() {
        assertEquals(map.get("six"), testMap.get("six"));
        assertNoEvents();
    }

    @Test
    public void testHashCode() {
        assertEquals(map.hashCode(), testMap.hashCode());
        assertNoEvents();
    }

    @Test
    public void testIsEmpty() {
        assertTrue(!testMap.isEmpty());
        assertNoEvents();
    }

    @Test
    public void testKeys() {
        assertFalse("Map Does not contain keys from test", map.contains(testMap.keys().nextElement()));
        assertNoEvents();
    }

    @Test
    public void testKeySet() {
        assertEquals(map.entrySet(), testMap.entrySet());
        assertNoEvents();
    }

    @Test
    public void testKeySetAdd() {
        Set<String> keySet = map.keySet();
        Set<String> testKeySet = testMap.keySet();
        UnsupportedOperationException uoe = null;
        try {
            keySet.add("newKey");
        } catch (UnsupportedOperationException ex) {
            uoe = ex;
        }
        assertNotNull(uoe);
        try {
            testKeySet.add("newKey");
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            assertEquals(uoe.getClass(), ex.getClass());
            assertEquals(uoe.getMessage(), ex.getMessage());
        }
        assertEquals(map, testMap);
        assertNoEvents();
    }

    @Test
    public void testKeySetAddAll() {
        Collection<String> collection = new HashSet<>();
        collection.add("sampleKey1");
        collection.add("sampleKey2");
        Set<String> keySet = map.keySet();
        Set<String> testKeySet = testMap.keySet();
        UnsupportedOperationException uoe = null;
        try {
            keySet.addAll(collection);
        } catch (UnsupportedOperationException ex) {
            uoe = ex;
        }
        assertNotNull(uoe);
        try {
            testKeySet.addAll(collection);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            assertEquals(uoe.getClass(), ex.getClass());
            assertEquals(uoe.getMessage(), ex.getMessage());
        }
        assertEquals(map, testMap);
        assertNoEvents();
    }

    @Test
    public void testKeySetClear() {
        Set<String> keySet = map.keySet();
        Set<String> testKeySet = testMap.keySet();
        keySet.clear();
        testKeySet.clear();
        assertEquals(map, testMap);
        assertNull(testMap.get("one"));
        assertRemoveEvents(10);
    }

    @Test
    public void testKeySetSize() {
        Set<String> keySet = map.keySet();
        Set<String> testKeySet = testMap.keySet();
        assertEquals(map, testMap);
        assertEquals(keySet.size(), testKeySet.size());
        assertNoEvents();
    }

    @Test
    public void testKeySetRemove() {
        Set<String> keySet = map.keySet();
        Set<String> testKeySet = testMap.keySet();
        boolean result1 = keySet.remove("one");
        boolean result2 = testKeySet.remove("one");
        assertEquals(map, testMap);
        assertEquals(result1, result2);
        assertNull(testMap.get("one"));
        assertRemoveEvents(1);
    }

    @Test
    public void testKeySetRemoveAll() {
        Set<String> keySet = map.keySet();
        Set<String> testKeySet = testMap.keySet();
        Collection<String> toRemove = new HashSet<>();
        toRemove.add("one");
        toRemove.add("two");
        boolean result1 = keySet.removeAll(toRemove);
        boolean result2 = testKeySet.removeAll(toRemove);
        assertEquals(map, testMap);
        assertEquals(result1, result2);
        assertNull(testMap.get("one"));
        assertRemoveEvents(2);
    }

    @Test
    public void testKeySetRemoveIf() {
        // remove 2 items - 'six' and 'seven'
        Set<String> keySet = map.keySet();
        Set<String> testKeySet = testMap.keySet();
        boolean result1 = keySet.removeIf(key -> key.startsWith("s"));
        boolean result2 = testKeySet.removeIf(key -> key.startsWith("s"));
        assertEquals(map, testMap);
        assertEquals(result1, result2);
        assertNull(testMap.get("six"));
        assertRemoveEvents(2);
    }

    @Test
    public void testKeySetRetainAll() {
        Set<String> keySet = map.keySet();
        Set<String> testKeySet = testMap.keySet();
        Collection<String> toRetain = new HashSet<>();
        toRetain.add("one");
        toRetain.add("two");
        boolean result1 = keySet.retainAll(toRetain);
        boolean result2 = testKeySet.retainAll(toRetain);
        assertEquals(map, testMap);
        assertNotNull(testMap.get("one"));
        assertEquals(result1, result2);
        assertNull(testMap.get("six"));
        assertRemoveEvents(8);
    }

    @Test
    public void testPut() {
        String key = "foo";
        String value = "bar";
        String result1 = map.put(key, value);
        String result2 = testMap.put(key, value);

        assertEquals(map, testMap);
        assertTrue(testMap.containsKey(key));
        assertTrue(testMap.contains(value));
        assertEquals(result1, result2);
        assertAddEvents(1);
    }

    @Test
    public void testPutAll() {
        Hashtable<String, String> temp = new Hashtable<>();
        temp.put("foo", "bar");
        temp.put("sna", "fu");

        map.putAll(temp);
        testMap.putAll(temp);
        assertEquals(map, testMap);
        for (Enumeration stream = temp.keys(); stream.hasMoreElements();) {
            assertTrue(testMap.containsKey(stream.nextElement()));
        }
        for (Enumeration stream = temp.elements(); stream.hasMoreElements();) {
            assertTrue(testMap.contains(stream.nextElement()));
        }
        assertAddEvents(2);
    }

    @Test
    public void testRemove() {
        String temp = "one";
        String result1 = map.remove(temp);
        String result2 = testMap.remove(temp);
        assertEquals(map, testMap);
        assertEquals(result1, result2);
        assertNotNull(result2);
        assertTrue(!testMap.containsKey(temp));
        assertRemoveEvents(1);
    }

    @Test
    public void testSize() {
        assertEquals(map.size(), testMap.size());
        assertNoEvents();
    }

    @Test
    public void testValues() {
        assertEquals(map.size(), testMap.values().size());

        for (Iterator stream = testMap.values().iterator(); stream.hasNext();) {
            assertTrue(map.contains(stream.next()));
        }
        map.values().removeAll(testMap.values());
        assertTrue(map.values().isEmpty());
        assertNoEvents();
    }

    @Test
    public void testValuesAdd() {
        Collection<String> keySet = map.values();
        Collection<String> testKeySet = testMap.values();
        UnsupportedOperationException uoe = null;
        try {
            keySet.add("newValue");
        } catch (UnsupportedOperationException ex) {
            uoe = ex;
        }
        assertNotNull(uoe);
        try {
            testKeySet.add("newValue");
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            assertEquals(uoe.getClass(), ex.getClass());
            assertEquals(uoe.getMessage(), ex.getMessage());
        }
        assertEquals(map, testMap);
        assertNoEvents();
    }

    @Test
    public void testValuesAddAll() {
        Collection<String> collection = new HashSet<>();
        collection.add("newValue1");
        collection.add("newValue2");
        Collection<String> keySet = map.values();
        Collection<String> testKeySet = testMap.values();
        UnsupportedOperationException uoe = null;
        try {
            keySet.addAll(collection);
        } catch (UnsupportedOperationException ex) {
            uoe = ex;
        }
        assertNotNull(uoe);
        try {
            testKeySet.addAll(collection);
            fail("should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            assertEquals(uoe.getClass(), ex.getClass());
            assertEquals(uoe.getMessage(), ex.getMessage());
        }
        assertEquals(map, testMap);
        assertNoEvents();
    }

    @Test
    public void testValuesClear() {
        Collection<String> keySet = map.values();
        Collection<String> testKeySet = testMap.values();
        keySet.clear();
        testKeySet.clear();
        assertEquals(map, testMap);
        assertNull(testMap.get("one"));
        assertRemoveEvents(10);
    }

    @Test
    public void testValuesSize() {
        Collection<String> keySet = map.values();
        Collection<String> testKeySet = testMap.values();
        assertEquals(map, testMap);
        assertEquals(keySet.size(), testKeySet.size());
        assertNoEvents();
    }

    @Test
    public void testValuesRemove() {
        Collection<String> keySet = map.values();
        Collection<String> testKeySet = testMap.values();
        boolean result1 = keySet.remove("111");
        boolean result2 = testKeySet.remove("111");
        assertEquals(map, testMap);
        assertEquals(result1, result2);
        assertNull(testMap.get("one"));
        assertRemoveEvents(1);
    }

    @Test
    public void testValuesRemoveAll() {
        Collection<String> keySet = map.values();
        Collection<String> testKeySet = testMap.values();
        Collection<String> toRemove = new HashSet<>();
        toRemove.add("111");
        toRemove.add("222");
        boolean result1 = keySet.removeAll(toRemove);
        boolean result2 = testKeySet.removeAll(toRemove);
        assertEquals(map, testMap);
        assertEquals(result1, result2);
        assertNull(testMap.get("one"));
        assertRemoveEvents(2);
    }

    @Test
    public void testValuesRemoveIf() {
        // remove 2 items - 'six' and 'seven'
        Collection<String> keySet = map.values();
        Collection<String> testKeySet = testMap.values();
        boolean result1 = keySet.removeIf(key -> key.startsWith("2"));
        boolean result2 = testKeySet.removeIf(key -> key.startsWith("2"));
        assertEquals(map, testMap);
        assertEquals(result1, result2);
        assertNull(testMap.get("two"));
        assertRemoveEvents(1);
    }


    @Test
    public void testValueRetainAll() {
        Collection<String> keySet = map.values();
        Collection<String> testKeySet = testMap.values();
        Collection<String> toRetain = new HashSet<>();
        toRetain.add("111");
        toRetain.add("222");
        boolean result1 = keySet.retainAll(toRetain);
        boolean result2 = testKeySet.retainAll(toRetain);
        assertEquals(map, testMap);
        assertNotNull(testMap.get("one"));
        assertEquals(result1, result2);
        assertNull(testMap.get("six"));
        assertRemoveEvents(8);
    }

    //Java SE 8 API
    @Test
    public void testReplace() {
        assertEquals(map, testMap);
        Object o = testMap.replace("one", "1");
        assertEquals(map.replace("one", "1"), o);
        assertNotNull("Should return value", o);
        assertEquals("111", o);
        assertEquals("1", testMap.get("one"));
        assertEquals(map, testMap);
        assertRemoveAddEvents(1);

        assertNull(map.replace("blablablabla", "whatever"));
        assertNull(testMap.replace("blablablabla", "whatever"));
        assertEquals(map, testMap);
        assertRemoveAddEvents(1);
    }

    @Test
    public void testReplaceWithDefault() {
        assertEquals("111", testMap.get("one"));
        Object o = testMap.replace("one", "1", "1234");
        assertEquals(map.replace("one", "1", "1234"), o);
        assertFalse("Should return false", (boolean) o);
        assertFalse("Should not have '1234'", testMap.containsValue("1234"));
        assertEquals(map, testMap);
        assertNoEvents();

        assertEquals("111", testMap.get("one"));
        o = testMap.replace("one", "111", "1234");
        assertEquals(map.replace("one", "111", "1234"), o);
        assertTrue("Should return true", (boolean) o);
        assertEquals("1234", testMap.get("one"));
        assertEquals(map, testMap);
        assertRemoveAddEvents(1);
    }

    @Test
    public void testGetOrDefault() {
        assertFalse(testMap.containsKey("temp"));
        Object o = testMap.getOrDefault("temp", "1234");
        assertNotNull("Should return value", o);
        assertEquals("1234", o);

        assertTrue(testMap.containsKey("one"));
        o = testMap.getOrDefault("one", "5678");
        assertNotNull("Should return value", o);
        assertEquals("111", o);
        assertNoEvents();
    }

    @Test
    public void testPutIfAbsent() {
        assertFalse(testMap.containsKey("temp"));
        Object o = testMap.putIfAbsent("temp", "1234");
        assertNull("Should return null", o);
        assertAddEvents(1);

        assertTrue(testMap.containsKey("temp"));
        o = testMap.putIfAbsent("temp", "5678");
        assertNotNull("Should return value", o);
        assertEquals("1234", o);
        assertAddEvents(1);
    }

    @Test
    public void testRemoveTwoArgs() {
        assertEquals("111", testMap.get("one"));
        Object o = testMap.remove("one", "1234");
        assertFalse("Should return false", (boolean) o);
        assertNoEvents();

        assertEquals("111", testMap.get("one"));
        o = testMap.remove("one", "111");
        assertTrue("Should return true", (boolean) o);
        assertNull(testMap.get("one"));
        assertRemoveEvents(1);
    }

    @Test
    public void testForEach() {
        final Vector<String> v1 = new Vector<>();
        final Vector<String> v2 = new Vector<>();
        map.forEach((k, v) -> {v1.add(k);v1.add(v);});
        testMap.forEach((k, v) -> {v2.add(k);v2.add(v);});
        ZTestCase.assertUnorderedElementsEqual(v1, v2);
        assertEquals(map, testMap);
        assertNoEvents();
    }

    @Test
    public void testReplaceAll() {
        assertEquals(map, testMap);
        map.replaceAll((k,v) -> v += "sufix");
        testMap.replaceAll((k,v) -> v += "sufix");
        assertEquals(map, testMap);
        assertRemoveAddEvents(testMap.size());
    }

    @Test
    public void testCompute() {
        assertEquals(map, testMap);
        String s1 = map.compute("one", (k,v) -> v += "suffix");
        String s2 = testMap.compute("one", (k,v) -> v += "suffix");
        assertEquals(s1, s2);
        assertEquals(map, testMap);
        assertEquals("111suffix", testMap.get("one"));
        assertRemoveAddEvents(1);
    }

    @Test
    public void testCompute2() {
        assertEquals(map, testMap);
        String s1 = map.compute("notExist", (k,v) -> v += "suffix");
        String s2 = testMap.compute("notExist", (k,v) -> v += "suffix");
        assertEquals(s1, s2);
        assertEquals(map, testMap);
        assertEquals("nullsuffix", testMap.get("notExist"));
        assertAddEvents(1);
    }

    @Test
    public void testComputeIfPresent() {
        assertEquals(map, testMap);
        String s1 = map.computeIfPresent("one", (k,v) -> v += "suffix");
        String s2 = testMap.computeIfPresent("one", (k,v) -> v += "suffix");
        assertEquals(s1, s2);
        assertEquals(map, testMap);
        assertEquals("111suffix", testMap.get("one"));
        assertRemoveAddEvents(1);
    }

    @Test
    public void testComputeIfPresent2() {
        assertEquals(map, testMap);
        String s1 = map.computeIfPresent("notExist", (k,v) -> v += "suffix");
        String s2 = testMap.computeIfPresent("notExist", (k,v) -> v += "suffix");
        assertEquals(s1, s2);
        assertEquals(map, testMap);
        assertNull(testMap.get("notExist"));
        assertNoEvents();;
    }

    @Test
    public void testComputeIfAbsent() {
        assertEquals(map, testMap);
        String s1 = map.computeIfAbsent("one", k -> "newitem");
        String s2 = testMap.computeIfAbsent("one", k -> "newitem");
        assertEquals(s1, s2);
        assertEquals(map, testMap);
        assertEquals("111", testMap.get("one"));
        assertNoEvents();
    }

    @Test
    public void testComputeIfAbsent2() {
        assertEquals(map, testMap);
        String s1 = map.computeIfAbsent("notExist", k -> "newitem");
        String s2 = testMap.computeIfAbsent("notExist", k -> "newitem");
        assertEquals(s1, s2);
        assertEquals(map, testMap);
        assertAddEvents(1);
    }

    @Test
    public void testMerge() {
        assertEquals(map, testMap);
        String s1 = map.merge("one", "custom", String::concat);
        String s2 = testMap.merge("one", "custom", String::concat);
        assertEquals(s1, s2);
        assertEquals(map, testMap);
        assertEquals("111custom", testMap.get("one"));
        assertRemoveAddEvents(1);
    }

    @Test
    public void testMerge2() {
        assertEquals(map, testMap);
        String s1 = map.merge("notExist", "custom", String::concat);
        String s2 = testMap.merge("notExist", "custom", String::concat);
        assertEquals(s1, s2);
        assertEquals(map, testMap);
        assertEquals("111", testMap.get("one"));
        assertAddEvents(1);
    }

    private void assertNoEvents() {
        if (useListener) {
            assertTrue(testMapLsn.events.isEmpty());
        }
    }

    private void assertAddEvents(int count) {
        if (useListener) {
            assertEquals("events do not match", count, testMapLsn.events.size());
            for (MapChangeEvent cce : testMapLsn.events) {
                assertEquals("expected add event", CollectionChangeEvent.ADD, cce.getChangeType());
            }
        }
    }

    private void assertRemoveEvents(int count) {
        if (useListener) {
            assertEquals("events do not match", count, testMapLsn.events.size());
            for (MapChangeEvent cce : testMapLsn.events) {
                assertEquals("expected add event", CollectionChangeEvent.REMOVE, cce.getChangeType());
            }
        }
    }

    private void assertRemoveAddEvents(int count) {
        if (useListener) {
            int totalEvents = count * 2;
            assertEquals("events do not match", totalEvents, testMapLsn.events.size());
            for (int i = 0; i < totalEvents;) {
                MapChangeEvent removeEvent = testMapLsn.events.get(i++);
                MapChangeEvent addEvent = testMapLsn.events.get(i++);
                assertEquals("expected remove event", CollectionChangeEvent.REMOVE, removeEvent.getChangeType());
                assertEquals("expected add event", CollectionChangeEvent.ADD, addEvent.getChangeType());
                assertFalse("removed: '" + removeEvent.getNewValue() + "', new: '" + addEvent.getNewValue() + "'",
                        removeEvent.getNewValue().equals(addEvent.getNewValue()));
            }
        }
    }

    private static final class Listener implements PropertyChangeListener {
        private List<MapChangeEvent> events = new ArrayList<>();

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            events.add((MapChangeEvent)evt);
        }

    }

    private static final class MapEntry<K, V> implements Map.Entry<K, V> {

        private K key;
        private V value;

        public MapEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            this.value = value;
            return this.value;
        }

    }
}
