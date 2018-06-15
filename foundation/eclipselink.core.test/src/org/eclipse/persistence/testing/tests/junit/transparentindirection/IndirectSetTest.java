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
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.stream.StreamSupport;

import org.eclipse.persistence.descriptors.changetracking.CollectionChangeEvent;
import org.eclipse.persistence.indirection.IndirectCollectionsFactory;
import org.eclipse.persistence.indirection.IndirectSet;
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
 * Test a simple IndirectSet.
 * this should only be used in jdk1.2+
 * @author: Big Country
 */
@RunWith(Parameterized.class)
public class IndirectSetTest {

    public static final class S<E> extends IndirectSet<E> {}

    private Vector<String> list;
    private IndirectSet<String> testList;
    private Listener testListLsn;
    private Class<? extends IndirectSet> cls;
    private boolean useListener;

    @Parameters(name = "{0}, {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                 { IndirectSet.class, true }, { IndirectSet.class, false }, { S.class, true }, { S.class, false }
           });
    }

    public IndirectSetTest(Class<? extends IndirectSet> cls, boolean useListener) {
        this.cls = cls;
        this.useListener = useListener;
    }

    /**
     * set up the test fixture:
     */
    @Before
    public void setUp() {
        list = setUpList();
        Object temp = new HashSet<>(list);

        ValueHolderInterface vh = new QueryBasedValueHolder(new ReadAllQuery(), new DatabaseRecord(), new TestSession(temp));
        if (cls == null) {
            testList = IndirectCollectionsFactory.createIndirectSet();
        } else {
            try {
                testList = cls.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        testList.setValueHolder(vh);
        if (useListener) {
            testListLsn = new Listener();
            testList._persistence_setPropertyChangeListener(testListLsn);
        }
    }

    protected Vector setUpList() {
        Vector<String> result = new Vector<>();
        result.addElement("zero");
        result.addElement("one");
        result.addElement("two");
        result.addElement("three");
        result.addElement("four");
        result.addElement("five");
        result.addElement("six");
        result.addElement("seven");
        result.addElement("eight");
        result.addElement("nine");
        return result;
    }

    /**
     * nothing for now...
     */
    @After
    public void tearDown() {
        if (useListener) {
            testListLsn.events.clear();
        }
    }

    @Test
    public void testAdd() {
        String temp = "foo";
        list.add(temp);
        testList.add(temp);
        ZTestCase.assertUnorderedElementsEqual(list, new Vector(testList));
        assertTrue(testList.contains(temp));
        assertAddEvents(1);
    }

    @Test
    public void testAddAll() {
        Vector<String> temp = new Vector<>();
        temp.addElement("foo");
        temp.addElement("bar");

        list.addAll(temp);
        testList.addAll(temp);

        ZTestCase.assertUnorderedElementsEqual(list, new Vector(testList));
        assertTrue(testList.containsAll(temp));
        assertAddEvents(2);
    }

    @Test
    public void testClear() {
        int originalSize = testList.size();
        list.clear();
        testList.clear();
        assertEquals(list, new Vector(testList));
        assertTrue(testList.isEmpty());
        assertRemoveEvents(originalSize);
    }

    @Test
    public void testContains() {
        assertTrue(testList.contains(list.elementAt(1)));
        assertNoEvents();
    }

    @Test
    public void testContainsAll() {
        assertTrue(testList.containsAll(list.subList(1, 5)));
        assertNoEvents();
    }

    @Test
    public void testEquals() {
        assertTrue(testList.equals(new HashSet(list)));
        assertNoEvents();
    }

    @Test
    public void testIsEmpty() {
        assertTrue(!testList.isEmpty());
        assertNoEvents();
    }

    @Test
    public void testIterator() {
        int i = 0;
        for (Iterator stream = testList.iterator(); stream.hasNext(); i++) {
            stream.next();
        }
        assertEquals(list.size(), i);
        assertNoEvents();
    }

    @Test
    public void testRemove() {
        String temp = "one";
        assertTrue(list.remove(temp));
        assertTrue(testList.remove(temp));
        ZTestCase.assertUnorderedElementsEqual(list, new Vector(testList));
        assertTrue(!testList.contains(temp));
        assertRemoveEvents(1);
    }

    @Test
    public void testRemoveAll() {
        Vector<String> temp = new Vector<>();
        temp.addElement("one");
        temp.addElement("two");

        assertTrue(list.removeAll(temp));
        assertTrue(testList.removeAll(temp));
        ZTestCase.assertUnorderedElementsEqual(list, new Vector(testList));
        assertTrue(!testList.containsAll(temp));
        assertRemoveEvents(2);
    }

    @Test
    public void testRetainAll() {
        int originalSize = testList.size();
        Vector<String> temp = new Vector<>();
        temp.addElement("one");
        temp.addElement("two");

        assertTrue(list.retainAll(temp));
        assertTrue(testList.retainAll(temp));

        ZTestCase.assertUnorderedElementsEqual(list, new Vector(testList));
        assertTrue(testList.containsAll(temp));

        assertEquals(temp.size(), testList.size());
        assertRemoveEvents(originalSize - temp.size());
    }

    @Test
    public void testSize() {
        assertEquals(list.size(), testList.size());
        assertNoEvents();
    }

    @Test
    public void testToArray1() {
        Object[] temp = list.toArray();
        Vector v1 = new Vector(temp.length);
        for (int i = 0; i < temp.length; i++) {
            v1.addElement(temp[i]);
        }
        temp = testList.toArray();
        Vector v2 = new Vector(temp.length);
        for (int i = 0; i < temp.length; i++) {
            v2.addElement(temp[i]);
        }
        ZTestCase.assertUnorderedElementsEqual(v1, v2);
        assertNoEvents();
    }

    @Test
    public void testToArray2() {
        String[] temp = list.toArray(new String[0]);
        Vector v1 = new Vector(temp.length);
        for (int i = 0; i < temp.length; i++) {
            v1.addElement(temp[i]);
        }
        temp = testList.toArray(new String[0]);
        Vector v2 = new Vector(temp.length);
        for (int i = 0; i < temp.length; i++) {
            v2.addElement(temp[i]);
        }
        ZTestCase.assertUnorderedElementsEqual(v1, v2);
        assertNoEvents();
    }

    //Java SE 8 API
    @Test
    public void testSpliterator() {
        assertTrue(StreamSupport.stream(testList.spliterator(), true).allMatch(item -> list.contains(item)));
        assertTrue(StreamSupport.stream(list.spliterator(), true).allMatch(item -> testList.contains(item)));
        assertEquals(testList.size(), testList.stream().count());
        assertNoEvents();
    }

    @Test
    public void testStream() {
        assertTrue(testList.stream().allMatch(item -> list.contains(item)));
        assertTrue(list.stream().allMatch(item -> testList.contains(item)));
        assertEquals(testList.size(), testList.stream().count());
        assertNoEvents();
    }

    @Test
    public void testParallelStream() {
        assertTrue(testList.parallelStream().allMatch(item -> list.contains(item)));
        assertTrue(list.parallelStream().allMatch(item -> testList.contains(item)));
        assertEquals(testList.size(), testList.parallelStream().count());
        assertNoEvents();
    }

    @Test
    public void testRemoveIf() {
        // remove 'six' and 'seven'
        assertTrue(list.removeIf(item -> item.startsWith("s")));
        assertTrue(testList.removeIf(item -> item.startsWith("s")));
        assertEquals("size do not match", 8, testList.size());
        ZTestCase.assertUnorderedElementsEqual(list, new Vector(testList));
        assertRemoveEvents(2);
    }

    @Test
    public void testForEach() {
        final Vector<String> v1 = new Vector<>();
        final Vector<String> v2 = new Vector<>();
        list.forEach(v1::add);
        testList.forEach(v2::add);
        ZTestCase.assertUnorderedElementsEqual(list, new Vector(testList));
        ZTestCase.assertUnorderedElementsEqual(v1, v2);
        assertNoEvents();
    }

    private void assertNoEvents() {
        if (useListener) {
            assertTrue(testListLsn.events.isEmpty());
        }
    }

    private void assertAddEvents(int count) {
        if (useListener) {
            assertEquals("events do not match", count, testListLsn.events.size());
            for (CollectionChangeEvent cce : testListLsn.events) {
                assertEquals("expected add event", CollectionChangeEvent.ADD, cce.getChangeType());
            }
        }
    }

    private void assertRemoveEvents(int count) {
        if (useListener) {
            assertEquals("events do not match", count, testListLsn.events.size());
            for (CollectionChangeEvent cce : testListLsn.events) {
                assertEquals("expected add event", CollectionChangeEvent.REMOVE, cce.getChangeType());
            }
        }
    }

    private static final class Listener implements PropertyChangeListener {
        private List<CollectionChangeEvent> events = new ArrayList<>();

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            events.add((CollectionChangeEvent)evt);
        }

    }
}
