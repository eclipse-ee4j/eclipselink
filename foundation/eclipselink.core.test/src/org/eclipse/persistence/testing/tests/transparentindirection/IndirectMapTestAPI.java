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
package org.eclipse.persistence.testing.tests.transparentindirection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;
import org.eclipse.persistence.indirection.IndirectCollectionsFactory;
import org.eclipse.persistence.indirection.IndirectMap;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.helper.JavaSEPlatform;
import org.eclipse.persistence.internal.indirection.QueryBasedValueHolder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;

/**
 * Test a simple IndirectMap.
 * @author: Big Country
 */
public class IndirectMapTestAPI extends ZTestCase {
    private Hashtable<String, String> map;
    private IndirectMap<String, String> testMap;

    public IndirectMapTestAPI(String name) {
        super(name);
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
            this.assertTrue(this.notEqualsMessage(message, expected, actual), false);
        }
        for (Iterator stream = expected.keySet().iterator(); stream.hasNext();) {
            Object key = stream.next();
            if (!expected.get(key).equals(actual.get(key))) {
                this.assertTrue(this.notEqualsMessage(message, expected, actual), false);
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
        this.assertElementsEqual("", expected, actual);
    }

    /**
     * set up the test fixture:
     * 1. an IndirectMap based on a Hashtable
     */
    protected void setUp() {
        super.setUp();
        map = this.setUpMap();
        Object temp = new Hashtable(map);

        ValueHolderInterface vh = new QueryBasedValueHolder(new ReadAllQuery(), new DatabaseRecord(), new TestSession(temp));
        testMap = IndirectCollectionsFactory.createIndirectMap();
        testMap.setValueHolder(vh);
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
    protected void tearDown() {
        super.tearDown();
    }

    public void testClear() {
        map.clear();
        testMap.clear();

        this.assertEquals(map, testMap);
        this.assertTrue(testMap.size() == 0);
    }

    public void testContains() {
        this.assertTrue(testMap.contains(map.elements().nextElement()));
    }

    public void testContainsKey() {
        this.assertTrue(testMap.containsKey(map.keys().nextElement()));
    }

    public void testContainsValue() {
        this.assertTrue(testMap.containsValue(map.elements().nextElement()));
    }

    public void testElements() {
        if (!map.contains(testMap.elements().nextElement())) {
            this.assertTrue("Map Does not contain elements from test", false);
        }
    }

    public void testEntrySet() {
        this.assertEquals(map.entrySet(), testMap.entrySet());
    }

    public void testEquals() {
        this.assertTrue(testMap.equals(map));
    }

    public void testGet() {
        this.assertEquals(map.get("six"), testMap.get("six"));
    }

    public void testHashCode() {
        this.assertEquals(map.hashCode(), testMap.hashCode());
    }

    public void testIsEmpty() {
        this.assertTrue(!testMap.isEmpty());
    }

    public void testKeys() {
        if (map.contains(testMap.keys().nextElement())) {
            this.assertTrue("Map Does not contain keys from test", false);
        }
    }

    public void testKeySet() {
        this.assertEquals(map.entrySet(), testMap.entrySet());
    }

    public void testPut() {
        String key = "foo";
        String value = "bar";
        map.put(key, value);
        testMap.put(key, value);

        this.assertEquals(map, testMap);
        this.assertTrue(testMap.containsKey(key));
        this.assertTrue(testMap.contains(value));
    }

    public void testPutAll() {
        Hashtable<String, String> temp = new Hashtable<>();
        temp.put("foo", "bar");
        temp.put("sna", "fu");

        map.putAll(temp);
        testMap.putAll(temp);
        this.assertEquals(map, testMap);
        for (Enumeration stream = temp.keys(); stream.hasMoreElements();) {
            this.assertTrue(testMap.containsKey(stream.nextElement()));
        }
        for (Enumeration stream = temp.elements(); stream.hasMoreElements();) {
            this.assertTrue(testMap.contains(stream.nextElement()));
        }
    }

    public void testRemove() {
        String temp = "one";
        this.assertTrue(map.remove(temp) != null);
        this.assertTrue(testMap.remove(temp) != null);
        this.assertEquals(map, testMap);
        this.assertTrue(!testMap.containsKey(temp));
    }

    public void testSize() {
        this.assertEquals(map.size(), testMap.size());
    }

    public void testValues() {
        this.assertEquals(map.size(), testMap.values().size());

        for (Iterator stream = testMap.values().iterator(); stream.hasNext();) {
            this.assertTrue(map.contains(stream.next()));
        }
        map.values().removeAll(testMap.values());
        this.assertTrue(map.values().isEmpty());
    }

    public void testReplace() {
        assertEquals(map, testMap);
        Object o = null;
        try {
            o = callMethod(testMap, "replace", new Class[] {Object.class, Object.class}, new Object[] {"one", "1"});
        } catch (UnsupportedOperationException e) {
            if (JavaSEPlatform.CURRENT.compareTo(JavaSEPlatform.v1_8) < 0) {
                //nothing to check on JDK 7 and lower
                return;
            }
        }
        assertNotNull("Should return value", o);
        assertEquals("111", o);
        assertEquals("1", testMap.get("one"));
    }

    public void testReplaceWithDefault() {
        assertEquals("111", testMap.get("one"));
        Object o = null;
        try {
            o = callMethod(testMap, "replace", new Class[] {Object.class, Object.class, Object.class}, new Object[] {"one", "1", "1234"});
        } catch (UnsupportedOperationException e) {
            if (JavaSEPlatform.CURRENT.compareTo(JavaSEPlatform.v1_8) < 0) {
                //nothing to check on JDK 7 and lower
                return;
            }
        }
        assertFalse("Should return false", (boolean) o);
        assertFalse("Should not have '1234'", testMap.containsValue("1234"));

        assertEquals("111", testMap.get("one"));
        try {
            o = callMethod(testMap, "replace", new Class[] {Object.class, Object.class, Object.class}, new Object[] {"one", "111", "1234"});
        } catch (UnsupportedOperationException e) {
            if (JavaSEPlatform.CURRENT.compareTo(JavaSEPlatform.v1_8) < 0) {
                //nothing to check on JDK 7 and lower
                return;
            }
        }
        assertTrue("Should return true", (boolean) o);
        assertEquals("1234", testMap.get("one"));
    }

    public void testGetOrDefault() {
        Object o = null;
        assertFalse(testMap.containsKey("temp"));
        try {
            o = callMethod(testMap, "getOrDefault", new Class[] {Object.class, Object.class}, new Object[] {"temp", "1234"});
        } catch (UnsupportedOperationException e) {
            if (JavaSEPlatform.CURRENT.compareTo(JavaSEPlatform.v1_8) < 0) {
                //nothing to check on JDK 7 and lower
                return;
            }
        }
        assertNotNull("Should return value", o);
        assertEquals("1234", o);

        assertTrue(testMap.containsKey("one"));
        try {
            o = callMethod(testMap, "getOrDefault", new Class[] {Object.class, Object.class}, new Object[] {"one", "5678"});
        } catch (UnsupportedOperationException e) {
            if (JavaSEPlatform.CURRENT.compareTo(JavaSEPlatform.v1_8) < 0) {
                //nothing to check on JDK 7 and lower
                return;
            }
        }
        assertNotNull("Should return value", o);
        assertEquals("111", o);
    }

    public void testPutIfAbsent() {
        Object o = null;
        assertFalse(testMap.containsKey("temp"));
        try {
            o = callMethod(testMap, "putIfAbsent", new Class[] {Object.class, Object.class}, new Object[] {"temp", "1234"});
        } catch (UnsupportedOperationException e) {
            if (JavaSEPlatform.CURRENT.compareTo(JavaSEPlatform.v1_8) < 0) {
                //nothing to check on JDK 7 and lower
                return;
            }
        }
        assertNull("Should return null", o);

        assertTrue(testMap.containsKey("temp"));
        try {
            o = callMethod(testMap, "putIfAbsent", new Class[] {Object.class, Object.class}, new Object[] {"temp", "5678"});
        } catch (UnsupportedOperationException e) {
            if (JavaSEPlatform.CURRENT.compareTo(JavaSEPlatform.v1_8) < 0) {
                //nothing to check on JDK 7 and lower
                return;
            }
        }
        assertNotNull("Should return value", o);
        assertEquals("1234", o);
    }

    public void testRemoveTwoArgs() {
        Object o = null;
        assertEquals("111", testMap.get("one"));
        try {
            o = callMethod(testMap, "remove", new Class[] {Object.class, Object.class}, new Object[] {"one", "1234"});
        } catch (UnsupportedOperationException e) {
            if (JavaSEPlatform.CURRENT.compareTo(JavaSEPlatform.v1_8) < 0) {
                //nothing to check on JDK 7 and lower
                return;
            }
        }
        assertFalse("Should return false", (boolean) o);

        assertEquals("111", testMap.get("one"));
        try {
            o = callMethod(testMap, "remove", new Class[] {Object.class, Object.class}, new Object[] {"one", "111"});
        } catch (UnsupportedOperationException e) {
            if (JavaSEPlatform.CURRENT.compareTo(JavaSEPlatform.v1_8) < 0) {
                //nothing to check on JDK 7 and lower
                return;
            }
        }
        assertTrue("Should return true", (boolean) o);
        assertNull(testMap.get("one"));
    }

    private Object callMethod(Map map, String method, Class[] params, Object[] args) {
        try {
            Method m = map.getClass().getMethod(method, params);
            return m.invoke(map, args);
        } catch (NoSuchMethodException | SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException ex) {
            if (JavaSEPlatform.CURRENT.atLeast(JavaSEPlatform.v1_8)) {
                fail("cannot call method '" + method + "' " + ex.getMessage());
            } else {
                throw new UnsupportedOperationException();
            }
        }
        return null;
    }
}
