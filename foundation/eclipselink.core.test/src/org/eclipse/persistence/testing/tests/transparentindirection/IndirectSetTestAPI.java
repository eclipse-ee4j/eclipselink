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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
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
    Vector<String> list;
    IndirectSet<String> testList;

    /**
     * Constructor
     * @param name java.lang.String
     */
    public IndirectSetTestAPI(String name) {
        super(name);
    }

    /**
     * set up the test fixture:
     */
    protected void setUp() {
        super.setUp();
        list = this.setUpList();
        Object temp = new HashSet<>(list);

        ValueHolderInterface vh = new QueryBasedValueHolder(new ReadAllQuery(), new DatabaseRecord(), new TestSession(temp));
        testList = IndirectCollectionsFactory.createIndirectSet();
        testList.setValueHolder(vh);
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
    protected void tearDown() {
        super.tearDown();
    }

    public void testAdd() {
        String temp = "foo";
        list.add(temp);
        testList.add(temp);
        this.assertUnorderedElementsEqual(list, new Vector(testList));
        this.assertTrue(testList.contains(temp));
    }

    public void testAddAll() {
        Vector<String> temp = new Vector<>();
        temp.addElement("foo");
        temp.addElement("bar");

        list.addAll(temp);
        testList.addAll(temp);

        this.assertUnorderedElementsEqual(list, new Vector(testList));
        this.assertTrue(testList.containsAll(temp));
    }

    public void testClear() {
        list.clear();
        testList.clear();
        this.assertEquals(list, new Vector(testList));
        this.assertTrue(testList.size() == 0);
    }

    public void testContains() {
        this.assertTrue(testList.contains(list.elementAt(1)));
    }

    public void testContainsAll() {
        this.assertTrue(testList.containsAll(list.subList(1, 5)));
    }

    public void testEquals() {
        this.assertTrue(testList.equals(new HashSet(list)));
    }

    public void testIsEmpty() {
        this.assertTrue(!testList.isEmpty());
    }

    public void testIterator() {
        int i = 0;
        for (Iterator stream = testList.iterator(); stream.hasNext(); i++) {
            stream.next();
        }
        this.assertEquals(list.size(), i);
    }

    public void testRemove() {
        String temp = "one";
        this.assertTrue(list.remove(temp));
        this.assertTrue(testList.remove(temp));
        this.assertUnorderedElementsEqual(list, new Vector(testList));
        this.assertTrue(!testList.contains(temp));
    }

    public void testRemoveAll() {
        Vector<String> temp = new Vector<>();
        temp.addElement("one");
        temp.addElement("two");

        this.assertTrue(list.removeAll(temp));
        this.assertTrue(testList.removeAll(temp));
        this.assertUnorderedElementsEqual(list, new Vector(testList));
        this.assertTrue(!testList.containsAll(temp));
    }

    public void testRetainAll() {
        Vector<String> temp = new Vector<>();
        temp.addElement("one");
        temp.addElement("two");

        this.assertTrue(list.retainAll(temp));
        this.assertTrue(testList.retainAll(temp));

        this.assertUnorderedElementsEqual(list, new Vector(testList));
        this.assertTrue(testList.containsAll(temp));

        this.assertEquals(temp.size(), testList.size());
    }

    public void testSize() {
        this.assertEquals(list.size(), testList.size());
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
        this.assertUnorderedElementsEqual(v1, v2);
    }

    public void testToArray2() {
        String[] temp = (String[])list.toArray(new String[0]);
        Vector v1 = new Vector(temp.length);
        for (int i = 0; i < temp.length; i++) {
            v1.addElement(temp[i]);
        }
        temp = (String[])testList.toArray(new String[0]);
        Vector v2 = new Vector(temp.length);
        for (int i = 0; i < temp.length; i++) {
            v2.addElement(temp[i]);
        }
        this.assertUnorderedElementsEqual(v1, v2);
    }

    public void testSpliterator() {
        Object o = null;
        try {
            o = callMethod(testList, "spliterator", new Class[0], new Object[0]);
        } catch (UnsupportedOperationException e) {
            if (JavaSEPlatform.CURRENT.compareTo(JavaSEPlatform.v1_8) < 0) {
                //nothing to check on JDK 7 and lower
                return;
            }
        }
        assertNotNull("Should get an instance of java.util.Spliterator", o);
        boolean streamFound = false;
        for (Class c: o.getClass().getInterfaces()) {
            if ("java.util.Spliterator".equals(c.getName())) {
                streamFound = true;
                break;
            }
        }
        assertTrue("not implementing java.util.Spliterator", streamFound);
    }

    public void testStream() {
        Object o = null;
        try {
            o = callMethod(testList, "stream", new Class[0], new Object[0]);
        } catch (UnsupportedOperationException e) {
            if (JavaSEPlatform.CURRENT.compareTo(JavaSEPlatform.v1_8) < 0) {
                //nothing to check on JDK 7 and lower
                return;
            }
        }
        assertNotNull("Should get an instance of java.util.stream.Stream", o);
        boolean streamFound = false;
        if (o.getClass().getEnclosingClass() != null) {
            for (Class c: o.getClass().getEnclosingClass().getInterfaces()) {
                if ("java.util.stream.Stream".equals(c.getName())) {
                    streamFound = true;
                    break;
                }
            }
        }
        assertTrue("not implementing java.util.stream.Stream", streamFound);
    }

    public void testParallelStream() {
        Object o = null;
        try {
            o = callMethod(testList, "parallelStream", new Class[0], new Object[0]);
        } catch (UnsupportedOperationException e) {
            if (JavaSEPlatform.CURRENT.compareTo(JavaSEPlatform.v1_8) < 0) {
                //nothing to check on JDK 7 and lower
                return;
            }
        }
        assertNotNull("Should get an instance of java.util.stream.Stream", o);
        boolean streamFound = false;
        if (o.getClass().getEnclosingClass() != null) {
            for (Class c: o.getClass().getEnclosingClass().getInterfaces()) {
                if ("java.util.stream.Stream".equals(c.getName())) {
                    streamFound = true;
                    break;
                }
            }
        }
        assertTrue("not implementing java.util.stream.Stream", streamFound);
    }

    private Object callMethod(Set set, String method, Class[] params, Object[] args) {
        try {
            Method m = set.getClass().getMethod(method, params);
            return m.invoke(set, args);
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
