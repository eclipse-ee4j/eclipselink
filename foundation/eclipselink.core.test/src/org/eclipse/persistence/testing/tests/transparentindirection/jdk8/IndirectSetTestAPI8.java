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
import java.util.Vector;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import org.eclipse.persistence.indirection.IndirectSet;
import org.eclipse.persistence.testing.tests.transparentindirection.IndirectSetTestAPI;

public class IndirectSetTestAPI8 extends IndirectSetTestAPI {

    public IndirectSetTestAPI8(String name, Class<? extends IndirectSet> cls, boolean useListener) {
        super(name, cls, useListener);
    }

    public void testForEach() {
        C c1 = new C();
        C c2 = new C();
        list.forEach(c1);
        testList.forEach(c2);
        assertUnorderedElementsEqual(list, new Vector(testList));
        assertUnorderedElementsEqual(c1.v, c2.v);
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
        Predicate<String> p = new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return s.startsWith("s");
            }
        };
        assertTrue(list.removeIf(p));
        assertTrue(testList.removeIf(p));
        assertEquals("size do not match", 8, testList.size());
        assertUnorderedElementsEqual(list, new Vector(testList));
        assertRemoveEvents(2);
    }

    public void testSpliterator() {
        assertTrue(StreamSupport.stream(testList.spliterator(), true).allMatch(new P(list)));
        assertTrue(StreamSupport.stream(list.spliterator(), true).allMatch(new P(testList)));
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

        private final Vector<String> v;

        C() {
            v = new Vector<>();
        }

        @Override
        public void accept(String s) {
            v.add(s);
        }
    }

    private static final class P implements Predicate<String> {

        private final Collection list;

        P(Collection l) {
            list = l;
        }

        @Override
        public boolean test(String s) {
            return list.contains(s);
        }
    }
}
