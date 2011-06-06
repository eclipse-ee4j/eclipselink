/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.IdentityHashBag;

public class IdentityHashBagTests extends TestCase {
	private IdentityHashBag bag;
	private String one = "one";
	private String two = "two";
	private String three = "three";
	private String four = "four";
	private String foo = "foo";
	private String bar = "bar";

	public static Test suite() {
		return new TestSuite(IdentityHashBagTests.class);
	}

	public IdentityHashBagTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.bag = this.buildBag();
	}
	
	protected IdentityHashBag buildBag() {
		IdentityHashBag result = new IdentityHashBag();
		result.add(null);
		result.add(this.one);
		result.add(this.two);
		result.add(this.two);
		result.add(this.three);
		result.add(this.three);
		result.add(this.three);
		result.add(this.four);
		result.add(this.four);
		result.add(this.four);
		result.add(this.four);
		return result;
	}
	
	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	private Collection buildCollection() {
		Collection c = new ArrayList();
		c.add(this.foo);
		c.add(this.foo);
		c.add(this.bar);
		c.add(this.bar);
		c.add(this.bar);
		return c;
	}
	
	public void testCtorCollection() {
		Collection c = this.buildCollection();
		IdentityHashBag localBag = new IdentityHashBag(c);
		for (Iterator stream = c.iterator(); stream.hasNext(); ) {
			assertTrue(localBag.contains(stream.next()));
		}
	}
	
	public void testCtorIntFloat() {
		boolean exCaught;
	
		exCaught = false;
		try {
			this.bag = new IdentityHashBag(-20, 0.66f);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue("IllegalArgumentException not thrown", exCaught);
	
		exCaught = false;
		try {
			this.bag = new IdentityHashBag(20, -0.66f);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue("IllegalArgumentException not thrown", exCaught);
	}
	
	public void testAdd() {
		// the other adds took place in setUp
		String five = "five";
		assertTrue(this.bag.add(five));
	
		assertTrue(this.bag.contains(this.one));
		assertTrue(this.bag.contains(this.two));
		assertTrue(this.bag.contains(this.three));
		assertTrue(this.bag.contains(this.four));
		assertTrue(this.bag.contains(five));
	}
	
	public void testAddAll() {
		Collection c = this.buildCollection();
		assertTrue(this.bag.addAll(c));
		for (Iterator stream = c.iterator(); stream.hasNext(); ) {
			assertTrue(this.bag.contains(stream.next()));
		}
	}
	
	public void testClear() {
		assertTrue(this.bag.contains(this.one));
		assertTrue(this.bag.contains(this.two));
		assertTrue(this.bag.contains(this.three));
		assertTrue(this.bag.contains(this.four));
		assertTrue(this.bag.contains(null));
		assertEquals(11, this.bag.size());
		this.bag.clear();
		assertFalse(this.bag.contains(this.one));
		assertFalse(this.bag.contains(this.two));
		assertFalse(this.bag.contains(this.three));
		assertFalse(this.bag.contains(this.four));
		assertFalse(this.bag.contains(null));
		assertEquals(0, this.bag.size());
	}
	
	public void testClone() {
		IdentityHashBag bag2 = (IdentityHashBag) this.bag.clone();
		assertTrue("bad clone", this.bag != bag2);
		assertEquals("bad clone", this.bag, bag2);
		assertTrue("bad clone", this.bag.hashCode() == bag2.hashCode());
	}
	
	public void testContains() {
		assertTrue(this.bag.contains(null));
		assertTrue(this.bag.contains(this.one));
		assertTrue(this.bag.contains(this.two));
		assertTrue(this.bag.contains(this.three));
		assertTrue(this.bag.contains(this.four));

		assertFalse(this.bag.contains(new String("four")));
		assertFalse(this.bag.contains("five"));
	}
	
	public void testContainsAll() {
		Collection c = new ArrayList();
		c.add(null);
		c.add(this.one);
		c.add(this.two);
		c.add(this.three);
		c.add(this.four);
		assertTrue(this.bag.containsAll(c));
		c.add(new String(this.four));
		assertFalse(this.bag.containsAll(c));
	}
	
	public void testCount() {
		assertEquals(0, this.bag.count("zero"));
		assertEquals(1, this.bag.count("one"));
		assertEquals(2, this.bag.count("two"));
		assertEquals(3, this.bag.count("three"));
		assertEquals(4, this.bag.count("four"));
		assertEquals(0, this.bag.count("five"));
	}
	
	public void testEquals() {
		IdentityHashBag bag2 = this.buildBag();
		assertEquals(this.bag, bag2);
		bag2.add("five");
		assertFalse(this.bag.equals(bag2));
		Collection c = new ArrayList(this.bag);
		assertFalse(this.bag.equals(c));
	}
	
	public void testHashCode() {
		IdentityHashBag bag2 = this.buildBag();
		assertEquals(this.bag.hashCode(), bag2.hashCode());
	}
	
	public void testIsEmpty() {
		assertFalse(this.bag.isEmpty());
		this.bag.clear();
		assertTrue(this.bag.isEmpty());
		this.bag.add("foo");
		assertFalse(this.bag.isEmpty());
	}
	
	public void testEmptyIterator() {
		this.bag.clear();
		Iterator iterator = this.bag.iterator();
		assertFalse(iterator.hasNext());
	
		boolean exCaught = false;
		Object element = null;
		try {
			element = iterator.next();
		} catch (NoSuchElementException ex) {
			exCaught = true;
		}
		assertTrue("NoSuchElementException not thrown: " + element, exCaught);
	
		exCaught = false;
		try {
			iterator.remove();
		} catch (IllegalStateException ex) {
			exCaught = true;
		}
		assertTrue("IllegalStateException not thrown", exCaught);
	}
	
	public void testIterator() {
		int i = 0;
		Iterator iterator = this.bag.iterator();
		assertTrue(iterator.hasNext());
		while (iterator.hasNext()) {
			iterator.next();
			i++;
		}
		assertEquals(11, i);
		assertFalse(iterator.hasNext());
	
		boolean exCaught = false;
		Object element = null;
		try {
			element = iterator.next();
		} catch (NoSuchElementException ex) {
			exCaught = true;
		}
		assertTrue("NoSuchElementException not thrown: " + element, exCaught);
	
		iterator.remove();
		assertEquals(10, this.bag.size());
	
		exCaught = false;
		try {
			iterator.remove();
		} catch (IllegalStateException ex) {
			exCaught = true;
		}
		assertTrue("IllegalStateException not thrown", exCaught);
	
		// start over
		iterator = this.bag.iterator();
		this.bag.add("five");
		exCaught = false;
		try {
			iterator.next();
		} catch (ConcurrentModificationException ex) {
			exCaught = true;
		}
		assertTrue("ConcurrentModificationException not thrown", exCaught);
	}
	
	public void testHashingDistribution() throws Exception {
		IdentityHashBag bigBag = new IdentityHashBag();
		for (int i = 0; i < 10000; i++) {
			bigBag.add("object" + i);
		}
	
		java.lang.reflect.Field field = bigBag.getClass().getDeclaredField("table");
		field.setAccessible(true);
		Object[] table = (Object[]) field.get(bigBag);
		int bucketCount = table.length;
		int filledBucketCount = 0;
		for (int i = 0; i < bucketCount; i++) {
			if (table[i] != null) {
				filledBucketCount++;
			}
		}
		float loadFactor = ((float) filledBucketCount)/((float) bucketCount);
		assertTrue("WARNING - poor load factor: " + loadFactor, loadFactor > 0.20);
		assertTrue("WARNING - poor load factor: " + loadFactor, loadFactor < 0.75);
	}
	
	public void testRemove() {
		assertTrue(this.bag.remove(this.one));
		assertFalse(this.bag.contains(this.one));
		assertFalse(this.bag.remove(this.one));
	
		assertTrue(this.bag.remove(this.two));
		assertTrue(this.bag.remove(this.two));
		assertFalse(this.bag.contains(this.two));
		assertFalse(this.bag.remove(this.two));

		assertFalse(this.bag.remove(new String(this.three)));
	}
	
	public void testRemoveAll() {
		Collection c = new ArrayList();
		c.add(this.one);
		c.add(new String(this.two));
		c.add(this.three);
		assertTrue(this.bag.removeAll(c));
		assertFalse(this.bag.contains(this.one));
		assertTrue(this.bag.contains(this.two));
		assertFalse(this.bag.contains(this.three));
		assertFalse(this.bag.remove(this.one));
		assertTrue(this.bag.remove(this.two));
		assertFalse(this.bag.remove(this.three));
		assertFalse(this.bag.removeAll(c));
	}
	
	public void testRetainAll() {
		Collection c = new ArrayList();
		c.add(this.one);
		c.add(new String(this.two));
		c.add(this.three);
		assertTrue(this.bag.retainAll(c));
		assertTrue(this.bag.contains(this.one));
		assertFalse(this.bag.contains(this.two));
		assertTrue(this.bag.contains(this.three));
		assertFalse(this.bag.contains(this.four));
		assertFalse(this.bag.remove(this.two));
		assertFalse(this.bag.remove(this.four));
		assertFalse(this.bag.retainAll(c));
	}
	
	public void testSize() {
		assertTrue(this.bag.size() == 11);
		String five = "five";
		this.bag.add(five);
		this.bag.add(five);
		this.bag.add(five);
		this.bag.add(five);
		this.bag.add(new String(five));
		assertEquals(16, this.bag.size());
	}
	
	public void testSerialization() throws Exception {
		IdentityHashBag bag2 = (IdentityHashBag) TestTools.serialize(this.bag);
	
		assertTrue("same object?", this.bag != bag2);
		assertEquals(11, bag2.size());
		assertEquals(CollectionTools.bag(this.bag.iterator()), CollectionTools.bag(bag2.iterator()));
		// look for similar elements
		assertTrue(CollectionTools.bag(bag2.iterator()).contains(null));
		assertTrue(CollectionTools.bag(bag2.iterator()).contains("one"));
		assertTrue(CollectionTools.bag(bag2.iterator()).contains("two"));
		assertTrue(CollectionTools.bag(bag2.iterator()).contains("three"));
		assertTrue(CollectionTools.bag(bag2.iterator()).contains("four"));
	
		int nullCount = 0, oneCount = 0, twoCount = 0, threeCount = 0, fourCount = 0;
		for (Iterator stream = bag2.iterator(); stream.hasNext(); ) {
			String next = (String) stream.next();
			if (next == null)
				nullCount++;
			else if (next.equals("one"))
				oneCount++;
			else if (next.equals("two"))
				twoCount++;
			else if (next.equals("three"))
				threeCount++;
			else if (next.equals("four"))
				fourCount++;
		}
		assertEquals(1, nullCount);
		assertEquals(1, oneCount);
		assertEquals(2, twoCount);
		assertEquals(3, threeCount);
		assertEquals(4, fourCount);
	}
	
	public void testToArray() {
		Object[] a = this.bag.toArray();
		assertEquals(11, a.length);
		assertTrue(CollectionTools.contains(a, null));
		assertTrue(CollectionTools.contains(a, this.one));
		assertTrue(CollectionTools.contains(a, this.two));
		assertTrue(CollectionTools.contains(a, this.three));
		assertTrue(CollectionTools.contains(a, this.four));
	}
	
	public void testToArrayObjectArray() {
		String[] a = new String[12];
		a[11] = "not null";
		String[] b = (String[]) this.bag.toArray(a);
		assertEquals(a, b);
		assertEquals(12, a.length);
		assertTrue(CollectionTools.contains(a, null));
		assertTrue(CollectionTools.contains(a, this.one));
		assertTrue(CollectionTools.contains(a, this.two));
		assertTrue(CollectionTools.contains(a, this.three));
		assertTrue(CollectionTools.contains(a, this.four));
		assertTrue(a[11] == null);
	}
	
	public void testToString() {
		String s = this.bag.toString();
		assertTrue(s.startsWith("["));
		assertTrue(s.endsWith("]"));
		int commaCount = 0;
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == ',') {
				commaCount++;
			}
		}
		assertEquals("invalid number of commas", 10, commaCount);
		assertTrue(s.indexOf("one") != -1);
		assertTrue(s.indexOf("two") != -1);
		assertTrue(s.indexOf("three") != -1);
		assertTrue(s.indexOf("four") != -1);
		assertTrue(s.indexOf("null") != -1);
	}

}
