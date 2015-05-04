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

import java.io.StringWriter;
import java.util.Comparator;
import java.util.List;
import java.util.function.*;

import org.eclipse.persistence.indirection.IndirectList;
import org.eclipse.persistence.testing.tests.transparentindirection.IndirectListTestAPI;

public class IndirectListTestAPI8 extends IndirectListTestAPI {

    public IndirectListTestAPI8(String name, Class<? extends IndirectList> cls, boolean useListener) {
        super(name, cls, useListener);
    }

    public void testForEach() {
        C c1 = new C();
        C c2 = new C();
        list.forEach(c1);
        testList.forEach(c2);
        assertElementsEqual(list, testList);
        assertEquals(c1.toString(), c2.toString());
        assertNoEvents();
    }

    public void testParallelStream() {
        assertTrue(testList.parallelStream().allMatch(new P(list)));
        assertTrue(list.parallelStream().allMatch(new P(testList)));
        assertEquals(testList.size(), testList.parallelStream().count());
        assertNoEvents();
    }

    public void testRemoveIf() {
        // remove 'six' and 'seven'
        assertTrue(list.removeIf(new P2()));
        assertTrue(testList.removeIf(new P2()));
        assertEquals("size do not match", 8, testList.size());
        assertElementsEqual(list, testList);
        assertRemoveEvents(2);
    }

    public void testReplaceAll() {
        UnaryOperator<String> op = new UnaryOperator<String>() {
            @Override
            public String apply(String s) {
                return s.toUpperCase();
            }
        };
        list.replaceAll(op);
        testList.replaceAll(op);
        assertElementsEqual(list, testList);
        assertRemoveAddEvents(testList.size());
    }

    public void testSort() {
        assertElementsEqual(list, testList);
        Comparator<String> c = new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        };
        list.sort(c);
        testList.sort(c);
        assertElementsEqual(list, testList);
        assertNoEvents();
    }

    public void testSpliterator() {
        assertTrue(java.util.stream.StreamSupport.stream(testList.spliterator(), true).allMatch(new P(list)));
        assertTrue(java.util.stream.StreamSupport.stream(list.spliterator(), true).allMatch(new P(testList)));
        assertEquals(testList.size(), testList.stream().count());
        assertNoEvents();
    }

    public void testStream() {
        assertTrue(testList.stream().allMatch(new P(list)));
        assertTrue(list.stream().allMatch(new P(testList)));
        assertEquals(testList.size(), testList.stream().count());
        assertNoEvents();
    }

    private static final class C implements Consumer<String> {

        private final StringWriter sw;

        C() {
            sw = new StringWriter();
        }

        @Override
        public void accept(String s) {
            sw.append(s);
        }

        @Override
        public String toString() {
            return sw.toString();
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

    private static final class P2 implements Predicate<String> {

        @Override
        public boolean test(String s) {
            return s.startsWith("s");
        }
    }
}
