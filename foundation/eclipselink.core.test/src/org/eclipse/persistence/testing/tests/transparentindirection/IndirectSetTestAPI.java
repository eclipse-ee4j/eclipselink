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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.descriptors.changetracking.CollectionChangeEvent;
import org.eclipse.persistence.indirection.IndirectCollectionsFactory;
import org.eclipse.persistence.indirection.IndirectSet;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.helper.JavaSEPlatform;
import org.eclipse.persistence.internal.indirection.QueryBasedValueHolder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;

/**
 * Test a simple IndirectSet.
 * this should only be used in jdk1.2+
 * @author: Big Country
 */
public class IndirectSetTestAPI extends ZTestCase {
    protected Vector<String> list;
    protected IndirectSet<String> testList;
    private Listener testListLsn;
    private Class<? extends IndirectSet> cls;
    private boolean useListener;

    /**
     * Constructor
     * @param name java.lang.String
     */
    public IndirectSetTestAPI(String name) {
        this(name, null, true);
    }

    public IndirectSetTestAPI(String name, Class<? extends IndirectSet> cls, boolean useListener) {
        super(name);
        this.cls = cls;
        this.useListener = useListener;
    }

    public static TestSuite getTestSuiteFor(Class<? extends IndirectSet> cls, boolean useListener) {
        ZTestSuite ts = new ZTestSuite("Suite for " + cls.getName() + "(useListener: " + useListener + ")");

        ts.addTest(new IndirectSetTestAPI("testAdd", cls, useListener));
        ts.addTest(new IndirectSetTestAPI("testAddAll", cls, useListener));
        ts.addTest(new IndirectSetTestAPI("testClear", cls, useListener));
        ts.addTest(new IndirectSetTestAPI("testContains", cls, useListener));
        ts.addTest(new IndirectSetTestAPI("testContainsAll", cls, useListener));
        ts.addTest(new IndirectSetTestAPI("testEquals", cls, useListener));
        ts.addTest(new IndirectSetTestAPI("testIsEmpty", cls, useListener));
        ts.addTest(new IndirectSetTestAPI("testIterator", cls, useListener));
        ts.addTest(new IndirectSetTestAPI("testRemove", cls, useListener));
        ts.addTest(new IndirectSetTestAPI("testRemoveAll", cls, useListener));
        ts.addTest(new IndirectSetTestAPI("testRetainAll", cls, useListener));
        ts.addTest(new IndirectSetTestAPI("testSize", cls, useListener));
        ts.addTest(new IndirectSetTestAPI("testToArray1", cls, useListener));
        ts.addTest(new IndirectSetTestAPI("testToArray2", cls, useListener));

        if (JavaSEPlatform.CURRENT.atLeast(JavaSEPlatform.v1_8) && cls.getName().contains(".jdk8.")) {
            try {
                Constructor<?> c = Class.forName("org.eclipse.persistence.testing.tests.transparentindirection.jdk8.IndirectSetTestAPI8").getConstructor(String.class, Class.class, boolean.class);
                ts.addTest((TestCase) c.newInstance("testForEach", cls, useListener));
                ts.addTest((TestCase) c.newInstance("testParallelStream", cls, useListener));
                ts.addTest((TestCase) c.newInstance("testRemoveIf", cls, useListener));
                ts.addTest((TestCase) c.newInstance("testSpliterator", cls, useListener));
                ts.addTest((TestCase) c.newInstance("testStream", cls, useListener));
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        return ts;
    }

    /**
     * set up the test fixture:
     */
    @Override
    protected void setUp() {
        super.setUp();
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
    @Override
    protected void tearDown() {
        super.tearDown();
        if (useListener) {
            testListLsn.events.clear();
        }
    }

    public void testAdd() {
        String temp = "foo";
        list.add(temp);
        testList.add(temp);
        assertUnorderedElementsEqual(list, new Vector(testList));
        assertTrue(testList.contains(temp));
        assertAddEvents(1);
    }

    public void testAddAll() {
        Vector<String> temp = new Vector<>();
        temp.addElement("foo");
        temp.addElement("bar");

        list.addAll(temp);
        testList.addAll(temp);

        assertUnorderedElementsEqual(list, new Vector(testList));
        assertTrue(testList.containsAll(temp));
        assertAddEvents(2);
    }

    public void testClear() {
        int originalSize = testList.size();
        list.clear();
        testList.clear();
        assertEquals(list, new Vector(testList));
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

    public void testEquals() {
        assertTrue(testList.equals(new HashSet(list)));
        assertNoEvents();
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

    public void testRemove() {
        String temp = "one";
        assertTrue(list.remove(temp));
        assertTrue(testList.remove(temp));
        assertUnorderedElementsEqual(list, new Vector(testList));
        assertTrue(!testList.contains(temp));
        assertRemoveEvents(1);
    }

    public void testRemoveAll() {
        Vector<String> temp = new Vector<>();
        temp.addElement("one");
        temp.addElement("two");

        assertTrue(list.removeAll(temp));
        assertTrue(testList.removeAll(temp));
        assertUnorderedElementsEqual(list, new Vector(testList));
        assertTrue(!testList.containsAll(temp));
        assertRemoveEvents(2);
    }

    public void testRetainAll() {
        int originalSize = testList.size();
        Vector<String> temp = new Vector<>();
        temp.addElement("one");
        temp.addElement("two");

        assertTrue(list.retainAll(temp));
        assertTrue(testList.retainAll(temp));

        assertUnorderedElementsEqual(list, new Vector(testList));
        assertTrue(testList.containsAll(temp));

        assertEquals(temp.size(), testList.size());
        assertRemoveEvents(originalSize - temp.size());
    }

    public void testSize() {
        assertEquals(list.size(), testList.size());
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
        assertUnorderedElementsEqual(v1, v2);
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
        assertUnorderedElementsEqual(v1, v2);
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

    private static final class Listener implements PropertyChangeListener {
        private List<CollectionChangeEvent> events = new ArrayList<>();

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            events.add((CollectionChangeEvent)evt);
        }

    }


}
