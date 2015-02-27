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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
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

    private Vector<String> list;
    private IndirectList<String> testList;

    /**
     * Constructor
     * @param name java.lang.String
     */
    public IndirectListTestAPI(String name) {
        super(name);
    }

    /**
     * set up the test fixture:
     * 1. an IndirectList based on a Vector
     */
    protected void setUp() {
        super.setUp();
        list = this.setUpList();
        Object temp = new Vector<>(list);

        ValueHolderInterface vh = new QueryBasedValueHolder(new ReadAllQuery(), new DatabaseRecord(), new TestSession(temp));
        testList = IndirectCollectionsFactory.createIndirectList();
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
    @Override
    protected void tearDown() {
        super.tearDown();
    }

    public void testAdd1() {
        String temp = "foo";

        list.add(3, temp);
        testList.add(3, temp);
        this.assertEquals(list, testList);
        this.assertTrue(testList.contains(temp));
    }

    public void testAdd2() {
        String temp = "foo";

        list.add(temp);
        testList.add(temp);
        this.assertEquals(list, testList);
        this.assertTrue(testList.contains(temp));
    }

    public void testAddAll1() {
        Vector temp = new Vector();
        temp.addElement("foo");
        temp.addElement("bar");

        list.addAll(temp);
        testList.addAll(temp);
        this.assertEquals(list, testList);
        this.assertTrue(testList.containsAll(temp));
    }

    public void testAddAll2() {
        Vector temp = new Vector();
        temp.addElement("foo");
        temp.addElement("bar");

        list.addAll(3, temp);
        testList.addAll(3, temp);
        this.assertEquals(list, testList);
        this.assertTrue(testList.containsAll(temp));
    }

    public void testAddElement() {
        String temp = "foo";
        list.addElement(temp);
        testList.addElement(temp);
        this.assertEquals(list, testList);
        this.assertTrue(testList.contains(temp));
    }

    public void testClear() {
        list.clear();
        testList.clear();
        this.assertEquals(list, testList);
        this.assertTrue(testList.size() == 0);
    }

    public void testContains() {
        this.assertTrue(testList.contains(list.elementAt(1)));
    }

    public void testContainsAll() {
        this.assertTrue(testList.containsAll(list.subList(1, 5)));
    }

    public void testElementAt() {
        this.assertEquals(list.elementAt(1), testList.elementAt(1));
    }

    public void testElements() {
        this.assertEquals(list.elements().nextElement(), testList.elements().nextElement());
    }

    public void testEquals() {
        this.assertTrue(testList.equals(list));
    }

    public void testFirstElement() {
        this.assertEquals(list.firstElement(), testList.firstElement());
    }

    public void testGet() {
        this.assertEquals(list.get(1), testList.get(1));
    }

    public void testHashCode() {
        this.assertEquals(list.hashCode(), testList.hashCode());
    }

    public void testIndexOf1() {
        String temp = "one";
        this.assertEquals(list.indexOf(temp), testList.indexOf(temp));
    }

    public void testIndexOf2() {
        String temp = "seven";
        this.assertEquals(list.indexOf(temp, 3), testList.indexOf(temp, 3));
    }

    public void testInsertElementAt() {
        String temp = "foo";
        list.insertElementAt(temp, 3);
        testList.insertElementAt(temp, 3);
        this.assertEquals(list, testList);
        this.assertTrue(testList.contains(temp));
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

    public void testLastElement() {
        this.assertEquals(list.lastElement(), testList.lastElement());
    }

    public void testLastIndexOf1() {
        String temp = "one";
        this.assertEquals(list.lastIndexOf(temp), testList.lastIndexOf(temp));
    }

    public void testLastIndexOf2() {
        String temp = "one";
        this.assertEquals(list.lastIndexOf(temp, 7), testList.lastIndexOf(temp, 7));
    }

    public void testListIterator1() {
        int i = 0;

        for (ListIterator stream = testList.listIterator(); stream.hasNext(); i++) {
            stream.next();
        }
        this.assertEquals(list.size(), i);
    }

    public void testListIterator2() {
        int i = 0;

        for (ListIterator stream = testList.listIterator(2); stream.hasNext(); i++) {
            stream.next();
        }
        this.assertEquals(list.size(), i + 2);
    }

    public void testRemove1() {
        Object temp = list.remove(1);
        this.assertEquals(temp, testList.remove(1));
        this.assertEquals(list, testList);
        this.assertTrue(!testList.contains(temp));
    }

    public void testRemove2() {
        Object temp = "one";

        this.assertTrue(list.remove(temp));
        this.assertTrue(testList.remove(temp));
        this.assertEquals(list, testList);
        this.assertTrue(!testList.contains(temp));
    }

    public void testRemoveAll() {
        Vector temp = new Vector();
        temp.addElement("one");
        temp.addElement("two");

        this.assertTrue(list.removeAll(temp));
        this.assertTrue(testList.removeAll(temp));
        this.assertEquals(list, testList);
        this.assertTrue(!testList.containsAll(temp));
    }

    public void testRemoveAllElements() {
        list.removeAllElements();
        testList.removeAllElements();
        this.assertEquals(list, testList);
        this.assertTrue(testList.size() == 0);
    }

    public void testRemoveElement() {
        Object temp = "one";
        this.assertTrue(list.removeElement(temp));
        this.assertTrue(testList.removeElement(temp));
        this.assertEquals(list, testList);
        this.assertTrue(!testList.contains(temp));
    }

    public void testRemoveElementAt() {
        Object temp = testList.elementAt(1);
        list.removeElementAt(1);
        testList.removeElementAt(1);
        this.assertEquals(list, testList);
        this.assertTrue(!testList.contains(temp));
    }

    public void testRetainAll() {
        Vector temp = new Vector();
        temp.addElement("one");
        temp.addElement("two");

        this.assertTrue(list.retainAll(temp));
        this.assertTrue(testList.retainAll(temp));
        this.assertEquals(list, testList);
        this.assertTrue(testList.containsAll(temp));
        this.assertEquals(temp.size(), testList.size());
    }

    public void testSet() {
        String temp = "foo";

        list.set(3, temp);
        testList.set(3, temp);
        this.assertEquals(list, testList);
        this.assertTrue(testList.contains(temp));
    }

    public void testSetElementAt() {
        String temp = "foo";
        list.setElementAt(temp, 3);
        testList.setElementAt(temp, 3);
        this.assertEquals(list, testList);
        this.assertTrue(testList.contains(temp));
    }

    public void testSize() {
        this.assertEquals(list.size(), testList.size());
    }

    public void testSubList() {
        this.assertEquals(list.subList(2, 5), testList.subList(2, 5));
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

        this.assertEquals(v1, v2);
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

        this.assertEquals(v1, v2);
    }

    //Java SE 8 API
    public void testSort() {
        assertElementsEqual(list, testList);
        Comparator c = new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        };
        try {
            callMethod(testList, "sort", new Class[]{Comparator.class}, new Object[]{c});
        } catch (UnsupportedOperationException e) {
            if (JavaSEPlatform.CURRENT.compareTo(JavaSEPlatform.v1_8) < 0) {
                //nothing to check on JDK 7 and lower
                return;
            }
        }
        list = new Vector<>();
        list.addElement("eight");
        list.addElement("five");
        list.addElement("four");
        list.addElement("nine");
        list.addElement("one");
        list.addElement("seven");
        list.addElement("six");
        list.addElement("three");
        list.addElement("two");
        list.addElement("zero");
        assertElementsEqual(list, testList);
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

    private Object callMethod(List list, String method, Class[] params, Object[] args) {
        try {
            Method m = list.getClass().getMethod(method, params);
            return m.invoke(list, args);
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
