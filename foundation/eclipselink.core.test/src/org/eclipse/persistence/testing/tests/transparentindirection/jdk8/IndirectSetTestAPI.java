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
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.stream.StreamSupport;

import junit.framework.TestSuite;

import org.eclipse.persistence.descriptors.changetracking.CollectionChangeEvent;
import org.eclipse.persistence.indirection.IndirectCollectionsFactory;
import org.eclipse.persistence.indirection.IndirectSet;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.internal.indirection.QueryBasedValueHolder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.testing.tests.transparentindirection.TestSession;
import org.eclipse.persistence.testing.tests.transparentindirection.ZTestCase;
import org.eclipse.persistence.testing.tests.transparentindirection.ZTestSuite;

/**
 * Test a simple IndirectSet for Java 8 API
 */
public class IndirectSetTestAPI extends ZTestCase {
    private Vector<String> list;
    private IndirectSet<String> testList;
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
        Enumeration<String> tests = ts.methodNamesStartingWithTestFor(IndirectSetTestAPI.class);
        while (tests.hasMoreElements()) {
            ts.addTest(new IndirectSetTestAPI(tests.nextElement(), cls, useListener));
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

    //Java SE 8 API
    public void testSpliterator() {
        assertTrue(StreamSupport.stream(testList.spliterator(), true).allMatch(item -> list.contains(item)));
        assertTrue(StreamSupport.stream(list.spliterator(), true).allMatch(item -> testList.contains(item)));
        assertEquals(testList.size(), testList.stream().count());
        assertNoEvents();
    }

    public void testStream() {
        assertTrue(testList.stream().allMatch(item -> list.contains(item)));
        assertTrue(list.stream().allMatch(item -> testList.contains(item)));
        assertEquals(testList.size(), testList.stream().count());
        assertNoEvents();
    }

    public void testParallelStream() {
        assertTrue(testList.parallelStream().allMatch(item -> list.contains(item)));
        assertTrue(list.parallelStream().allMatch(item -> testList.contains(item)));
        assertEquals(testList.size(), testList.parallelStream().count());
        assertNoEvents();
    }

    public void testRemoveIf() {
        // remove 'six' and 'seven'
        assertTrue(list.removeIf(item -> item.startsWith("s")));
        assertTrue(testList.removeIf(item -> item.startsWith("s")));
        assertEquals("size do not match", 8, testList.size());
        assertUnorderedElementsEqual(list, new Vector(testList));
        assertRemoveEvents(2);
    }

    public void testForEach() {
        final Vector<String> v1 = new Vector<>();
        final Vector<String> v2 = new Vector<>();
        list.forEach(v1::add);
        testList.forEach(v2::add);
        assertUnorderedElementsEqual(list, new Vector(testList));
        assertUnorderedElementsEqual(v1, v2);
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
