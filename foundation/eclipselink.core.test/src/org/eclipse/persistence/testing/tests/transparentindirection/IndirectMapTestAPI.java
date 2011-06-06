/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import java.util.*;
import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;

/**
 * Test a simple IndirectMap.
 * @author: Big Country
 */
public class IndirectMapTestAPI extends ZTestCase {
    Hashtable map;
    IndirectMap testMap;

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

        org.eclipse.persistence.indirection.ValueHolderInterface vh = new org.eclipse.persistence.internal.indirection.QueryBasedValueHolder(new ReadAllQuery(), new DatabaseRecord(), new TestSession(temp));
        testMap = new IndirectMap();
        testMap.setValueHolder(vh);
    }

    protected Hashtable setUpMap() {
        Hashtable result = new Hashtable();
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
        Object key = "foo";
        Object value = "bar";
        map.put(key, value);
        testMap.put(key, value);

        this.assertEquals(map, testMap);
        this.assertTrue(testMap.containsKey(key));
        this.assertTrue(testMap.contains(value));
    }

    public void testPutAll() {
        Hashtable temp = new Hashtable();
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
        Object temp = "one";
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
}
