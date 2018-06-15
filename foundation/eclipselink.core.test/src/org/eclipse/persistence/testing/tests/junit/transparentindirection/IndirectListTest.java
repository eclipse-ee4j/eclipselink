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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import java.util.stream.StreamSupport;

import org.eclipse.persistence.descriptors.changetracking.CollectionChangeEvent;
import org.eclipse.persistence.indirection.IndirectCollectionsFactory;
import org.eclipse.persistence.indirection.IndirectList;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.indirection.QueryBasedValueHolder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test a simple IndirectList.
 * @author: Big Country
 */
@RunWith(Parameterized.class)
public class IndirectListTest {

    public static final class L<E> extends IndirectList<E> {}

    private Vector<String> list;
    private IndirectList<String> testList;
    private Listener testListLsn;
    private Class<? extends IndirectList> cls;
    private boolean useListener;

    @Parameters(name = "{0}, {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                 { IndirectList.class, true }, { IndirectList.class, false }, { L.class, true }, { L.class, false }
           });
    }

    public IndirectListTest(Class<? extends IndirectList> cls, boolean useListener) {
        this.cls = cls;
        this.useListener = useListener;
    }

    /**
     * set up the test fixture:
     * 1. an IndirectList based on a Vector
     */
    @Before
    public void setUp() {
        list = setUpList();
        Object temp = new Vector<>(list);

        ValueHolderInterface vh = new QueryBasedValueHolder(new ReadAllQuery(), new DatabaseRecord(), new TestSession(temp));
        if (cls == null) {
            testList = IndirectCollectionsFactory.createIndirectList();
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
    public void testAdd1() {
        String temp = "foo";

        list.add(3, temp);
        testList.add(3, temp);
        assertEquals(list, testList);
        assertTrue(testList.contains(temp));
        assertAddEvents(1);
    }

    @Test
    public void testAdd2() {
        String temp = "foo";

        list.add(temp);
        testList.add(temp);
        assertEquals(list, testList);
        assertTrue(testList.contains(temp));
        assertAddEvents(1);
    }

    @Test
    public void testAddAll1() {
        Vector temp = new Vector();
        temp.addElement("foo");
        temp.addElement("bar");

        list.addAll(temp);
        testList.addAll(temp);
        assertEquals(list, testList);
        assertTrue(testList.containsAll(temp));
        assertAddEvents(2);
    }

    @Test
    public void testAddAll2() {
        Vector temp = new Vector();
        temp.addElement("foo");
        temp.addElement("bar");

        list.addAll(3, temp);
        testList.addAll(3, temp);
        assertEquals(list, testList);
        assertTrue(testList.containsAll(temp));
        assertAddEvents(2);
    }

    @Test
    public void testAddElement() {
        String temp = "foo";
        list.addElement(temp);
        testList.addElement(temp);
        assertEquals(list, testList);
        assertTrue(testList.contains(temp));
        assertAddEvents(1);
    }

    @Test
    public void testClear() {
        int originalSize = testList.size();
        list.clear();
        testList.clear();
        assertEquals(list, testList);
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
    public void testElementAt() {
        assertEquals(list.elementAt(1), testList.elementAt(1));
        assertNoEvents();
    }

    @Test
    public void testElements() {
        assertEquals(list.elements().nextElement(), testList.elements().nextElement());
        assertNoEvents();
    }

    @Test
    public void testEquals() {
        assertTrue(testList.equals(list));
        assertNoEvents();
    }

    @Test
    public void testFirstElement() {
        assertEquals(list.firstElement(), testList.firstElement());
        assertNoEvents();
    }

    @Test
    public void testGet() {
        assertEquals(list.get(1), testList.get(1));
        assertNoEvents();
    }

    @Test
    public void testHashCode() {
        assertEquals(list.hashCode(), testList.hashCode());
        assertNoEvents();
    }

    @Test
    public void testIndexOf1() {
        String temp = "one";
        assertEquals(list.indexOf(temp), testList.indexOf(temp));
        assertNoEvents();
    }

    @Test
    public void testIndexOf2() {
        String temp = "seven";
        assertEquals(list.indexOf(temp, 3), testList.indexOf(temp, 3));
        assertNoEvents();
    }

    @Test
    public void testInsertElementAt() {
        String temp = "foo";
        list.insertElementAt(temp, 3);
        testList.insertElementAt(temp, 3);
        assertEquals(list, testList);
        assertTrue(testList.contains(temp));
        assertAddEvents(1);
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
    public void testLastElement() {
        assertEquals(list.lastElement(), testList.lastElement());
        assertNoEvents();
    }

    @Test
    public void testLastIndexOf1() {
        String temp = "one";
        assertEquals(list.lastIndexOf(temp), testList.lastIndexOf(temp));
        assertNoEvents();
    }

    @Test
    public void testLastIndexOf2() {
        String temp = "one";
        assertEquals(list.lastIndexOf(temp, 7), testList.lastIndexOf(temp, 7));
        assertNoEvents();
    }

    @Test
    public void testListIterator1() {
        int i = 0;

        for (ListIterator stream = testList.listIterator(); stream.hasNext(); i++) {
            stream.next();
        }
        assertEquals(list.size(), i);
        assertNoEvents();
    }

    @Test
    public void testListIterator2() {
        int i = 0;

        for (ListIterator stream = testList.listIterator(2); stream.hasNext(); i++) {
            stream.next();
        }
        assertEquals(list.size(), i + 2);
        assertNoEvents();
    }

    @Test
    public void testRemove1() {
        Object temp = list.remove(1);
        assertEquals(temp, testList.remove(1));
        assertEquals(list, testList);
        assertTrue(!testList.contains(temp));
        assertRemoveEvents(1);
    }

    @Test
    public void testRemove2() {
        Object temp = "one";

        assertTrue(list.remove(temp));
        assertTrue(testList.remove(temp));
        assertEquals(list, testList);
        assertTrue(!testList.contains(temp));
        assertRemoveEvents(1);
    }

    @Test
    public void testRemoveAll() {
        Vector temp = new Vector();
        temp.addElement("one");
        temp.addElement("two");

        assertTrue(list.removeAll(temp));
        assertTrue(testList.removeAll(temp));
        assertEquals(list, testList);
        assertTrue(!testList.containsAll(temp));
        assertRemoveEvents(2);
    }

    @Test
    public void testRemoveAllElements() {
        int originalSize = testList.size();
        list.removeAllElements();
        testList.removeAllElements();
        assertEquals(list, testList);
        assertTrue(testList.size() == 0);
        assertRemoveEvents(originalSize);
    }

    @Test
    public void testRemoveElement() {
        Object temp = "one";
        assertTrue(list.removeElement(temp));
        assertTrue(testList.removeElement(temp));
        assertEquals(list, testList);
        assertTrue(!testList.contains(temp));
        assertRemoveEvents(1);
    }

    @Test
    public void testRemoveElementAt() {
        Object temp = testList.elementAt(1);
        list.removeElementAt(1);
        testList.removeElementAt(1);
        assertEquals(list, testList);
        assertTrue(!testList.contains(temp));
        assertRemoveEvents(1);
    }

    @Test
    public void testRetainAll() {
        int originalSize = testList.size();
        Vector temp = new Vector();
        temp.addElement("one");
        temp.addElement("two");

        assertTrue(list.retainAll(temp));
        assertTrue(testList.retainAll(temp));
        assertEquals(list, testList);
        assertTrue(testList.containsAll(temp));
        assertEquals(temp.size(), testList.size());
        assertRemoveEvents(originalSize - temp.size());
    }

    @Test
    public void testSet() {
        String temp = "foo";

        list.set(3, temp);
        testList.set(3, temp);
        assertEquals(list, testList);
        assertTrue(testList.contains(temp));
        assertRemoveAddEvents(1);
    }

    @Test
    public void testSetElementAt() {
        String temp = "foo";
        list.setElementAt(temp, 3);
        testList.setElementAt(temp, 3);
        assertEquals(list, testList);
        assertTrue(testList.contains(temp));
        assertRemoveAddEvents(1);
    }

    @Test
    public void testSize() {
        assertEquals(list.size(), testList.size());
        assertNoEvents();
    }

    @Test
    public void testSubList() {
        assertEquals(list.subList(2, 5), testList.subList(2, 5));
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

        assertEquals(v1, v2);
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

        assertEquals(v1, v2);
        assertNoEvents();
    }

    //Java SE 8 API
    @Test
    public void testSort() {
        assertArrayEquals(list.toArray(), testList.toArray());
        Comparator<String> c = new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        };
        list.sort(c);
        testList.sort(c);
        assertArrayEquals(list.toArray(), testList.toArray());
        assertNoEvents();
    }

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
        assertArrayEquals(list.toArray(), testList.toArray());
        assertRemoveEvents(2);
    }

    @Test
    public void testReplaceAll() {
        list.replaceAll(String::toUpperCase);
        testList.replaceAll(String::toUpperCase);
        assertArrayEquals(list.toArray(), testList.toArray());
        assertRemoveAddEvents(testList.size());
    }

    @Test
    public void testForEach() {
        final StringWriter sw1 = new StringWriter();
        final StringWriter sw2 = new StringWriter();
        list.forEach(sw1::append);
        testList.forEach(sw2::append);
        assertArrayEquals(list.toArray(), testList.toArray());
        assertEquals(sw1.toString(), sw2.toString());
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

    private void assertRemoveAddEvents(int count) {
        if (useListener) {
            int totalEvents = count * 2;
            assertEquals("events do not match", totalEvents, testListLsn.events.size());
            for (int i = 0; i < totalEvents;) {
                CollectionChangeEvent removeEvent = testListLsn.events.get(i++);
                CollectionChangeEvent addEvent = testListLsn.events.get(i++);
                assertEquals("expected remove event", CollectionChangeEvent.REMOVE, removeEvent.getChangeType());
                assertEquals("expected add event", CollectionChangeEvent.ADD, addEvent.getChangeType());
                assertFalse("removed: '" + removeEvent.getNewValue() + "', new: '" + addEvent.getNewValue() + "'",
                        removeEvent.getNewValue().equals(addEvent.getNewValue()));
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
