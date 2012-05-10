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
package org.eclipse.persistence.tools.workbench.test.utility.iterators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;

public class CompositeIteratorTests extends TestCase {

	public static Test suite() {
		return new TestSuite(CompositeIteratorTests.class);
	}
	
	public CompositeIteratorTests(String name) {
		super(name);
	}
	
	public void testHasAnother() {
		this.verifyHasAnother(this.buildCompositeIterator());
	}
	
	void verifyHasAnother(Iterator stream) {
		this.verifyHasAnother(8, stream);
	}
	
	void verifyHasAnother(int expected, Iterator stream) {
		int i = 0;
		while (stream.hasNext()) {
			stream.next();
			i++;
		}
		assertEquals(expected, i);
	}
	
	public void testAnother() {
		this.verifyAnother(this.buildCompositeIterator());
	}
	
	void verifyAnother(Iterator stream) {
		this.verifyAnother(1, stream);
	}
	
	void verifyAnother(int start, Iterator stream) {
		int index = start;
		while (stream.hasNext()) {
			assertEquals("bogus element", String.valueOf(index++), ((String) stream.next()).substring(0, 1));
		}
	}
	
	public void testRemove() {
		List list1 = this.buildList1();
		Object lastElement1 = list1.get(list1.size() - 1);
		List list2 = this.buildList2();
		List list3 = this.buildList3();
	
		List list = new ArrayList();
		list.add(list1.listIterator());
		list.add(list2.listIterator());
		list.add(list3.listIterator());
	
		Iterator stream = this.buildCompositeIterator(list.listIterator());
		while (stream.hasNext()) {
			Object next =  stream.next();
			if (next.equals("333")) {
				stream.remove();
			}
			// test special case - where we are between iterators
			if (next.equals(lastElement1)) {
				// this will trigger the next iterator to be loaded
				stream.hasNext();
				// now try to remove from the previous iterator
				stream.remove();
			}
		}
		stream.remove();
	
		assertEquals("nothing removed from collection 1", this.buildList1().size() - 2, list1.size());
		assertFalse("element still in collection 1", list1.contains("333"));
		assertFalse("last element still in collection 1", list1.contains(lastElement1));
		assertTrue("wrong element removed from collection 1", list1.contains("22"));
	
		assertEquals("nothing removed from collection 3", this.buildList3().size() - 1, list3.size());
		assertFalse("element still in collection 3", list3.contains("88888888"));
		assertTrue("wrong element removed from collection 3", list3.contains("666666"));
	}

	public void testSingleElement() {
		Object item = "0";
		this.verifyHasAnother(9, this.buildCompositeIterator(item, this.buildCompositeIterator()));
		this.verifyAnother(0, this.buildCompositeIterator(item, this.buildCompositeIterator()));
	}

	public void testNoSuchElementException() {
		this.verifyNoSuchElementException(this.buildCompositeIterator());
	}
	
	void verifyNoSuchElementException(Iterator stream) {
		boolean exCaught = false;
		String string = null;
		while (stream.hasNext()) {
			string = (String) stream.next();
		}
		try {
			string = (String) stream.next();
		} catch (NoSuchElementException ex) {
			exCaught = true;
		}
		assertTrue("NoSuchElementException not thrown: " + string, exCaught);
	}
	
	public void testUnsupportedOperationException() {
		this.verifyUnsupportedOperationException(this.buildUnmodifiableCompositeIterator());
	}
	
	void verifyUnsupportedOperationException(Iterator stream) {
		boolean exCaught = false;
		while (stream.hasNext()) {
			Object string = stream.next();
			if (string.equals("333")) {
				try {
					stream.remove();
				} catch (UnsupportedOperationException ex) {
					exCaught = true;
				}
			}
		}
		assertTrue("UnsupportedOperationException not thrown", exCaught);
	}
	
	public void testIllegalStateException() {
		this.verifyIllegalStateException(this.buildCompositeIterator());
	}
	
	void verifyIllegalStateException(Iterator stream) {
		boolean exCaught = false;
		try {
			stream.remove();
		} catch (IllegalStateException ex) {
			exCaught = true;
		}
		assertTrue("IllegalStateException not thrown", exCaught);
	}
	
	public void testEmptyHasAnother1() {
		this.verifyEmptyHasAnother(this.buildEmptyCompositeIterator1());
	}
	
	void verifyEmptyHasAnother(Iterator stream) {
		int i = 0;
		while (stream.hasNext()) {
			stream.next();
			i++;
		}
		assertEquals(0, i);
	}
	
	public void testEmptyNoSuchElementException1() {
		this.verifyNoSuchElementException(this.buildEmptyCompositeIterator1());
	}
	
	public void testEmptyIllegalStateException1() {
		this.verifyIllegalStateException(this.buildEmptyCompositeIterator1());
	}
	
	public void testEmptyHasAnother2() {
		this.verifyEmptyHasAnother(this.buildEmptyCompositeIterator2());
	}
	
	public void testEmptyNoSuchElementException2() {
		this.verifyNoSuchElementException(this.buildEmptyCompositeIterator2());
	}
	
	public void testEmptyIllegalStateException2() {
		this.verifyIllegalStateException(this.buildEmptyCompositeIterator2());
	}
	
	Iterator buildCompositeIterator() {
		return this.buildCompositeIterator(this.buildIterators());
	}
	
	Iterator buildEmptyCompositeIterator1() {
		return this.buildCompositeIterator(this.buildEmptyIterators1());
	}
	
	Iterator buildEmptyCompositeIterator2() {
		return this.buildCompositeIterator(this.buildEmptyIterators2());
	}
	
	Iterator buildUnmodifiableCompositeIterator() {
		return this.buildCompositeIterator(this.buildUnmodifiableIterators());
	}
	
	Iterator buildCompositeIterator(Iterator iterators) {
		return new CompositeIterator(iterators);
	}
	
	Iterator buildCompositeIterator(Object object, Iterator iterator) {
		return new CompositeIterator(object, iterator);
	}
	
	ListIterator buildIterators() {
		List list = new ArrayList();
		list.add(this.buildIterator1());
		list.add(this.buildIterator2());
		list.add(this.buildIterator3());
		return list.listIterator();
	}
	
	ListIterator buildEmptyIterators1() {
		return this.buildEmptyIterator();
	}
	
	ListIterator buildEmptyIterators2() {
		List list = new ArrayList();
		list.add(this.buildEmptyIterator());
		list.add(this.buildEmptyIterator());
		list.add(this.buildEmptyIterator());
		return list.listIterator();
	}
	
	ListIterator buildUnmodifiableIterators() {
		List list = new ArrayList();
		list.add(this.buildUnmodifiableIterator1());
		list.add(this.buildUnmodifiableIterator2());
		list.add(this.buildUnmodifiableIterator3());
		return list.listIterator();
	}
	
	ListIterator buildIterator1() {
		return this.buildList1().listIterator();
	}
	
	ListIterator buildIterator2() {
		return this.buildList2().listIterator();
	}
	
	ListIterator buildIterator3() {
		return this.buildList3().listIterator();
	}
	
	ListIterator buildUnmodifiableIterator1() {
		return this.buildUnmodifiableList1().listIterator();
	}
	
	ListIterator buildUnmodifiableIterator2() {
		return this.buildUnmodifiableList2().listIterator();
	}
	
	ListIterator buildUnmodifiableIterator3() {
		return this.buildUnmodifiableList3().listIterator();
	}
	
	ListIterator buildEmptyIterator() {
		return (new ArrayList()).listIterator();
	}
	
	List buildList1() {
		List list = new ArrayList();
		list.add("1");
		list.add("22");
		list.add("333");
		list.add("4444");
		return list;
	}
	
	List buildList2() {
		return new ArrayList();
	}
	
	List buildList3() {
		List list = new ArrayList();
		list.add("55555");
		list.add("666666");
		list.add("7777777");
		list.add("88888888");
		return list;
	}
	
	List buildUnmodifiableList1() {
		return Collections.unmodifiableList(this.buildList1());
	}
	
	List buildUnmodifiableList2() {
		return Collections.unmodifiableList(this.buildList2());
	}
	
	List buildUnmodifiableList3() {
		return Collections.unmodifiableList(this.buildList3());
	}
	
}
