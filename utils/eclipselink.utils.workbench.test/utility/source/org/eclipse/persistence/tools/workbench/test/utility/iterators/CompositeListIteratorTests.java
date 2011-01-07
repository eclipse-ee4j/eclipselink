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
package org.eclipse.persistence.tools.workbench.test.utility.iterators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeListIterator;

public class CompositeListIteratorTests extends CompositeIteratorTests {

	public static Test suite() {
		return new TestSuite(CompositeListIteratorTests.class);
	}
	
	public CompositeListIteratorTests(String name) {
		super(name);
	}
	
	void verifyHasAnother(Iterator stream) {
		super.verifyHasAnother(stream);
		ListIterator stream2 = (ListIterator) stream;
		int i = 0;
		while (stream2.hasPrevious()) {
			stream2.previous();
			i++;
		}
		assertEquals(8, i);
	}
	
	void verifyAnother(Iterator stream) {
		super.verifyAnother(stream);
		int i = 8;
		ListIterator stream2 = (ListIterator) stream;
		while (stream2.hasPrevious()) {
			assertEquals("bogus element", String.valueOf(i--), ((String) stream2.previous()).substring(0, 1));
		}
	}
	
	public void testRemove() {
		super.testRemove();
		List list1 = this.buildList1();
		List list2 = this.buildList2();
		List list3 = this.buildList3();
		Object firstElement3 = list3.get(0);
	
		List list = new ArrayList();
		list.add(list1.listIterator());
		list.add(list2.listIterator());
		list.add(list3.listIterator());
	
		ListIterator stream = (ListIterator) this.buildCompositeIterator(list.listIterator());
		// position to end of stream
		while (stream.hasNext()) {
			stream.next();
		}
		while (stream.hasPrevious()) {
			Object previous =  stream.previous();
			if (previous.equals("333")) {
				stream.remove();
			}
			// test special case - where we are between iterators
			if (previous.equals(firstElement3)) {
				// this will trigger the next iterator to be loaded
				stream.hasPrevious();
				// now try to remove from the previous iterator
				stream.remove();
			}
		}
		stream.remove();
	
		assertEquals("nothing removed from collection 1", this.buildList1().size() - 2, list1.size());
		assertFalse("element still in collection 1", list1.contains("1"));
		assertFalse("element still in collection 1", list1.contains("333"));
	
		assertEquals("nothing removed from collection 3", this.buildList3().size() - 1, list3.size());
		assertFalse("first element still in collection 3", list3.contains(firstElement3));
		assertTrue("wrong element removed from collection 3", list3.contains("666666"));
	}
	
	public void testAdd() {
		List list1 = this.buildList1();
		Object lastElement1 = list1.get(list1.size() - 1);
		List list2 = this.buildList2();
		List list3 = this.buildList3();
		Object firstElement3 = list3.get(0);
	
		List list = new ArrayList();
		list.add(list1.listIterator());
		list.add(list2.listIterator());
		list.add(list3.listIterator());
	
		ListIterator stream = (ListIterator) this.buildCompositeIterator(list.listIterator());
		while (stream.hasNext()) {
			Object next = stream.next();
			if (next.equals("333")) {
				stream.add("3.5");
			}
			// test special case - where we are between iterators
			if (next.equals(lastElement1)) {
				// this will trigger the next iterator to be loaded
				stream.hasNext();
				// now try to add to the iterator
				stream.add("something in 3");
			}
		}
		stream.add("finale");
		boolean checkForFinale = true;
		while (stream.hasPrevious()) {
			Object previous = stream.previous();
			if (checkForFinale) {
				checkForFinale = false;
				assertEquals("added element dropped", "finale", previous);
			}
			if (previous.equals("333")) {
				stream.add("2.5");
			}
			// test special case - where we are between iterators
			if (previous.equals(firstElement3)) {
				// this will trigger the next iterator to be loaded
				stream.hasPrevious();
				// now try to remove from the previous iterator
				stream.add("old start of 3");
			}
		}
		stream.add("prelude");
		assertEquals("added element dropped", "prelude", stream.previous());
	
		assertEquals("elements not added to collection 1", this.buildList1().size() + 3, list1.size());
		assertEquals("element not added to collection 1", "prelude", list1.get(0));
		assertEquals("element not added to collection 1", "2.5", list1.get(3));
		assertEquals("element not added to collection 1", "3.5", list1.get(5));
	
		assertEquals("elements not added to collection 3", this.buildList3().size() + 3, list3.size());
		assertEquals("element not added to collection 3", "something in 3", list3.get(0));
		assertEquals("element not added to collection 3", "old start of 3", list3.get(1));
		assertEquals("element not added to collection 3", "finale", list3.get(list3.size() - 1));
	
		// add to the front
		stream = (ListIterator) this.buildCompositeIterator();
		stream.add("blah");
		assertFalse("added element should be placed BEFORE the \"cursor\"", stream.next().equals("blah"));
	
		stream = (ListIterator) this.buildCompositeIterator();
	 	stream.add("blah");
		assertTrue("added element should be placed BEFORE the \"cursor\"", stream.previous().equals("blah"));
	
		stream = (ListIterator) this.buildCompositeIterator();
		while (stream.hasNext()) {
			stream.next();
		}
		while (stream.hasPrevious()) {
			stream.previous();
		}
		stream.add("blah");
		assertFalse("added element should be placed BEFORE the \"cursor\"", stream.next().equals("blah"));
	
		stream = (ListIterator) this.buildCompositeIterator();
		while (stream.hasNext()) {
			stream.next();
		}
		while (stream.hasPrevious()) {
			stream.previous();
		}
		stream.add("blah");
		assertTrue("added element should be placed BEFORE the \"cursor\"", stream.previous().equals("blah"));
	
		// add to the middle
		stream = (ListIterator) this.buildCompositeIterator();
		stream.next();
		stream.add("blah");
		assertFalse("added element should be placed BEFORE the \"cursor\"", stream.next().equals("blah"));
	
		stream = (ListIterator) this.buildCompositeIterator();
		stream.next();
		stream.add("blah");
		assertTrue("added element should be placed BEFORE the \"cursor\"", stream.previous().equals("blah"));
	
		stream = (ListIterator) this.buildCompositeIterator();
		while (stream.hasNext()) {
			stream.next();
		}
		stream.previous();
		stream.add("blah");
		assertFalse("added element should be placed BEFORE the \"cursor\"", stream.next().equals("blah"));
	
		stream = (ListIterator) this.buildCompositeIterator();
		while (stream.hasNext()) {
			stream.next();
		}
		stream.previous();
		stream.add("blah");
		assertTrue("added element should be placed BEFORE the \"cursor\"", stream.previous().equals("blah"));
	
		// add to the end
		stream = (ListIterator) this.buildCompositeIterator();
		while (stream.hasNext()) {
			stream.next();
		}
		stream.add("blah");
		assertFalse("added element should be placed BEFORE the \"cursor\"", stream.hasNext());
	
		stream = (ListIterator) this.buildCompositeIterator();
		while (stream.hasNext()) {
			stream.next();
		}
		stream.add("blah");
		assertTrue("added element should be placed BEFORE the \"cursor\"", stream.previous().equals("blah"));
	}
	
	public void testSet() {
		List list1 = this.buildList1();
		Object lastElement1 = list1.get(list1.size() - 1);
		List list2 = this.buildList2();
		List list3 = this.buildList3();
		Object firstElement3 = list3.get(0);
	
		List list = new ArrayList();
		list.add(list1.listIterator());
		list.add(list2.listIterator());
		list.add(list3.listIterator());
	
		ListIterator stream = (ListIterator) this.buildCompositeIterator(list.listIterator());
		// position to end of stream
		while (stream.hasNext()) {
			Object next = stream.next();
			if (next.equals("333")) {
				stream.set("333a");
			}
			// test special case - where we are between iterators
			if (next.equals(lastElement1)) {
				// this will trigger the next iterator to be loaded
				stream.hasNext();
				// now try to remove from the previous iterator
				stream.set("end of 1");
			}
		}
		while (stream.hasPrevious()) {
			Object previous = stream.previous();
			if (previous.equals("22")) {
				stream.set("22a");
			}
			// test special case - where we are between iterators
			if (previous.equals(firstElement3)) {
				// this will trigger the next iterator to be loaded
				stream.hasPrevious();
				// now try to remove from the previous iterator
				stream.set("start of 3");
			}
		}
	
		assertEquals("element(s) added to collection 1", this.buildList1().size(), list1.size());
		assertEquals("element not set in collection 1", "22a", list1.get(1));
		assertFalse("element not set in collection 1", list1.contains("22"));
		assertEquals("element not set in collection 1", "333a", list1.get(2));
		assertFalse("element not set in collection 1", list1.contains("333"));
		assertEquals("element not set in collection 1", "end of 1", list1.get(list1.size() - 1));
		assertFalse("element not set in collection 1", list1.contains(lastElement1));
	
		assertEquals("element(s) added to collection 3", this.buildList3().size(), list3.size());
		assertEquals("element not set in collection 3", "start of 3", list3.get(0));
		assertFalse("element not set in collection 3", list3.contains(firstElement3));
	}
	
	public void testNextIndexPreviousIndex() {
		int i = 0;
		ListIterator stream = (ListIterator) this.buildCompositeIterator();
		assertEquals(i, stream.nextIndex());
		assertEquals(i - 1, stream.previousIndex());
		while (stream.hasNext()) {
			Object next = stream.next();
			i++;
			if (next.equals("333")) {
				stream.remove();
				i--;
			}
			if (next.equals("7777777")) {
				stream.add("7.5");
				i++;
			}
			assertEquals(i, stream.nextIndex());
			assertEquals(i - 1, stream.previousIndex());
		}
		assertEquals("index is corrupt", 8, i);
	
		assertEquals(i, stream.nextIndex());
		assertEquals(i - 1, stream.previousIndex());
		while (stream.hasPrevious()) {
			Object previous = stream.previous();
			i--;
			if (previous.equals("666666")) {
				stream.remove();
				// removing a previous element, does not change the cursor
			}
			if (previous.equals("22")) {
				stream.add("1.5");
				i++;
			}
			assertEquals(i, stream.nextIndex());
			assertEquals(i - 1, stream.previousIndex());
		}
		assertEquals("index is corrupt", 0, i);
	}
	
	public void testPreviousIndex() {
		// TODO
	}
	
	void verifyNoSuchElementException(Iterator stream) {
		super.verifyNoSuchElementException(stream);
		ListIterator stream2 = (ListIterator) stream;
		boolean exCaught = false;
		String string = null;
		while (stream2.hasPrevious()) {
			string = (String) stream2.previous();
		}
		try {
			string = (String) stream2.previous();
		} catch (NoSuchElementException ex) {
			exCaught = true;
		}
		assertTrue("NoSuchElementException not thrown: " + string, exCaught);
	}
	
	void verifyUnsupportedOperationException(Iterator stream) {
		super.verifyUnsupportedOperationException(stream);
		boolean exCaught = false;
		ListIterator stream2 = (ListIterator) stream;
		while(stream2.hasPrevious()) {
			Object string = stream2.previous();
			if (string.equals("333")) {
				try {
					stream2.remove();
				} catch (UnsupportedOperationException ex) {
					exCaught = true;
				}
			}
		}
		assertTrue("UnsupportedOperationException not thrown", exCaught);
	}
	
	void verifyIllegalStateException(Iterator stream) {
		super.verifyIllegalStateException(stream);
		ListIterator stream2 = (ListIterator) stream;
		boolean exCaught = false;
		try {
			stream2.set("junk");
		} catch (IllegalStateException ex) {
			exCaught = true;
		}
		assertTrue("IllegalStateException not thrown", exCaught);
	}
	
	void verifyEmptyHasAnother(Iterator stream) {
		super.verifyEmptyHasAnother(stream);
		ListIterator stream2 = (ListIterator) stream;
		int i = 0;
		while (stream2.hasPrevious()) {
			stream2.previous();
			i++;
		}
		assertEquals(0, i);
	}
	
	Iterator buildCompositeIterator(Iterator iterators) {
		return this.buildCompositeListIterator((ListIterator) iterators);
	}
	
	ListIterator buildCompositeListIterator(ListIterator iterators) {
		return new CompositeListIterator(iterators);
	}
	
	Iterator buildCompositeIterator(Object object, ListIterator iterator) {
		return this.buildCompositeListIterator(object, iterator);
	}
	
	ListIterator buildCompositeListIterator(Object object, ListIterator iterator) {
		return new CompositeListIterator(object, iterator);
	}
	
}
