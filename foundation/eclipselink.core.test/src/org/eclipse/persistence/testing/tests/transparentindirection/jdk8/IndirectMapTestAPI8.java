/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.transparentindirection.jdk8;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.eclipse.persistence.indirection.IndirectMap;
import org.eclipse.persistence.testing.tests.transparentindirection.IndirectMapTestAPI;

public class IndirectMapTestAPI8 extends IndirectMapTestAPI {

    public IndirectMapTestAPI8(String name, Class<? extends IndirectMap> cls, boolean useListener) {
        super(name, cls, useListener);
    }

    public void testCompute() {
        assertEquals(map, testMap);
        String s1 = map.compute("one", new BF_S());
        String s2 = testMap.compute("one", new BF_S());
        assertEquals(s1, s2);
        assertEquals(map, testMap);
        assertEquals("111suffix", testMap.get("one"));
        assertRemoveAddEvents(1);
    }

    public void testCompute2() {
        assertEquals(map, testMap);
        String s1 = map.compute("notExist", new BF_S());
        String s2 = testMap.compute("notExist", new BF_S());
        assertEquals(s1, s2);
        assertEquals(map, testMap);
        assertEquals("nullsuffix", testMap.get("notExist"));
        assertAddEvents(1);
    }

    public void testComputeIfAbsent() {
        assertEquals(map, testMap);
        String s1 = map.computeIfAbsent("one", new F());
        String s2 = testMap.computeIfAbsent("one", new F());
        assertEquals(s1, s2);
        assertEquals(map, testMap);
        assertEquals("111", testMap.get("one"));
        assertNoEvents();
    }

    public void testComputeIfAbsent2() {
        assertEquals(map, testMap);
        String s1 = map.computeIfAbsent("notExist", new F());
        String s2 = testMap.computeIfAbsent("notExist", new F());
        assertEquals(s1, s2);
        assertEquals(map, testMap);
        assertAddEvents(1);
    }

    public void testComputeIfPresent() {
        assertEquals(map, testMap);
        String s1 = map.computeIfPresent("one", new BF_S());
        String s2 = testMap.computeIfPresent("one", new BF_S());
        assertEquals(s1, s2);
        assertEquals(map, testMap);
        assertEquals("111suffix", testMap.get("one"));
        assertRemoveAddEvents(1);
    }

    public void testComputeIfPresent2() {
        assertEquals(map, testMap);
        String s1 = map.computeIfPresent("notExist", new BF_S());
        String s2 = testMap.computeIfPresent("notExist", new BF_S());
        assertEquals(s1, s2);
        assertEquals(map, testMap);
        assertNull(testMap.get("notExist"));
        assertNoEvents();;
    }

    public void testEntrySetRemoveIf() {
        Predicate<Map.Entry<String, String>> p = new Predicate<Map.Entry<String, String>>() {

            @Override
            public boolean test(Map.Entry<String, String> item) {
                return "one".equals(item.getKey());
            }
        };
        Set<Map.Entry<String, String>> entrySet = map.entrySet();
        Set<Map.Entry<String, String>> testEntrySet = testMap.entrySet();
        boolean result1 = entrySet.removeIf(p);
        boolean result2 = testEntrySet.removeIf(p);
        assertEquals(map, testMap);
        assertEquals(result1, result2);
        assertNull(testMap.get("one"));
        assertRemoveEvents(1);
    }

    public void testForEach() {
        C c1 = new C();
        C c2 = new C();
        map.forEach(c1);
        testMap.forEach(c2);
        assertUnorderedElementsEqual(c1.v, c2.v);
        assertEquals(map, testMap);
        assertNoEvents();
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

    public void testKeySetRemoveIf() {
        // remove 2 items - 'six' and 'seven'
        Predicate<String> p = new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return s.startsWith("s");
            }
        };
        Set<String> keySet = map.keySet();
        Set<String> testKeySet = testMap.keySet();
        boolean result1 = keySet.removeIf(p);
        boolean result2 = testKeySet.removeIf(p);
        assertEquals(map, testMap);
        assertEquals(result1, result2);
        assertNull(testMap.get("six"));
        assertRemoveEvents(2);
    }

    public void testMerge() {
        assertEquals(map, testMap);
        String s1 = map.merge("one", "custom", new BF());
        String s2 = testMap.merge("one", "custom", new BF());
        assertEquals(s1, s2);
        assertEquals(map, testMap);
        assertEquals("111custom", testMap.get("one"));
        assertRemoveAddEvents(1);
    }

    public void testMerge2() {
        assertEquals(map, testMap);
        String s1 = map.merge("notExist", "custom", new BF());
        String s2 = testMap.merge("notExist", "custom", new BF());
        assertEquals(s1, s2);
        assertEquals(map, testMap);
        assertEquals("111", testMap.get("one"));
        assertAddEvents(1);
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

    public void testReplaceAll() {
        assertEquals(map, testMap);
        map.replaceAll(new BF_S());
        testMap.replaceAll(new BF_S());
        assertEquals(map, testMap);
        assertRemoveAddEvents(testMap.size());
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

    public void testValuesRemoveIf() {
        // remove 1 item - 'two'
        Predicate<String> p = new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return s.startsWith("2");
            }
        };
        Collection<String> keySet = map.values();
        Collection<String> testKeySet = testMap.values();
        boolean result1 = keySet.removeIf(p);
        boolean result2 = testKeySet.removeIf(p);
        assertEquals(map, testMap);
        assertEquals(result1, result2);
        assertNull(testMap.get("two"));
        assertRemoveEvents(1);
    }

    private static final class BF implements BiFunction<String, String, String> {

        public String apply(String a, String b) {
            return a != null ? a.concat(b) : b;
        }
    }

    private static final class BF_S implements BiFunction<String, String, String> {

        public String apply(String a, String b) {
            return b != null ? b + "suffix" : "nullsuffix";
        }
    }

    private static final class C implements BiConsumer<String, String> {

        private final Vector<String> v;

        C() {
            v = new Vector<>();
        }

        @Override
        public void accept(String a, String b) {
            v.add(a);
            v.add(b);
        }
    }

    private static final class F implements Function<String, String> {

        public String apply(String a) {
            return "newitem";
        }
    }

    private static final class P implements Predicate<String> {

        private final List list;

        P(List l) {
            list = l;
        }

        @Override
        public boolean test(String s) {
            return list.contains(s);
        }
    }
}
