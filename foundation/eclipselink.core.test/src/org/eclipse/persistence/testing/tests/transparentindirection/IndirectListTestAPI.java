/*******************************************************************************
 * Copyright (c) 1998, 2014 Oracle and/or its affiliates. All rights reserved.
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

import java.util.*;

import org.eclipse.persistence.indirection.*;
import org.eclipse.persistence.internal.helper.JavaSEPlatform;
import org.eclipse.persistence.internal.helper.JavaVersion;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;

/**
 * Test a simple IndirectList.
 * @author: Big Country
 */
public class IndirectListTestAPI extends ZTestCase {

    /**
     * Vector with sort method to verify sorting with JDK < 1.8.
     * @param <E> Object type stored inside.
     */
    public static final class VectorWithSort<E> extends Vector<E> {

        /**
         * Creates an instance of Vector with sort method with provided initial
         * elements.
         * @param collection The collection whose elements are to be placed into
         *                   this vector.
         */
        private VectorWithSort(final Collection<? extends E> collection) {
            super(collection);
        }

        /**
         * Creates an instance of Vector.
         */
        private VectorWithSort() {
            super();
        }

        /**
         * JDK 1.8 sort implemented in Vector to verify sorting on JDK < 1.8
         * @param c Comparator to define sorting order.
         */
        public synchronized void sort(final Comparator<? super E> c) {
            final int expectedModCount = modCount;
            Arrays.sort((E[]) elementData, 0, elementCount, c);
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            modCount++;
        }
    }

    /**
     * Indirect list with delegate {@see Vector} setter.
     * @param <E> Object type stored inside.
     */
    private static final class IndirectListWrapper<E> extends IndirectList<E> {

        /**
         * Creates an instance of IndirectList with provided initial capacity.
         * @param initialCapacity Internal initial capacity.
         */
        private IndirectListWrapper(final int initialCapacity) {
            super(0);
        }

        /**
         * Allow to set delegate object from outside.
         * @param delegate Delegate object to be set.
         */
        private final void setDelegate(final Vector<E> delegate) {
            this.delegate = delegate;
        }
    }

    /**
     * Do we run with at least Java SE 1.8?
     * @return Value of {@code true} when we run with Java SE 1.8 or higher
     *         or {@code false} otherwise.
     */
    private static final boolean isJava8() {
        return JavaVersion.currentPlatform().atLeast(JavaSEPlatform.v1_8);
    }

    Vector list;
    IndirectList testList;

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
        Object temp = new Vector(list);

        org.eclipse.persistence.indirection.ValueHolderInterface vh = new org.eclipse.persistence.internal.indirection.QueryBasedValueHolder(new ReadAllQuery(), new DatabaseRecord(), new TestSession(temp));
        testList = new IndirectList();
        testList.setValueHolder(vh);
    }

    protected Vector setUpList() {
        Vector result = new Vector();
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

    public void testAdd1() {
        Object temp = "foo";

        list.add(3, temp);
        testList.add(3, temp);
        this.assertEquals(list, testList);
        this.assertTrue(testList.contains(temp));
    }

    public void testAdd2() {
        Object temp = "foo";

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
        Object temp = "foo";
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
        Object temp = "foo";
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
        Object temp = "foo";

        list.set(3, temp);
        testList.set(3, temp);
        this.assertEquals(list, testList);
        this.assertTrue(testList.contains(temp));
    }

    public void testSetElementAt() {
        Object temp = "foo";
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

        this.assertEquals(v1, v2);
    }

    // TODO: Rewrite to work directly with Vector#sort(Comparator) when source level will be at least 1.8
    public void testSort() {
        final Vector<String> data = isJava8() ? new Vector<String>(list) : new VectorWithSort<String>(list);
        final VectorWithSort<String> sortedData = new VectorWithSort<String>(list);
        final IndirectListWrapper<String> list = new IndirectListWrapper<String>(data.size());
        sortedData.sort(null);
        list.setDelegate(data);
        list.sort(null);
        assertEquals(data.size(), sortedData.size());
        for (int i = 0; i < sortedData.size(); i++) {
            this.assertEquals(data.get(i), sortedData.get(i));
        }
    }

    // TODO: Rewrite to work directly with Vector#sort(Comparator) when source level will be at least 1.8
    public void testSortOnCommonVector() {
        final Vector<String> data = new Vector<String>(list);
        final IndirectListWrapper<String> list = new IndirectListWrapper<String>(data.size());
        list.setDelegate(data);
        try {
            list.sort(null);
            if (!isJava8()) {
                fail("Sort shall throw an exception on JDK < 1.8.");
            }
        } catch (UnsupportedOperationException e) {
            if (isJava8()) {
                fail("Sort shall work since JDK 1.8.");
            }
        }
    }

}
