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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.descriptors.changetracking.CollectionChangeEvent;
import org.eclipse.persistence.indirection.IndirectCollectionsFactory;
import org.eclipse.persistence.indirection.IndirectList;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.helper.JavaSEPlatform;
import org.eclipse.persistence.internal.indirection.QueryBasedValueHolder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;

/**
 * Test a simple IndirectList.
 * @author: Big Country
 */
public class IndirectListTestAPI extends ZTestCase {

    protected Vector<String> list;
    protected IndirectList<String> testList;
    private Listener testListLsn;
    private Class<? extends IndirectList> cls;
    private boolean useListener;

    /**
     * Constructor
     * @param name java.lang.String
     */
    public IndirectListTestAPI(String name) {
        this(name, null, true);
    }

    public IndirectListTestAPI(String name, Class<? extends IndirectList> cls, boolean useListener) {
        super(name);
        this.cls = cls;
        this.useListener = useListener;
    }

    public static TestSuite getTestSuiteFor(Class<? extends IndirectList> cls, boolean useListener) {
        ZTestSuite ts = new ZTestSuite("Suite for " + cls.getName() + "(useListener: " + useListener + ")");

        ts.addTest(new IndirectListTestAPI("testAdd1", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testAdd2", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testAddAll1", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testAddAll2", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testAddElement", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testClear", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testContains", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testContainsAll", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testElementAt", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testElements", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testEquals", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testFirstElement", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testGet", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testHashCode", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testIndexOf1", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testIndexOf2", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testInsertElementAt", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testIsEmpty", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testIterator", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testLastElement", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testLastIndexOf1", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testLastIndexOf2", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testListIterator1", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testListIterator2", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testRemove1", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testRemove2", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testRemoveAll", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testRemoveAllElements", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testRemoveElement", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testRemoveElementAt", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testRetainAll", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testSet", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testSetElementAt", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testSize", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testSubList", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testToArray1", cls, useListener));
        ts.addTest(new IndirectListTestAPI("testToArray2", cls, useListener));

        ts.addTest(new IndirectListTestAPI("testSort", cls, useListener));

        if (JavaSEPlatform.CURRENT.atLeast(JavaSEPlatform.v1_8) && cls.getName().contains(".jdk8.")) {
            try {
                Constructor<?> c = Class.forName("org.eclipse.persistence.testing.tests.transparentindirection.jdk8.IndirectListTestAPI8").getConstructor(String.class, Class.class, boolean.class);
                ts.addTest((TestCase) c.newInstance("testForEach", cls, useListener));
                ts.addTest((TestCase) c.newInstance("testParallelStream", cls, useListener));
                ts.addTest((TestCase) c.newInstance("testRemoveIf", cls, useListener));
                ts.addTest((TestCase) c.newInstance("testReplaceAll", cls, useListener));
                ts.addTest((TestCase) c.newInstance("testSpliterator", cls, useListener));
                ts.addTest((TestCase) c.newInstance("testSort", cls, useListener));
                ts.addTest((TestCase) c.newInstance("testStream", cls, useListener));
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
        return ts;
    }

    /**
     * set up the test fixture:
     * 1. an IndirectList based on a Vector
     */
    @Override
    protected void setUp() {
        super.setUp();
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
    @Override
    protected void tearDown() {
        super.tearDown();
        if (useListener) {
            testListLsn.events.clear();
        }
    }

    public void testAdd1() {
        String temp = "foo";

        list.add(3, temp);
        testList.add(3, temp);
        assertEquals(list, testList);
        assertTrue(testList.contains(temp));
        assertAddEvents(1);
    }

    public void testAdd2() {
        String temp = "foo";

        list.add(temp);
        testList.add(temp);
        assertEquals(list, testList);
        assertTrue(testList.contains(temp));
        assertAddEvents(1);
    }

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

    public void testAddElement() {
        String temp = "foo";
        list.addElement(temp);
        testList.addElement(temp);
        assertEquals(list, testList);
        assertTrue(testList.contains(temp));
        assertAddEvents(1);
    }

    public void testClear() {
        int originalSize = testList.size();
        list.clear();
        testList.clear();
        assertEquals(list, testList);
        assertTrue(testList.isEmpty());
        assertRemoveEvents(originalSize);

    }

    public void testContains() {
        assertTrue(testList.contains(list.elementAt(1)));
        assertNoEvents();
    }

    public void testContainsAll() {
        assertTrue(testList.containsAll(list.subList(1, 5)));
        assertNoEvents();
    }

    public void testElementAt() {
        assertEquals(list.elementAt(1), testList.elementAt(1));
        assertNoEvents();
    }

    public void testElements() {
        assertEquals(list.elements().nextElement(), testList.elements().nextElement());
        assertNoEvents();
    }

    public void testEquals() {
        assertTrue(testList.equals(list));
        assertNoEvents();
    }

    public void testFirstElement() {
        assertEquals(list.firstElement(), testList.firstElement());
        assertNoEvents();
    }

    public void testGet() {
        assertEquals(list.get(1), testList.get(1));
        assertNoEvents();
    }

    public void testHashCode() {
        assertEquals(list.hashCode(), testList.hashCode());
        assertNoEvents();
    }

    public void testIndexOf1() {
        String temp = "one";
        assertEquals(list.indexOf(temp), testList.indexOf(temp));
        assertNoEvents();
    }

    public void testIndexOf2() {
        String temp = "seven";
        assertEquals(list.indexOf(temp, 3), testList.indexOf(temp, 3));
        assertNoEvents();
    }

    public void testInsertElementAt() {
        String temp = "foo";
        list.insertElementAt(temp, 3);
        testList.insertElementAt(temp, 3);
        assertEquals(list, testList);
        assertTrue(testList.contains(temp));
        assertAddEvents(1);
    }

    public void testIsEmpty() {
        assertTrue(!testList.isEmpty());
        assertNoEvents();
    }

    public void testIterator() {
        int i = 0;

        for (Iterator stream = testList.iterator(); stream.hasNext(); i++) {
            stream.next();
        }
        assertEquals(list.size(), i);
        assertNoEvents();
    }

    public void testLastElement() {
        assertEquals(list.lastElement(), testList.lastElement());
        assertNoEvents();
    }

    public void testLastIndexOf1() {
        String temp = "one";
        assertEquals(list.lastIndexOf(temp), testList.lastIndexOf(temp));
        assertNoEvents();
    }

    public void testLastIndexOf2() {
        String temp = "one";
        assertEquals(list.lastIndexOf(temp, 7), testList.lastIndexOf(temp, 7));
        assertNoEvents();
    }

    public void testListIterator1() {
        int i = 0;

        for (ListIterator stream = testList.listIterator(); stream.hasNext(); i++) {
            stream.next();
        }
        assertEquals(list.size(), i);
        assertNoEvents();
    }

    public void testListIterator2() {
        int i = 0;

        for (ListIterator stream = testList.listIterator(2); stream.hasNext(); i++) {
            stream.next();
        }
        assertEquals(list.size(), i + 2);
        assertNoEvents();
    }

    public void testRemove1() {
        Object temp = list.remove(1);
        assertEquals(temp, testList.remove(1));
        assertEquals(list, testList);
        assertTrue(!testList.contains(temp));
        assertRemoveEvents(1);
    }

    public void testRemove2() {
        Object temp = "one";

        assertTrue(list.remove(temp));
        assertTrue(testList.remove(temp));
        assertEquals(list, testList);
        assertTrue(!testList.contains(temp));
        assertRemoveEvents(1);
    }

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

    public void testRemoveAllElements() {
        int originalSize = testList.size();
        list.removeAllElements();
        testList.removeAllElements();
        assertEquals(list, testList);
        assertTrue(testList.size() == 0);
        assertRemoveEvents(originalSize);
    }

    public void testRemoveElement() {
        Object temp = "one";
        assertTrue(list.removeElement(temp));
        assertTrue(testList.removeElement(temp));
        assertEquals(list, testList);
        assertTrue(!testList.contains(temp));
        assertRemoveEvents(1);
    }

    public void testRemoveElementAt() {
        Object temp = testList.elementAt(1);
        list.removeElementAt(1);
        testList.removeElementAt(1);
        assertEquals(list, testList);
        assertTrue(!testList.contains(temp));
        assertRemoveEvents(1);
    }

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

    public void testSet() {
        String temp = "foo";

        list.set(3, temp);
        testList.set(3, temp);
        assertEquals(list, testList);
        assertTrue(testList.contains(temp));
        assertRemoveAddEvents(1);
    }

    public void testSetElementAt() {
        String temp = "foo";
        list.setElementAt(temp, 3);
        testList.setElementAt(temp, 3);
        assertEquals(list, testList);
        assertTrue(testList.contains(temp));
        assertRemoveAddEvents(1);
    }

    public void testSize() {
        assertEquals(list.size(), testList.size());
        assertNoEvents();
    }

    public void testSubList() {
        assertEquals(list.subList(2, 5), testList.subList(2, 5));
        assertNoEvents();
    }

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

    public void testSort() {
        assertElementsEqual(list, testList);
        Comparator<String> c = new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        };
        Collections.sort(list, c);
        testList.sort(c);
        assertElementsEqual(list, testList);
        assertNoEvents();
    }

    protected void assertNoEvents() {
        if (useListener) {
            assertTrue(testListLsn.events.isEmpty());
        }
    }

    protected void assertAddEvents(int count) {
        if (useListener) {
            assertEquals("events do not match", count, testListLsn.events.size());
            for (CollectionChangeEvent cce : testListLsn.events) {
                assertEquals("expected add event", CollectionChangeEvent.ADD, cce.getChangeType());
            }
        }
    }

    protected void assertRemoveEvents(int count) {
        if (useListener) {
            assertEquals("events do not match", count, testListLsn.events.size());
            for (CollectionChangeEvent cce : testListLsn.events) {
                assertEquals("expected add event", CollectionChangeEvent.REMOVE, cce.getChangeType());
            }
        }
    }

    protected void assertRemoveAddEvents(int count) {
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
