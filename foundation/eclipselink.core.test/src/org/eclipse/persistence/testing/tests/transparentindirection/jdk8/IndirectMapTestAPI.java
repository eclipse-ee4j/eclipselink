/*******************************************************************************
 * Copyright (c) 1998, 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.transparentindirection.jdk8;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
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

import junit.framework.TestSuite;

import org.eclipse.persistence.descriptors.changetracking.CollectionChangeEvent;
import org.eclipse.persistence.descriptors.changetracking.MapChangeEvent;
import org.eclipse.persistence.indirection.IndirectCollectionsFactory;
import org.eclipse.persistence.indirection.IndirectMap;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.indirection.QueryBasedValueHolder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.testing.tests.transparentindirection.TestSession;
import org.eclipse.persistence.testing.tests.transparentindirection.ZTestCase;
import org.eclipse.persistence.testing.tests.transparentindirection.ZTestSuite;

/**
 * Test a simple IndirectMap for Java 8 API.
 */
public class IndirectMapTestAPI extends ZTestCase {
    private Hashtable<String, String> map;
    private IndirectMap<String, String> testMap;
    private Listener testMapLsn;
    private Class<? extends IndirectMap> cls;
    private boolean useListener;

    public IndirectMapTestAPI(String name) {
        this(name, null, true);
    }

    public IndirectMapTestAPI(String name, Class<? extends IndirectMap> cls, boolean useListener) {
        super(name);
        this.cls = cls;
        this.useListener = useListener;
    }

    public static TestSuite getTestSuiteFor(Class<? extends IndirectMap> cls, boolean useListener) {
        ZTestSuite ts = new ZTestSuite("Suite for " + cls.getName() + "(useListener: " + useListener + ")");
        Enumeration<String> tests = ts.methodNamesStartingWithTestFor(IndirectMapTestAPI.class);
        while (tests.hasMoreElements()) {
            ts.addTest(new IndirectMapTestAPI(tests.nextElement(), cls, useListener));
        }
        return ts;
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
            assertTrue(notEqualsMessage(message, expected, actual), false);
        }
        for (Iterator stream = expected.keySet().iterator(); stream.hasNext();) {
            Object key = stream.next();
            if (!expected.get(key).equals(actual.get(key))) {
                assertTrue(notEqualsMessage(message, expected, actual), false);
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
    @Override
    protected void setUp() {
        super.setUp();
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
    @Override
    protected void tearDown() {
        super.tearDown();
        if (useListener) {
            testMapLsn.events.clear();
        }
    }

    //Java SE 8 API
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

    public void testForEach() {
        final Vector<String> v1 = new Vector<>();
        final Vector<String> v2 = new Vector<>();
        map.forEach((k, v) -> {v1.add(k);v1.add(v);});
        testMap.forEach((k, v) -> {v2.add(k);v2.add(v);});
        assertUnorderedElementsEqual(v1, v2);
        assertEquals(map, testMap);
        assertNoEvents();
    }

    public void testReplaceAll() {
        assertEquals(map, testMap);
        map.replaceAll((k,v) -> v += "sufix");
        testMap.replaceAll((k,v) -> v += "sufix");
        assertEquals(map, testMap);
        assertRemoveAddEvents(testMap.size());
    }

    public void testCompute() {
        assertEquals(map, testMap);
        String s1 = map.compute("one", (k,v) -> v += "suffix");
        String s2 = testMap.compute("one", (k,v) -> v += "suffix");
        assertEquals(s1, s2);
        assertEquals(map, testMap);
        assertEquals("111suffix", testMap.get("one"));
        assertRemoveAddEvents(1);
    }

    public void testCompute2() {
        assertEquals(map, testMap);
        String s1 = map.compute("notExist", (k,v) -> v += "suffix");
        String s2 = testMap.compute("notExist", (k,v) -> v += "suffix");
        assertEquals(s1, s2);
        assertEquals(map, testMap);
        assertEquals("nullsuffix", testMap.get("notExist"));
        assertAddEvents(1);
    }

    public void testComputeIfPresent() {
        assertEquals(map, testMap);
        String s1 = map.computeIfPresent("one", (k,v) -> v += "suffix");
        String s2 = testMap.computeIfPresent("one", (k,v) -> v += "suffix");
        assertEquals(s1, s2);
        assertEquals(map, testMap);
        assertEquals("111suffix", testMap.get("one"));
        assertRemoveAddEvents(1);
    }

    public void testComputeIfPresent2() {
        assertEquals(map, testMap);
        String s1 = map.computeIfPresent("notExist", (k,v) -> v += "suffix");
        String s2 = testMap.computeIfPresent("notExist", (k,v) -> v += "suffix");
        assertEquals(s1, s2);
        assertEquals(map, testMap);
        assertNull(testMap.get("notExist"));
        assertNoEvents();;
    }

    public void testComputeIfAbsent() {
        assertEquals(map, testMap);
        String s1 = map.computeIfAbsent("one", k -> "newitem");
        String s2 = testMap.computeIfAbsent("one", k -> "newitem");
        assertEquals(s1, s2);
        assertEquals(map, testMap);
        assertEquals("111", testMap.get("one"));
        assertNoEvents();
    }

    public void testComputeIfAbsent2() {
        assertEquals(map, testMap);
        String s1 = map.computeIfAbsent("notExist", k -> "newitem");
        String s2 = testMap.computeIfAbsent("notExist", k -> "newitem");
        assertEquals(s1, s2);
        assertEquals(map, testMap);
        assertAddEvents(1);
    }

    public void testMerge() {
        assertEquals(map, testMap);
        String s1 = map.merge("one", "custom", String::concat);
        String s2 = testMap.merge("one", "custom", String::concat);
        assertEquals(s1, s2);
        assertEquals(map, testMap);
        assertEquals("111custom", testMap.get("one"));
        assertRemoveAddEvents(1);
    }

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
