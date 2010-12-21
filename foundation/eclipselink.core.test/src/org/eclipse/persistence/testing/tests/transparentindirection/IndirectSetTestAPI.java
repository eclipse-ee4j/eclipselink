/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.transparentindirection.IndirectSet;

/**
 * Test a simple IndirectSet.
 * this should only be used in jdk1.2+
 * @author: Big Country
 */
public class IndirectSetTestAPI extends ZTestCase {
    Vector list;
    IndirectSet testList;

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
        Object temp = new HashSet(list);

        org.eclipse.persistence.indirection.ValueHolderInterface vh = new org.eclipse.persistence.internal.indirection.QueryBasedValueHolder(new ReadAllQuery(), new DatabaseRecord(), new TestSession(temp));
        testList = new IndirectSet();
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

    public void testAdd() {
        Object temp = "foo";
        list.add(temp);
        testList.add(temp);
        this.assertUnorderedElementsEqual(list, new Vector(testList));
        this.assertTrue(testList.contains(temp));
    }

    public void testAddAll() {
        Vector temp = new Vector();
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
        Object temp = "one";
        this.assertTrue(list.remove(temp));
        this.assertTrue(testList.remove(temp));
        this.assertUnorderedElementsEqual(list, new Vector(testList));
        this.assertTrue(!testList.contains(temp));
    }

    public void testRemoveAll() {
        Vector temp = new Vector();
        temp.addElement("one");
        temp.addElement("two");

        this.assertTrue(list.removeAll(temp));
        this.assertTrue(testList.removeAll(temp));
        this.assertUnorderedElementsEqual(list, new Vector(testList));
        this.assertTrue(!testList.containsAll(temp));
    }

    public void testRetainAll() {
        Vector temp = new Vector();
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
}
