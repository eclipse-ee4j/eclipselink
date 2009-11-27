package org.eclipse.persistence.testing.tests.jpa.deployment;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;

import junit.framework.TestCase;

import org.eclipse.persistence.internal.jpa.deployment.osgi.CompositeEnumeration;

public class CompositeEnumerationTest extends TestCase {
    
    public void testNone() throws Exception {
        CompositeEnumeration<Integer> compositeEnumeration = new CompositeEnumeration<Integer>(new Vector<Enumeration<Integer>>());
        assertNoMoreElements(compositeEnumeration);
    }

    public void testOneWithOne() throws Exception {
        Vector<Integer> v1 = createVector(1, 1);
        Vector<Enumeration<Integer>> enumerations = new Vector<Enumeration<Integer>>();
        enumerations.add(v1.elements());
        CompositeEnumeration<Integer> compositeEnumeration = new CompositeEnumeration<Integer>(new Vector<Enumeration<Integer>>(enumerations));
        assertHasElement(compositeEnumeration, "first", 1);
        assertNoMoreElements(compositeEnumeration);
    }

    public void testOneWithTwo() throws Exception {
        Vector<Integer> v1 = createVector(1, 2);
        Vector<Enumeration<Integer>> enumerations = new Vector<Enumeration<Integer>>();
        enumerations.add(v1.elements());
        CompositeEnumeration<Integer> compositeEnumeration = new CompositeEnumeration<Integer>(new Vector<Enumeration<Integer>>(enumerations));
        assertHasElement(compositeEnumeration, "first", 1);
        assertHasElement(compositeEnumeration, "second", 2);
        assertNoMoreElements(compositeEnumeration);
    }

    public void testThreeWithTwo() throws Exception {
        Vector<Integer> v1 = createVector(1, 2);
        Vector<Integer> v2 = createVector(3, 4);
        Vector<Integer> v3 = createVector(5, 6);
        Vector<Enumeration<Integer>> enumerations = new Vector<Enumeration<Integer>>();
        enumerations.add(v1.elements());
        enumerations.add(v2.elements());
        enumerations.add(v3.elements());
        CompositeEnumeration<Integer> compositeEnumeration = new CompositeEnumeration<Integer>(new Vector<Enumeration<Integer>>(enumerations));
        assertHasElement(compositeEnumeration, "first", 1);
        assertHasElement(compositeEnumeration, "second", 2);
        assertHasElement(compositeEnumeration, "third", 3);
        assertHasElement(compositeEnumeration, "fourth", 4);
        assertHasElement(compositeEnumeration, "fifth", 5);
        assertHasElement(compositeEnumeration, "sixth", 6);
        assertNoMoreElements(compositeEnumeration);
    }
    
    public void testEmptyFirst() throws Exception {
        Vector<Integer> v1 = new Vector<Integer>();
        Vector<Integer> v2 = createVector(1, 1);
        Vector<Integer> v3 = createVector(2, 2);
        Vector<Enumeration<Integer>> enumerations = new Vector<Enumeration<Integer>>();
        enumerations.add(v1.elements());
        enumerations.add(v2.elements());
        enumerations.add(v3.elements());
        CompositeEnumeration<Integer> compositeEnumeration = new CompositeEnumeration<Integer>(new Vector<Enumeration<Integer>>(enumerations));
        assertHasElement(compositeEnumeration, "first", 1);
        assertHasElement(compositeEnumeration, "second", 2);
        assertNoMoreElements(compositeEnumeration);
    }

    public void testEmptyLast() throws Exception {
        Vector<Integer> v1 = createVector(1, 1);
        Vector<Integer> v2 = createVector(2, 2);
        Vector<Integer> v3 = new Vector<Integer>();
        Vector<Enumeration<Integer>> enumerations = new Vector<Enumeration<Integer>>();
        enumerations.add(v1.elements());
        enumerations.add(v2.elements());
        enumerations.add(v3.elements());
        CompositeEnumeration<Integer> compositeEnumeration = new CompositeEnumeration<Integer>(new Vector<Enumeration<Integer>>(enumerations));
        assertHasElement(compositeEnumeration, "first", 1);
        assertHasElement(compositeEnumeration, "second", 2);
        assertNoMoreElements(compositeEnumeration);
    }

    public void testEmptyMiddle() throws Exception {
        Vector<Integer> v1 = createVector(1, 1);
        Vector<Integer> v2 = new Vector<Integer>();
        Vector<Integer> v3 = createVector(2, 2);
        Vector<Enumeration<Integer>> enumerations = new Vector<Enumeration<Integer>>();
        enumerations.add(v1.elements());
        enumerations.add(v2.elements());
        enumerations.add(v3.elements());
        CompositeEnumeration<Integer> compositeEnumeration = new CompositeEnumeration<Integer>(new Vector<Enumeration<Integer>>(enumerations));
        assertHasElement(compositeEnumeration, "first", 1);
        assertHasElement(compositeEnumeration, "second", 2);
        assertNoMoreElements(compositeEnumeration);
    }

    public void testAllEmpty() throws Exception {
        Vector<Integer> v1 = new Vector<Integer>();
        Vector<Integer> v2 = new Vector<Integer>();
        Vector<Integer> v3 = new Vector<Integer>();
        Vector<Enumeration<Integer>> enumerations = new Vector<Enumeration<Integer>>();
        enumerations.add(v1.elements());
        enumerations.add(v2.elements());
        enumerations.add(v3.elements());
        CompositeEnumeration<Integer> compositeEnumeration = new CompositeEnumeration<Integer>(new Vector<Enumeration<Integer>>(enumerations));
        assertNoMoreElements(compositeEnumeration);
    }

    private void assertHasElement(
            CompositeEnumeration<Integer> compositeEnumeration,
            String elementName, int expectedElement) {
        assertTrue(compositeEnumeration.hasMoreElements());
        Integer nextElement = compositeEnumeration.nextElement();
        assertSame(elementName + " element", expectedElement, nextElement);
    }
    
    private void assertNoMoreElements(
            CompositeEnumeration<Integer> compositeEnumeration) {
        assertFalse(compositeEnumeration.hasMoreElements());
        try {
            compositeEnumeration.nextElement();
            fail("NoNoSuchElementException expected");
        } catch (NoSuchElementException e) {
            // expected Exception caught
        }
    }

    private Vector<Integer> createVector(int from, int to) {
        Vector<Integer> vector = new Vector<Integer>();
        for (int i = from; i <= to; i++) {
            vector.add(i);
        }
        return vector;
    }    

}
