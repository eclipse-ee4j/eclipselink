/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.workbench.test.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.Bag;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.ReverseComparator;

public class CollectionToolsTests extends TestCase {
	
	public static Test suite() {
		return new TestSuite(CollectionToolsTests.class);
	}
	
	public CollectionToolsTests(String name) {
		super(name);
	}
	
	public void testAddObjectArrayObject() {
		Object[] a = CollectionTools.add(this.buildArray1(), "twenty");
		assertEquals("invalid size", 4, a.length);
		assertTrue("element not present", CollectionTools.contains(a, "twenty"));
	}
	
	public void testAddObjectArrayObject2() {
		String[] a = (String[]) CollectionTools.add(this.buildStringArray1(), "twenty");
		assertEquals("invalid size", 4, a.length);
		assertTrue("element not present", CollectionTools.contains(a, "twenty"));
	}
	
	public void testAddObjectArrayIntObject() {
		Object[] a = new Object[] {"a", "b", "c", "d"};
		a = CollectionTools.add(a, 2, "X");
		assertEquals(5, a.length);
		assertTrue(CollectionTools.contains(a, "X"));
		assertTrue(Arrays.equals(new Object[] {"a", "b", "X", "c", "d"}, a));
	}
	
	public void testAddObjectArrayIntObject2() {
		String[] a = new String[] {"a", "b", "c", "d"};
		a = (String[]) CollectionTools.add(a, 2, "X");
		assertEquals(5, a.length);
		assertTrue(CollectionTools.contains(a, "X"));
		assertTrue(Arrays.equals(new String[] {"a", "b", "X", "c", "d"}, a));
	}
	
	public void testAddObjectArrayIntObjectException() {
		Object[] a = new Object[] {"a", "b", "c", "d"};
		boolean exCaught = false;
		try {
			a = CollectionTools.add(a, 33, "X");
		} catch (IndexOutOfBoundsException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}
	
	public void testAddCharArrayChar() {
		char[] a = CollectionTools.add(this.buildCharArray(), 'd');
		assertEquals("invalid size", 4, a.length);
		assertTrue("element not present", CollectionTools.contains(a, 'd'));
	}
	
	public void testAddCharArrayIntChar() {
		char[] a = new char[] {'a', 'b', 'c', 'd'};
		a = CollectionTools.add(a, 2, 'X');
		assertEquals(5, a.length);
		assertTrue(CollectionTools.contains(a, 'X'));
		assertTrue(Arrays.equals(new char[] {'a', 'b', 'X', 'c', 'd'}, a));
	}
	
	public void testAddIntArrayInt() {
		int[] a = CollectionTools.add(this.buildIntArray(), 30);
		assertEquals("invalid size", 4, a.length);
		assertTrue("element not present", CollectionTools.contains(a, 30));
	}
	
	public void testAddIntArrayIntInt() {
		int[] a = new int[] {1, 2, 3, 4};
		a = CollectionTools.add(a, 2, 99);
		assertEquals(5, a.length);
		assertTrue(CollectionTools.contains(a, 99));
		assertTrue(Arrays.equals(new int[] {1, 2, 99, 3, 4}, a));
	}
	
	public void testAddAllCollectionIterator() {
		List l1 = this.buildList1();
		List l2 = this.buildList2();
		Set s1 = this.buildSet1();
		List l3 = this.buildList1();	// same elements as s1
	
		assertTrue("invalid return value", CollectionTools.addAll(l1, l2.iterator()));
		assertEquals("invalid size", 6, l1.size());
		assertTrue("elements not added", l1.containsAll(l2));
	
		assertFalse("invalid return value", CollectionTools.addAll(s1, l3.iterator()));
		assertEquals("invalid size", 3, s1.size());
		assertTrue("elements not present", s1.containsAll(l3));
	}
	
	public void testAddAllCollectionObjectArray() {
		List l = this.buildList1();
		Object[] a = this.buildArray1();
		Set s = this.buildSet1();
	
		assertTrue("invalid return value", CollectionTools.addAll(l, a));
		assertEquals("invalid size", 6, l.size());
		assertTrue("elements not added", l.containsAll(CollectionTools.collection(a)));
	
		assertFalse("invalid return value", CollectionTools.addAll(s, a));
		assertEquals("invalid size", 3, s.size());
		assertTrue("elements not present", s.containsAll(CollectionTools.collection(a)));
	}
	
	public void testAddAllObjectArrayCollection() {
		Object[] a = this.buildArray1();
		Collection c = this.buildList1();
		Object[] newArray = CollectionTools.addAll(a, c);
	
		assertEquals("invalid size", 6, newArray.length);
		assertTrue("elements not added", CollectionTools.containsAll(newArray, c));
	}
	
	public void testAddAllObjectArrayCollection2() {
		String[] a = this.buildStringArray1();
		Collection c = this.buildList1();
		String[] newArray = (String[]) CollectionTools.addAll(a, c);
	
		assertEquals("invalid size", 6, newArray.length);
		assertTrue("elements not added", CollectionTools.containsAll(newArray, c));
	}
	
	public void testAddAllObjectArrayIterator() {
		Object[] a = this.buildArray1();
		Iterator i = this.buildList1().iterator();
		Object[] newArray = CollectionTools.addAll(a, i);
	
		assertEquals("invalid size", 6, newArray.length);
		assertTrue("elements not added", CollectionTools.containsAll(newArray, this.buildList1()));
	}
	
	public void testAddAllObjectArrayObjectArray() {
		Object[] a1 = this.buildArray1();
		Object[] a2 = this.buildArray2();
		Object[] newArray = CollectionTools.addAll(a1, a2);
	
		assertEquals("invalid size", 6, newArray.length);
		assertTrue("elements not added", CollectionTools.containsAll(newArray, a1));
		assertTrue("elements not added", CollectionTools.containsAll(newArray, a2));
	}
	
	public void testAddAllObjectArrayObjectArray2() {
		String[] a1 = this.buildStringArray1();
		String[] a2 = this.buildStringArray2();
		String[] newArray = (String[]) CollectionTools.addAll(a1, a2);
	
		assertEquals("invalid size", 6, newArray.length);
		assertTrue("elements not added", CollectionTools.containsAll(newArray, a1));
		assertTrue("elements not added", CollectionTools.containsAll(newArray, a2));
	}
	
	public void testAddAllObjectArrayIntObjectArray() {
		Object[] a = new Object[] {"a", "b", "c", "d"};
		a = CollectionTools.addAll(a, 2, new Object[] {"X", "X", "X"});
		assertEquals(7, a.length);
		assertTrue(CollectionTools.contains(a, "X"));
		assertTrue(Arrays.equals(new Object[] {"a", "b", "X", "X", "X", "c", "d"}, a));
	}
	
	public void testAddAllObjectArrayIntObjectArray2() {
		String[] a = new String[] {"a", "b", "c", "d"};
		a = (String[]) CollectionTools.addAll(a, 2, new String[] {"X", "X", "X"});
		assertEquals(7, a.length);
		assertTrue(CollectionTools.contains(a, "X"));
		assertTrue(Arrays.equals(new String[] {"a", "b", "X", "X", "X", "c", "d"}, a));
	}
	
	public void testAddAllCharArrayCharArray() {
		char[] a = CollectionTools.addAll(this.buildCharArray(), new char[]{'d', 'e'});
		assertEquals("invalid size", 5, a.length);
		assertTrue("element not present", CollectionTools.contains(a, 'd'));
		assertTrue("element not present", CollectionTools.contains(a, 'e'));
	}
	
	public void testAddAllCharArrayIntCharArray() {
		char[] a = new char[] {'a', 'b', 'c', 'd'};
		a = CollectionTools.addAll(a, 2, new char[] {'X', 'X', 'X'});
		assertEquals(7, a.length);
		assertTrue(CollectionTools.contains(a, 'X'));
		assertTrue(Arrays.equals(new char[] {'a', 'b', 'X', 'X', 'X', 'c', 'd'}, a));
	}
	
	public void testAddAllIntArrayIntArray() {
		int[] a = CollectionTools.addAll(this.buildIntArray(), new int[] {30, 40});
		assertEquals("invalid size", 5, a.length);
		assertTrue("element not present", CollectionTools.contains(a, 30));
		assertTrue("element not present", CollectionTools.contains(a, 40));
	}
	
	public void testAddAllIntArrayIntIntArray() {
		int[] a = new int[] {1, 2, 3, 4};
		a = CollectionTools.addAll(a, 2, new int[] {99, 99, 99});
		assertEquals(7, a.length);
		assertTrue(CollectionTools.contains(a, 99));
		assertTrue(Arrays.equals(new int[] {1, 2, 99, 99, 99, 3, 4}, a));
	}
	
	public void testArrayIterator() {
		Object[] a = CollectionTools.array(this.buildList1().iterator());
		assertEquals("invalid size", 3, a.length);
		assertTrue("elements not present", CollectionTools.containsAll(a, this.buildList1().iterator()));
	}
	
	public void testArrayIteratorObjectArray() {
		String[] a = (String[]) CollectionTools.array(this.buildList1().iterator(), new String[0]);
		assertEquals("invalid size", 3, a.length);
		assertTrue("elements not present", CollectionTools.containsAll(a, this.buildList1().iterator()));
	}
	
	public void testBagEnumeration() {
		Bag b = CollectionTools.bag(this.buildVector1().elements());
		assertEquals("invalid size", 3, b.size());
		assertTrue("elements not present", b.containsAll(this.buildVector1()));
	}
	
	public void testBagIterator() {
		Bag b = CollectionTools.bag(this.buildList1().iterator());
		assertEquals("invalid size", 3, b.size());
		assertTrue("elements not present", b.containsAll(this.buildList1()));
	}
	
	public void testBagObjectArray() {
		Bag b = CollectionTools.bag(this.buildArray1());
		assertEquals("invalid size", 3, b.size());
		assertTrue("elements not present", CollectionTools.containsAll(b, this.buildArray1()));
	}

	public void testCollectionEnumeration() {
		Collection c = CollectionTools.collection(this.buildVector1().elements());
		assertEquals("invalid size", 3, c.size());
		assertTrue("elements not present", c.containsAll(this.buildVector1()));
	}
	
	public void testCollectionIterator() {
		Collection c = CollectionTools.collection(this.buildList1().iterator());
		assertEquals("invalid size", 3, c.size());
		assertTrue("elements not present", c.containsAll(this.buildList1()));
	}
	
	public void testCollectionObjectArray() {
		Collection c = CollectionTools.collection(this.buildArray1());
		assertEquals("invalid size", 3, c.size());
		assertTrue("elements not present", CollectionTools.containsAll(c, this.buildArray1()));
	}

	public void testContainsIteratorObject() {
		Collection c = this.buildList1();
		assertTrue("element not present", CollectionTools.contains(c.iterator(), "one"));
		assertFalse("null present", CollectionTools.contains(c.iterator(), null));
		c.add(null);
		assertTrue("null not present", CollectionTools.contains(c.iterator(), null));
	}
	
	public void testContainsObjectArrayObject() {
		Object[] a = this.buildArray1();
		assertTrue("element not present", CollectionTools.contains(a, "one"));
		assertFalse("null present", CollectionTools.contains(a, null));
		Object[] a2 = CollectionTools.add(a, null);
		assertTrue("null not present", CollectionTools.contains(a2, null));
	}
	
	public void testContainsCharArrayChar() {
		char[] a = this.buildCharArray();
		assertTrue("element not present", CollectionTools.contains(a, 'a'));
		assertFalse("'z' present", CollectionTools.contains(a, 'z'));
		char[] a2 = CollectionTools.add(a, 'z');
		assertTrue("'z' not present", CollectionTools.contains(a2, 'z'));
	}
	
	public void testContainsIntArrayInt() {
		int[] a = this.buildIntArray();
		assertTrue("element not present", CollectionTools.contains(a, 10));
		assertFalse("55 present", CollectionTools.contains(a, 55));
		int[] a2 = CollectionTools.add(a, 55);
		assertTrue("55 not present", CollectionTools.contains(a2, 55));
	}
	
	public void testContainsAllCollectionIterator() {
		assertTrue("elements not present", CollectionTools.containsAll(this.buildList1(), this.buildList1().iterator()));
	}
	
	public void testContainsAllCollectionObjectArray() {
		assertTrue("elements not present", CollectionTools.containsAll(this.buildList1(), this.buildArray1()));
	}
	
	public void testContainsAllIteratorCollection() {
		assertTrue("elements not present", CollectionTools.containsAll(this.buildList1().iterator(), this.buildList1()));
	}
	
	public void testContainsAllIteratorIterator() {
		assertTrue("elements not present", CollectionTools.containsAll(this.buildList1().iterator(), this.buildList1().iterator()));
	}
	
	public void testContainsAllIteratorObjectArray() {
		assertTrue("elements not present", CollectionTools.containsAll(this.buildList1().iterator(), this.buildArray1()));
	}
	
	public void testContainsAllObjectArrayCollection() {
		assertTrue("elements not present", CollectionTools.containsAll(this.buildArray1(), this.buildList1()));
	}
	
	public void testContainsAllObjectArrayIterator() {
		assertTrue("elements not present", CollectionTools.containsAll(this.buildArray1(), this.buildList1().iterator()));
	}
	
	public void testContainsAllObjectArrayObjectArray() {
		assertTrue("elements not present", CollectionTools.containsAll(this.buildArray1(), this.buildArray1()));
	}
	
	public void testContainsAllCharArrayCharArray() {
		assertTrue("elements not present", CollectionTools.containsAll(this.buildCharArray(), this.buildCharArray()));
	}
	
	public void testContainsAllIntArrayIntArray() {
		assertTrue("elements not present", CollectionTools.containsAll(this.buildIntArray(), this.buildIntArray()));
	}
	
	public void testIdentityDiffEnd() {
		String a = "a";
		String b = "b";
		String c = "c";
		String d = "d";
		String e = "e";
		String a_ = a;
		String b_ = b;
		String c_ = c;
		String d_ = d;
		String e_ = e;
		assertTrue("invalid setup", (a == a_) && a.equals(a_));
		assertTrue("invalid setup", (b == b_) && b.equals(b_));
		assertTrue("invalid setup", (c == c_) && c.equals(c_));
		assertTrue("invalid setup", (d == d_) && d.equals(d_));
		assertTrue("invalid setup", (e == e_) && e.equals(e_));
		String[] array1;
		String[] array2;

		array1 = new String[] {a, b, c, d, e};
		array2 = new String[] {a_, b_, c_, d_, e_};
		assertEquals("invalid identity diff end", -1, CollectionTools.identityDiffEnd(array1, array2));

		array1 = new String[] {a};
		array2 = new String[] {a_};
		assertEquals("invalid identity diff end", -1, CollectionTools.identityDiffEnd(array1, array2));

		array1 = new String[] {b, c, d, e};
		array2 = new String[] {a_, b_, c_, d_, e_};
		assertEquals("invalid identity diff end", 4, CollectionTools.identityDiffEnd(array1, array2));

		array1 = new String[] {a, b, c, d, e};
		array2 = new String[] {b_, c_, d_, e_};
		assertEquals("invalid identity diff end", 4, CollectionTools.identityDiffEnd(array1, array2));

		array1 = new String[0];
		array2 = new String[] {a_, b_, c_, d_, e_};
		assertEquals("invalid identity diff end", 4, CollectionTools.identityDiffEnd(array1, array2));

		array1 = new String[] {a, b, c, d, e};
		array2 = new String[0];
		assertEquals("invalid identity diff end", 4, CollectionTools.identityDiffEnd(array1, array2));

		array1 = new String[0];
		array2 = new String[0];
		assertEquals("invalid identity diff end", -1, CollectionTools.identityDiffEnd(array1, array2));

		array1 = new String[] {a, b, c, d, e};
		array2 = new String[] {b_, c_, a_, d_, e_};
		assertEquals("invalid identity diff end", 2, CollectionTools.identityDiffEnd(array1, array2));

		array1 = new String[] {b, c, d, e};
		array2 = new String[] {a_, c_, d_, e_};
		assertEquals("invalid identity diff end", 0, CollectionTools.identityDiffEnd(array1, array2));

		array1 = new String[] {a, b, c, e};
		array2 = new String[] {a_, b_, c_, d_};
		assertEquals("invalid identity diff end", 3, CollectionTools.identityDiffEnd(array1, array2));

		String c__ = new String(c);
		assertTrue("invalid setup", (c != c__) && c.equals(c_));
		array1 = new String[] {a, b, c, d, e};
		array2 = new String[] {a_, b_, c__, d_, e_};
		assertEquals("invalid identity diff end", 2, CollectionTools.identityDiffEnd(array1, array2));

		array1 = new String[] {a, b, null, d, e};
		array2 = new String[] {a_, b_, c_, d_, e_};
		assertEquals("invalid identity diff end", 2, CollectionTools.identityDiffEnd(array1, array2));

		array1 = new String[] {a, b, null, d, e};
		array2 = new String[] {a_, b_, null, d_, e_};
		assertEquals("invalid identity diff end", -1, CollectionTools.identityDiffEnd(array1, array2));
	}
	
	public void testIdentityDiffStart() {
		String a = "a";
		String b = "b";
		String c = "c";
		String d = "d";
		String e = "e";
		String a_ = a;
		String b_ = b;
		String c_ = c;
		String d_ = d;
		String e_ = e;
		assertTrue("invalid setup", (a == a_) && a.equals(a_));
		assertTrue("invalid setup", (b == b_) && b.equals(b_));
		assertTrue("invalid setup", (c == c_) && c.equals(c_));
		assertTrue("invalid setup", (d == d_) && d.equals(d_));
		assertTrue("invalid setup", (e == e_) && e.equals(e_));
		String[] array1;
		String[] array2;

		array1 = new String[] {a, b, c, d, e};
		array2 = new String[] {a_, b_, c_, d_, e_};
		assertEquals("invalid identity diff start", 5, CollectionTools.identityDiffStart(array1, array2));

		array1 = new String[] {a};
		array2 = new String[] {a_};
		assertEquals("invalid identity diff start", 1, CollectionTools.identityDiffStart(array1, array2));

		array1 = new String[] {a, b, c, d};
		array2 = new String[] {a_, b_, c_, d_, e_};
		assertEquals("invalid identity diff start", 4, CollectionTools.identityDiffStart(array1, array2));

		array1 = new String[] {a, b, c, d, e};
		array2 = new String[] {a_, b_, c_, d_};
		assertEquals("invalid identity diff start", 4, CollectionTools.identityDiffStart(array1, array2));

		array1 = new String[0];
		array2 = new String[] {a_, b_, c_, d_, e_};
		assertEquals("invalid identity diff start", 0, CollectionTools.identityDiffStart(array1, array2));

		array1 = new String[] {a, b, c, d, e};
		array2 = new String[0];
		assertEquals("invalid identity diff start", 0, CollectionTools.identityDiffStart(array1, array2));

		array1 = new String[0];
		array2 = new String[0];
		assertEquals("invalid identity diff start", 0, CollectionTools.identityDiffStart(array1, array2));

		array1 = new String[] {a, b, c, d, e};
		array2 = new String[] {a_, b_, e_, c_, d_};
		assertEquals("invalid identity diff start", 2, CollectionTools.identityDiffStart(array1, array2));

		array1 = new String[] {a, b, c, e};
		array2 = new String[] {a_, b_, c_, d_};
		assertEquals("invalid identity diff start", 3, CollectionTools.identityDiffStart(array1, array2));

		array1 = new String[] {b, c, d, e};
		array2 = new String[] {a_, c_, d_, e_};
		assertEquals("invalid identity diff start", 0, CollectionTools.identityDiffStart(array1, array2));

		String c__ = new String(c);
		assertTrue("invalid setup", (c != c__) && c.equals(c_));
		array1 = new String[] {a, b, c, d, e};
		array2 = new String[] {a_, b_, c__, d_, e_};
		assertEquals("invalid identity diff start", 2, CollectionTools.identityDiffStart(array1, array2));

		array1 = new String[] {a, b, null, d, e};
		array2 = new String[] {a_, b_, c_, d_, e_};
		assertEquals("invalid identity diff start", 2, CollectionTools.identityDiffStart(array1, array2));

		array1 = new String[] {a, b, null, d, e};
		array2 = new String[] {a_, b_, null, d_, e_};
		assertEquals("invalid identity diff start", 5, CollectionTools.identityDiffStart(array1, array2));
	}
	
	public void testDiffEnd() {
		String a = "a";
		String b = "b";
		String c = "c";
		String d = "d";
		String e = "e";
		String a_ = new String("a");
		String b_ = new String("b");
		String c_ = new String("c");
		String d_ = new String("d");
		String e_ = new String("e");
		assertTrue("invalid setup", (a != a_) && a.equals(a_));
		assertTrue("invalid setup", (b != b_) && b.equals(b_));
		assertTrue("invalid setup", (c != c_) && c.equals(c_));
		assertTrue("invalid setup", (d != d_) && d.equals(d_));
		assertTrue("invalid setup", (e != e_) && e.equals(e_));
		String[] array1;
		String[] array2;

		array1 = new String[] {a, b, c, d, e};
		array2 = new String[] {a_, b_, c_, d_, e_};
		assertEquals("invalid diff end", -1, CollectionTools.diffEnd(array1, array2));

		array1 = new String[] {a};
		array2 = new String[] {a_};
		assertEquals("invalid diff end", -1, CollectionTools.diffEnd(array1, array2));

		array1 = new String[] {b, c, d, e};
		array2 = new String[] {a_, b_, c_, d_, e_};
		assertEquals("invalid diff end", 4, CollectionTools.diffEnd(array1, array2));

		array1 = new String[] {a, b, c, d, e};
		array2 = new String[] {b_, c_, d_, e_};
		assertEquals("invalid diff end", 4, CollectionTools.diffEnd(array1, array2));

		array1 = new String[0];
		array2 = new String[] {a_, b_, c_, d_, e_};
		assertEquals("invalid diff end", 4, CollectionTools.diffEnd(array1, array2));

		array1 = new String[] {a, b, c, d, e};
		array2 = new String[0];
		assertEquals("invalid diff end", 4, CollectionTools.diffEnd(array1, array2));

		array1 = new String[0];
		array2 = new String[0];
		assertEquals("invalid diff end", -1, CollectionTools.diffEnd(array1, array2));

		array1 = new String[] {a, b, c, d, e};
		array2 = new String[] {b_, c_, a_, d_, e_};
		assertEquals("invalid diff end", 2, CollectionTools.diffEnd(array1, array2));

		array1 = new String[] {b, c, d, e};
		array2 = new String[] {a_, c_, d_, e_};
		assertEquals("invalid diff end", 0, CollectionTools.diffEnd(array1, array2));

		array1 = new String[] {a, b, c, e};
		array2 = new String[] {a_, b_, c_, d_};
		assertEquals("invalid diff end", 3, CollectionTools.diffEnd(array1, array2));

		String c__ = new String(c);
		assertTrue("invalid setup", (c != c__) && c.equals(c_));
		array1 = new String[] {a, b, c, d, e};
		array2 = new String[] {a_, b_, c__, d_, e_};
		assertEquals("invalid diff end", -1, CollectionTools.diffEnd(array1, array2));

		array1 = new String[] {a, b, null, d, e};
		array2 = new String[] {a_, b_, c_, d_, e_};
		assertEquals("invalid diff end", 2, CollectionTools.diffEnd(array1, array2));

		array1 = new String[] {a, b, null, d, e};
		array2 = new String[] {a_, b_, null, d_, e_};
		assertEquals("invalid diff end", -1, CollectionTools.diffEnd(array1, array2));
	}
	
	public void testDiffStart() {
		String a = "a";
		String b = "b";
		String c = "c";
		String d = "d";
		String e = "e";
		String a_ = new String("a");
		String b_ = new String("b");
		String c_ = new String("c");
		String d_ = new String("d");
		String e_ = new String("e");
		assertTrue("invalid setup", (a != a_) && a.equals(a_));
		assertTrue("invalid setup", (b != b_) && b.equals(b_));
		assertTrue("invalid setup", (c != c_) && c.equals(c_));
		assertTrue("invalid setup", (d != d_) && d.equals(d_));
		assertTrue("invalid setup", (e != e_) && e.equals(e_));
		String[] array1;
		String[] array2;

		array1 = new String[] {a, b, c, d, e};
		array2 = new String[] {a_, b_, c_, d_, e_};
		assertEquals("invalid diff start", 5, CollectionTools.diffStart(array1, array2));

		array1 = new String[] {a};
		array2 = new String[] {a_};
		assertEquals("invalid diff start", 1, CollectionTools.diffStart(array1, array2));

		array1 = new String[] {a, b, c, d};
		array2 = new String[] {a_, b_, c_, d_, e_};
		assertEquals("invalid diff start", 4, CollectionTools.diffStart(array1, array2));

		array1 = new String[] {a, b, c, d, e};
		array2 = new String[] {a_, b_, c_, d_};
		assertEquals("invalid diff start", 4, CollectionTools.diffStart(array1, array2));

		array1 = new String[0];
		array2 = new String[] {a_, b_, c_, d_, e_};
		assertEquals("invalid diff start", 0, CollectionTools.diffStart(array1, array2));

		array1 = new String[] {a, b, c, d, e};
		array2 = new String[0];
		assertEquals("invalid diff start", 0, CollectionTools.diffStart(array1, array2));

		array1 = new String[0];
		array2 = new String[0];
		assertEquals("invalid diff start", 0, CollectionTools.diffStart(array1, array2));

		array1 = new String[] {a, b, c, d, e};
		array2 = new String[] {a_, b_, e_, c_, d_};
		assertEquals("invalid diff start", 2, CollectionTools.diffStart(array1, array2));

		array1 = new String[] {a, b, c, e};
		array2 = new String[] {a_, b_, c_, d_};
		assertEquals("invalid diff start", 3, CollectionTools.diffStart(array1, array2));

		array1 = new String[] {b, c, d, e};
		array2 = new String[] {a_, c_, d_, e_};
		assertEquals("invalid diff start", 0, CollectionTools.diffStart(array1, array2));

		String c__ = new String(c);
		assertTrue("invalid setup", (c != c__) && c.equals(c__));
		array1 = new String[] {a, b, c, d, e};
		array2 = new String[] {a_, b_, c__, d_, e_};
		assertEquals("invalid diff start", 5, CollectionTools.diffStart(array1, array2));

		array1 = new String[] {a, b, null, d, e};
		array2 = new String[] {a_, b_, c_, d_, e_};
		assertEquals("invalid diff start", 2, CollectionTools.diffStart(array1, array2));

		array1 = new String[] {a, b, null, d, e};
		array2 = new String[] {a_, b_, null, d_, e_};
		assertEquals("invalid diff start", 5, CollectionTools.diffStart(array1, array2));
	}
	
	public void testEqualsListIteratorListIterator() {
		List list1 = new ArrayList();
		list1.add("1000");
		list1.add("2000");
		list1.add("3000");
		list1.add("4000");

		List list2 = new ArrayList();
		for (int i = 1; i < list1.size() + 1; i++) {
			list2.add(String.valueOf(i * 1000));
		}
		assertFalse(CollectionTools.identical(list1.listIterator(), list2.listIterator()));
		assertTrue(CollectionTools.equals(list1.listIterator(), list2.listIterator()));
	}

	public void testGetListIteratorInt() {
		List list = this.buildList1();
		assertEquals("invalid object", "one", CollectionTools.get(list.listIterator(), 1));
		list.add(null);
		assertEquals("invalid object", null, CollectionTools.get(list.listIterator(), list.size() - 1));

		boolean exCaught = false;
		try {
			CollectionTools.get(list.listIterator(), list.size());
		} catch (IndexOutOfBoundsException ex) {
			exCaught = true;
		}
		assertTrue("IndexOutOfBoundsException not thrown", exCaught);
	}
	
	public void testIdenticalObjectArrayObjectArray() {
		Object[] a1 = new Object[4];
		for (int i = 0; i < a1.length; i++) {
			a1[i] = String.valueOf(i * 1000);
		}

		Object[] a2 = new Object[a1.length];
		for (int i = 0; i < a2.length; i++) {
			a2[i] = a1[i];
		}

		assertTrue(CollectionTools.identical(a1, a2));
		a2[2] = "2000";
		assertFalse(CollectionTools.identical(a1, a2));
		assertTrue(Arrays.equals(a1, a2));
	}

	public void testIdenticalListIteratorListIterator() {
		List list1 = new ArrayList();
		list1.add("0");
		list1.add("1");
		list1.add("2");
		list1.add("3");

		List list2 = new ArrayList();
		for (int i = 0; i < list1.size(); i++) {
			list2.add(list1.get(i));
		}
		assertTrue(CollectionTools.identical(list1.listIterator(), list2.listIterator()));
		assertTrue(CollectionTools.equals(list1.listIterator(), list2.listIterator()));
	}

	public void testIndexOfListIteratorObject() {
		List list = this.buildList1();
		assertEquals("invalid index", 1, CollectionTools.indexOf(list.listIterator(), "one"));
		list.add(null);
		assertEquals("invalid index", list.size() - 1, CollectionTools.indexOf(list.listIterator(), null));
	}
	
	public void testIndexOfObjectArrayObject() {
		Object[] a = this.buildArray1();
		assertEquals("invalid index", 1, CollectionTools.indexOf(a, "one"));
		a = CollectionTools.add(a, null);
		assertEquals("invalid index", a.length - 1, CollectionTools.indexOf(a, null));
	}
	
	public void testIndexOfCharArrayChar() {
		char[] a = this.buildCharArray();
		assertEquals(1, CollectionTools.indexOf(a, 'b'));
		a = CollectionTools.add(a, 'd');
		assertEquals(a.length - 1, CollectionTools.indexOf(a, 'd'));
	}
	
	public void testIndexOfIntArrayInt() {
		int[] a = this.buildIntArray();
		assertEquals("invalid index", 1, CollectionTools.indexOf(a, 10));
		a = CollectionTools.add(a, 30);
		assertEquals("invalid index", a.length - 1, CollectionTools.indexOf(a, 30));
	}
	
	public void testInsertionIndexOfListComparable() {
		List list = Arrays.asList(new Object[] {"A", "C", "D"});
		assertEquals(1, CollectionTools.insertionIndexOf(list, "B"));

		list = Arrays.asList(new Object[] {"A", "B", "C", "D"});
		assertEquals(2, CollectionTools.insertionIndexOf(list, "B"));

		list = Arrays.asList(new Object[] {"A", "B", "B", "B", "C", "D"});
		assertEquals(4, CollectionTools.insertionIndexOf(list, "B"));

		list = Arrays.asList(new Object[] {"A", "B", "B", "B", "C", "D"});
		assertEquals(6, CollectionTools.insertionIndexOf(list, "E"));

		list = Arrays.asList(new Object[] {"B", "B", "B", "C", "D"});
		assertEquals(0, CollectionTools.insertionIndexOf(list, "A"));

		list = Arrays.asList(new Object[] {"A", "A", "B", "B", "C", "D"});
		assertEquals(2, CollectionTools.insertionIndexOf(list, "A"));
	}
	
	public void testInsertionIndexOfListObjectComparator() {
		Comparator c = new ReverseComparator();
		List list = Arrays.asList(new Object[] {"D", "C", "A"});
		assertEquals(2, CollectionTools.insertionIndexOf(list, "B", c));

		list = Arrays.asList(new Object[] {"D", "C", "B", "A"});
		assertEquals(3, CollectionTools.insertionIndexOf(list, "B", c));

		list = Arrays.asList(new Object[] {"D", "C", "B", "B", "B", "A"});
		assertEquals(5, CollectionTools.insertionIndexOf(list, "B", c));

		list = Arrays.asList(new Object[] {"D", "C", "B", "B", "B", "A"});
		assertEquals(0, CollectionTools.insertionIndexOf(list, "E", c));

		list = Arrays.asList(new Object[] {"D", "C", "B", "B", "B"});
		assertEquals(5, CollectionTools.insertionIndexOf(list, "A", c));

		list = Arrays.asList(new Object[] {"D", "C", "B", "B", "A", "A"});
		assertEquals(6, CollectionTools.insertionIndexOf(list, "A", c));
	}
	
	public void testInsertionIndexOfObjectArrayComparable() {
		Object[] a = new Object[] {"A", "C", "D"};
		assertEquals(1, CollectionTools.insertionIndexOf(a, "B"));

		a = new Object[] {"A", "B", "C", "D"};
		assertEquals(2, CollectionTools.insertionIndexOf(a, "B"));

		a = new Object[] {"A", "B", "B", "B", "C", "D"};
		assertEquals(4, CollectionTools.insertionIndexOf(a, "B"));

		a = new Object[] {"A", "B", "B", "B", "C", "D"};
		assertEquals(6, CollectionTools.insertionIndexOf(a, "E"));

		a = new Object[] {"B", "B", "B", "C", "D"};
		assertEquals(0, CollectionTools.insertionIndexOf(a, "A"));

		a = new Object[] {"A", "A", "B", "B", "C", "D"};
		assertEquals(2, CollectionTools.insertionIndexOf(a, "A"));
	}
	
	public void testInsertionIndexOfObjectArrayObjectComparator() {
		Comparator c = new ReverseComparator();
		Object[] a = new Object[] {"D", "C", "A"};
		assertEquals(2, CollectionTools.insertionIndexOf(a, "B", c));

		a = new Object[] {"D", "C", "B", "A"};
		assertEquals(3, CollectionTools.insertionIndexOf(a, "B", c));

		a = new Object[] {"D", "C", "B", "B", "B", "A"};
		assertEquals(5, CollectionTools.insertionIndexOf(a, "B", c));

		a = new Object[] {"D", "C", "B", "B", "B", "A"};
		assertEquals(0, CollectionTools.insertionIndexOf(a, "E", c));

		a = new Object[] {"D", "C", "B", "B", "B"};
		assertEquals(5, CollectionTools.insertionIndexOf(a, "A", c));

		a = new Object[] {"D", "C", "B", "B", "A", "A"};
		assertEquals(6, CollectionTools.insertionIndexOf(a, "A", c));
	}
	
	public void testIteratorObjectArray() {
		Object[] a = this.buildArray1();
		int i = 0;
		for (Iterator stream = CollectionTools.iterator(a); stream.hasNext(); i++) {
			assertEquals("invalid element", a[i], stream.next());
		}
	}
	
	public void testLastIndexOfListIteratorObject() {
		List list = this.buildList1();
		assertEquals("invalid index", 1, CollectionTools.lastIndexOf(list.listIterator(), "one"));
		list.add(null);
		assertEquals("invalid index", list.size() - 1, CollectionTools.lastIndexOf(list.listIterator(), null));
	}
	
	public void testLastIndexOfObjectArrayObject() {
		Object[] a = this.buildArray1();
		assertEquals("invalid index", 1, CollectionTools.lastIndexOf(a, "one"));
		a = CollectionTools.add(a, null);
		assertEquals("invalid index", a.length - 1, CollectionTools.lastIndexOf(a, null));
	}
	
	public void testLastIndexOfCharArrayChar() {
		char[] a = this.buildCharArray();
		assertEquals("invalid index", 1, CollectionTools.lastIndexOf(a, 'b'));
		a = CollectionTools.add(a, 'd');
		assertEquals("invalid index", a.length - 1, CollectionTools.lastIndexOf(a, 'd'));
	}
	
	public void testLastIndexOfIntArrayInt() {
		int[] a = this.buildIntArray();
		assertEquals("invalid index", 1, CollectionTools.lastIndexOf(a, 10));
		a = CollectionTools.add(a, 30);
		assertEquals("invalid index", a.length - 1, CollectionTools.lastIndexOf(a, 30));
	}
	
	public void testListIterator() {
		List list = CollectionTools.list(this.buildList1().iterator());
		assertEquals("invalid list", this.buildList1(), list);
	}
	
	public void testListObjectArray() {
		List list = CollectionTools.list(this.buildArray1());
		assertEquals("invalid list", this.buildList1(), list);
	}
	
	public void testListIteratorObjectArray() {
		Object[] a = this.buildArray1();
		int i = 0;
		for (ListIterator stream = CollectionTools.listIterator(a); stream.hasNext(); i++) {
			assertEquals("invalid element", a[i], stream.next());
		}
	}
	
	public void testListIteratorObjectArrayInt() {
		Object[] a = this.buildArray1();
		int i = 1;
		for (ListIterator stream = CollectionTools.listIterator(a, 1); stream.hasNext(); i++) {
			assertEquals("invalid element", a[i], stream.next());
		}
	}

	public void testMaxCharArray() {
		assertEquals('c', CollectionTools.max(this.buildCharArray()));
	}
	
	public void testMaxIntArray() {
		assertEquals(20, CollectionTools.max(this.buildIntArray()));
	}
	
	public void testMinCharArray() {
		assertEquals('a', CollectionTools.min(this.buildCharArray()));
	}
	
	public void testMinIntArray() {
		assertEquals(0, CollectionTools.min(this.buildIntArray()));
	}

	public void testRemoveAllObjectArrayObjectArray() {
		Object[] a1 = new Object[] {"A", "A", "B","B", "C", "C", "D", "D", "E", "E", "F", "F"};
		Object[] a2 = new Object[] {"E", "B"};
		assertTrue(Arrays.equals(new Object[] {"A", "A", "C", "C", "D", "D", "F", "F"}, CollectionTools.removeAll(a1, a2)));
	}
	
	public void testRemoveAllCharArrayCharArray() {
		char[] a1 = new char[] {'A', 'A', 'B','B', 'C', 'C', 'D', 'D', 'E', 'E', 'F', 'F'};
		char[] a2 = new char[] {'E', 'B'};
		assertTrue(Arrays.equals(new char[] {'A', 'A', 'C', 'C', 'D', 'D', 'F', 'F'}, CollectionTools.removeAll(a1, a2)));
	}
	
	public void testRemoveAllIntArrayIntArray() {
		int[] a1 = new int[] {1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6};
		int[] a2 = new int[] {5, 2};
		assertTrue(Arrays.equals(new int[] {1, 1, 3, 3, 4, 4, 6, 6}, CollectionTools.removeAll(a1, a2)));
	}
	
	public void testRemoveObjectArrayObject() {
		Object[] a = this.buildArray1();
		a = CollectionTools.add(a, "three");
		a = CollectionTools.add(a, "four");
		a = CollectionTools.add(a, "five");

		assertEquals(6, a.length);
		assertTrue(CollectionTools.contains(a, "three"));
		a = CollectionTools.remove(a, "three");
		assertEquals(5, a.length);
		assertFalse(CollectionTools.contains(a, "three"));
		assertTrue(CollectionTools.contains(a, "four"));
		assertTrue(CollectionTools.contains(a, "five"));
	}
	
	public void testRemoveObjectArrayObject2() {
		String[] a = this.buildStringArray1();
		a = (String[]) CollectionTools.add(a, "three");
		a = (String[]) CollectionTools.add(a, "four");
		a = (String[]) CollectionTools.add(a, "five");

		assertEquals(6, a.length);
		assertTrue(CollectionTools.contains(a, "three"));
		a = (String[]) CollectionTools.remove(a, "three");
		assertEquals(5, a.length);
		assertFalse(CollectionTools.contains(a, "three"));
		assertTrue(CollectionTools.contains(a, "four"));
		assertTrue(CollectionTools.contains(a, "five"));
	}
	
	public void testRemoveCharArrayChar() {
		char[] a = this.buildCharArray();
		a = CollectionTools.add(a, 'd');
		a = CollectionTools.add(a, 'e');
		a = CollectionTools.add(a, 'f');

		assertEquals(6, a.length);
		assertTrue(CollectionTools.contains(a, 'd'));
		a = CollectionTools.remove(a, 'd');
		assertEquals(5, a.length);
		assertFalse(CollectionTools.contains(a, 'd'));
		assertTrue(CollectionTools.contains(a, 'e'));
		assertTrue(CollectionTools.contains(a, 'f'));
	}
	
	public void testRemoveIntArrayInt() {
		int[] a = this.buildIntArray();
		a = CollectionTools.add(a, 30);
		a = CollectionTools.add(a, 40);
		a = CollectionTools.add(a, 50);

		assertEquals(6, a.length);
		assertTrue(CollectionTools.contains(a, 30));
		a = CollectionTools.remove(a, 30);
		assertEquals(5, a.length);
		assertFalse(CollectionTools.contains(a, 30));
		assertTrue(CollectionTools.contains(a, 40));
		assertTrue(CollectionTools.contains(a, 50));
	}
	
	public void testRemoveAllCollectionIterator() {
		Collection c = this.buildList1();
		assertTrue("invalid return value", CollectionTools.removeAll(c, this.buildList1().iterator()));
		assertEquals("invalid size", 0, c.size());
		assertFalse("element present", c.contains("one"));
		assertFalse("element present", c.contains("two"));
		assertFalse("element present", c.contains("three"));
	
		c = this.buildList1();
		assertFalse("invalid return value", CollectionTools.removeAll(c, this.buildList2().iterator()));
		assertEquals("invalid size", this.buildList1().size(), c.size());
		assertEquals("elements removed", this.buildList1(), c);
	}
	
	public void testRemoveAllCollectionObjectArray() {
		Collection c = this.buildList1();
		assertTrue("invalid return value", CollectionTools.removeAll(c, this.buildArray1()));
		assertEquals("invalid size", 0, c.size());
		assertFalse("element present", c.contains("one"));
		assertFalse("element present", c.contains("two"));
		assertFalse("element present", c.contains("three"));
	
		c = this.buildList1();
		assertFalse("invalid return value", CollectionTools.removeAll(c, this.buildArray2()));
		assertEquals("invalid size", this.buildList1().size(), c.size());
		assertEquals("elements removed", this.buildList1(), c);
	}

	public void testRemoveAllOccurrencesCollectionObject() {
		Collection c = this.buildList1();
		assertEquals(3, c.size());
		assertFalse(CollectionTools.removeAllOccurrences(c, "three"));
		assertTrue(CollectionTools.removeAllOccurrences(c, "two"));
		assertFalse(CollectionTools.removeAllOccurrences(c, "two"));
		assertEquals(2, c.size());

		c.add("five");
		c.add("five");
		c.add("five");
		assertEquals(5, c.size());
		assertTrue(CollectionTools.removeAllOccurrences(c, "five"));
		assertFalse(CollectionTools.removeAllOccurrences(c, "five"));
		assertEquals(2, c.size());

		c.add(null);
		c.add(null);
		c.add(null);
		assertEquals(5, c.size());
		assertTrue(CollectionTools.removeAllOccurrences(c, null));
		assertFalse(CollectionTools.removeAllOccurrences(c, null));
		assertEquals(2, c.size());
	}

	public void testRemoveAllOccurrencesObjectArrayObject() {
		Object[] a = this.buildArray1();
		assertEquals(3, a.length);
		a = CollectionTools.removeAllOccurrences(a, "three");
		assertEquals(3, a.length);
		a = CollectionTools.removeAllOccurrences(a, "two");
		assertEquals(2, a.length);
		a = CollectionTools.removeAllOccurrences(a, "two");
		assertEquals(2, a.length);

		a = CollectionTools.add(a, "five");
		a = CollectionTools.add(a, "five");
		a = CollectionTools.add(a, "five");
		assertEquals(5, a.length);
		a = CollectionTools.removeAllOccurrences(a, "five");
		assertEquals(2, a.length);
		a = CollectionTools.removeAllOccurrences(a, "five");
		assertEquals(2, a.length);

		a = CollectionTools.add(a, null);
		a = CollectionTools.add(a, null);
		a = CollectionTools.add(a, null);
		assertEquals(5, a.length);
		a = CollectionTools.removeAllOccurrences(a, null);
		assertEquals(2, a.length);
		a = CollectionTools.removeAllOccurrences(a, null);
		assertEquals(2, a.length);
	}

	public void testRemoveAllOccurrencesCharArrayChar() {
		char[] a = this.buildCharArray();
		assertEquals(3, a.length);
		a = CollectionTools.removeAllOccurrences(a, 'd');
		assertEquals(3, a.length);
		a = CollectionTools.removeAllOccurrences(a, 'b');
		assertEquals(2, a.length);
		a = CollectionTools.removeAllOccurrences(a, 'b');
		assertEquals(2, a.length);

		a = CollectionTools.add(a, 'g');
		a = CollectionTools.add(a, 'g');
		a = CollectionTools.add(a, 'g');
		assertEquals(5, a.length);
		a = CollectionTools.removeAllOccurrences(a, 'g');
		assertEquals(2, a.length);
		a = CollectionTools.removeAllOccurrences(a, 'g');
		assertEquals(2, a.length);
	}

	public void testRemoveAllOccurrencesIntArrayInt() {
		int[] a = this.buildIntArray();
		assertEquals(3, a.length);
		a = CollectionTools.removeAllOccurrences(a, 55);
		assertEquals(3, a.length);
		a = CollectionTools.removeAllOccurrences(a, 10);
		assertEquals(2, a.length);
		a = CollectionTools.removeAllOccurrences(a, 10);
		assertEquals(2, a.length);

		a = CollectionTools.add(a, 77);
		a = CollectionTools.add(a, 77);
		a = CollectionTools.add(a, 77);
		assertEquals(5, a.length);
		a = CollectionTools.removeAllOccurrences(a, 77);
		assertEquals(2, a.length);
		a = CollectionTools.removeAllOccurrences(a, 77);
		assertEquals(2, a.length);
	}

	public void testReplaceAllObjectArray() {
		Object[] a = new Object[] {"A", "B", "A", "C", "A", "D"};
		a = CollectionTools.replaceAll(a, "A", "Z");
		assertTrue(Arrays.equals(new Object[] {"Z", "B", "Z", "C", "Z", "D"}, a));
	}

	public void testReplaceAllCharArray() {
		char[] a = new char[] {'A', 'B', 'A', 'C', 'A', 'D'};
		a = CollectionTools.replaceAll(a, 'A', 'Z');
		assertTrue(Arrays.equals(new char[] {'Z', 'B', 'Z', 'C', 'Z', 'D'}, a));
	}

	public void testReplaceAllIntArray() {
		int[] a = new int[] {0, 1, 0, 7, 0, 99};
		a = CollectionTools.replaceAll(a, 0, 13);
		assertTrue(Arrays.equals(new int[] {13, 1, 13, 7, 13, 99}, a));
	}

	public void testRetainAllCollectionIterator() {
		Collection c = this.buildList1();
		assertFalse("invalid return value", CollectionTools.retainAll(c, this.buildList1().iterator()));
		assertEquals("invalid size", this.buildList1().size(), c.size());
		assertEquals("elements removed", this.buildList1(), c);
	
		assertTrue("invalid return value", CollectionTools.retainAll(c, this.buildList2().iterator()));
		assertEquals("invalid size", 0, c.size());
		assertFalse("element present", c.contains("one"));
		assertFalse("element present", c.contains("two"));
		assertFalse("element present", c.contains("three"));
	}
	
	public void testRetainAllCollectionObjectArray() {
		Collection c = this.buildList1();
		assertFalse("invalid return value", CollectionTools.retainAll(c, this.buildArray1()));
		assertEquals("invalid size", this.buildList1().size(), c.size());
		assertEquals("elements removed", this.buildList1(), c);
	
		assertTrue("invalid return value", CollectionTools.retainAll(c, this.buildArray2()));
		assertEquals("invalid size", 0, c.size());
		assertFalse("element present", c.contains("one"));
		assertFalse("element present", c.contains("two"));
		assertFalse("element present", c.contains("three"));
	}
	
	public void testRetainAllObjectArrayObjectArray() {
		Object[] a1 = new Object[] {"A", "A", "B", "B", "C", "C", "D", "D", "E", "E", "F", "F"};
		Object[] a2 = new Object[] {"E", "B"};
		assertTrue(Arrays.equals(new Object[] {"B", "B", "E", "E"}, CollectionTools.retainAll(a1, a2)));
	}
	
	public void testRetainAllCharArrayCharArray() {
		char[] a1 = new char[] {'A', 'A', 'B', 'B', 'C', 'C', 'D', 'D', 'E', 'E', 'F', 'F'};
		char[] a2 = new char[] {'E', 'B'};
		assertTrue(Arrays.equals(new char[] {'B', 'B', 'E', 'E'}, CollectionTools.retainAll(a1, a2)));
	}
	
	public void testRetainAllIntArrayIntArray() {
		int[] a1 = new int[] {1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6};
		int[] a2 = new int[] {5, 2};
		assertTrue(Arrays.equals(new int[] {2, 2, 5, 5}, CollectionTools.retainAll(a1, a2)));
	}
	
	public void testReverseObjectArray() {
		Object[] a = this.buildArray1();
		a = CollectionTools.reverse(a);
		assertEquals("two", a[0]);
		assertEquals("one", a[1]);
		assertEquals("zero", a[2]);
	}

	public void testReverseCharArray() {
		char[] a = this.buildCharArray();
		a = CollectionTools.reverse(a);
		assertEquals('c', a[0]);
		assertEquals('b', a[1]);
		assertEquals('a', a[2]);
	}

	public void testReverseIntArray() {
		int[] a = this.buildIntArray();
		a = CollectionTools.reverse(a);
		assertEquals(20, a[0]);
		assertEquals(10, a[1]);
		assertEquals(0, a[2]);
	}

	public void testReverseListIterator() {
		List actual = CollectionTools.reverseList(this.buildList1().iterator());
		List expected = this.buildList1();
		Collections.reverse(expected);
		assertEquals("invalid reverse list", expected, actual);
	}
	
	public void testRotateObjectArray() {
		Object[] a = this.buildArray1();
		a = CollectionTools.rotate(a);
		assertEquals("two", a[0]);
		assertEquals("zero", a[1]);
		assertEquals("one", a[2]);
	}

	public void testRotateObjectArrayInt() {
		Object[] a = this.buildArray1();
		a = CollectionTools.rotate(a, 2);
		assertEquals("one", a[0]);
		assertEquals("two", a[1]);
		assertEquals("zero", a[2]);
	}

	public void testRotateCharArray() {
		char[] a = this.buildCharArray();
		a = CollectionTools.rotate(a);
		assertEquals('c', a[0]);
		assertEquals('a', a[1]);
		assertEquals('b', a[2]);
	}

	public void testRotateCharArrayInt() {
		char[] a = this.buildCharArray();
		a = CollectionTools.rotate(a, 2);
		assertEquals('b', a[0]);
		assertEquals('c', a[1]);
		assertEquals('a', a[2]);
	}

	public void testRotateIntArray() {
		int[] a = this.buildIntArray();
		a = CollectionTools.rotate(a);
		assertEquals(20, a[0]);
		assertEquals(0, a[1]);
		assertEquals(10, a[2]);
	}

	public void testRotateIntArrayInt() {
		int[] a = this.buildIntArray();
		a = CollectionTools.rotate(a, 2);
		assertEquals(10, a[0]);
		assertEquals(20, a[1]);
		assertEquals(0, a[2]);
	}

	public void testSetIterator() {
		assertEquals("incorrect Set", this.buildSet1(), CollectionTools.set(this.buildSet1().iterator()));
	}
	
	public void testSetObjectArray() {
		assertEquals("incorrect Set", this.buildSet1(), CollectionTools.set(this.buildSet1().toArray()));
	}
	
	public void testSortedSetIterator() {
		assertEquals("incorrect SortedSet", this.buildSortedSet1(), CollectionTools.set(this.buildSortedSet1().iterator()));
	}
	
	public void testSortedSetObjectArray() {
		assertEquals("incorrect SortedSet", this.buildSortedSet1(), CollectionTools.set(this.buildSortedSet1().toArray()));
	}
	
	public void testSwapObjectArray() {
		Object[] a = this.buildArray1();
		a = CollectionTools.swap(a, 1, 2);
		assertEquals("zero", a[0]);
		assertEquals("two", a[1]);
		assertEquals("one", a[2]);
	}
	
	public void testSwapCharArray() {
		char[] a = this.buildCharArray();
		a = CollectionTools.swap(a, 1, 2);
		assertEquals('a', a[0]);
		assertEquals('c', a[1]);
		assertEquals('b', a[2]);
	}
	
	public void testSwapIntArray() {
		int[] a = this.buildIntArray();
		a = CollectionTools.swap(a, 1, 2);
		assertEquals(0, a[0]);
		assertEquals(20, a[1]);
		assertEquals(10, a[2]);
	}

	public void testRemoveDuplicateElementsList() {
		List list = this.buildVector1();
		list.add("zero");
		list.add("zero");
		list.add("two");
		list.add("zero");
		list = CollectionTools.removeDuplicateElements(list);
		int i = 0;
		assertEquals("zero", list.get(i++));
		assertEquals("one", list.get(i++));
		assertEquals("two", list.get(i++));
		assertEquals(i, list.size());
	}
	
	public void testRemoveDuplicateElementsObjectArray() {
		List list = this.buildVector1();
		list.add("zero");
		list.add("zero");
		list.add("two");
		list.add("zero");
		Object[] array = CollectionTools.removeDuplicateElements(list.toArray());
		int i = 0;
		assertEquals("zero", array[i++]);
		assertEquals("one", array[i++]);
		assertEquals("two", array[i++]);
		assertEquals(i, array.length);
	}
	
	public void testRemoveDuplicateElementsObjectArray2() {
		List list = this.buildVector1();
		list.add("zero");
		list.add("zero");
		list.add("two");
		list.add("zero");
		String[] array = (String[]) CollectionTools.removeDuplicateElements(list.toArray(new String[list.size()]));
		int i = 0;
		assertEquals("zero", array[i++]);
		assertEquals("one", array[i++]);
		assertEquals("two", array[i++]);
		assertEquals(i, array.length);
	}
	
	private Object[] buildArray1() {
		return new Object[] {"zero", "one", "two"};
	}
	
	private String[] buildStringArray1() {
		return new String[] {"zero", "one", "two"};
	}
	
	private char[] buildCharArray() {
		return new char[] {'a', 'b', 'c'};
	}
	
	private int[] buildIntArray() {
		return new int[] {0, 10, 20};
	}
	
	private Object[] buildArray2() {
		return new Object[] {"three", "four", "five"};
	}
	
	private String[] buildStringArray2() {
		return new String[] {"three", "four", "five"};
	}
	
	private Vector buildVector1() {
		Vector v = new Vector();
		this.addToCollection1(v);
		return v;
	}
	
	private List buildList1() {
		List l = new ArrayList();
		this.addToCollection1(l);
		return l;
	}
	
	private void addToCollection1(Collection c) {
		c.add("zero");
		c.add("one");
		c.add("two");
	}
	
	private List buildList2() {
		List l = new ArrayList();
		this.addToCollection2(l);
		return l;
	}
	
	private void addToCollection2(Collection c) {
		c.add("three");
		c.add("four");
		c.add("five");
	}
	
	private Set buildSet1() {
		Set s = new HashSet();
		this.addToCollection1(s);
		return s;
	}
	
	private SortedSet buildSortedSet1() {
		SortedSet s = new TreeSet();
		this.addToCollection1(s);
		return s;
	}
	
}
