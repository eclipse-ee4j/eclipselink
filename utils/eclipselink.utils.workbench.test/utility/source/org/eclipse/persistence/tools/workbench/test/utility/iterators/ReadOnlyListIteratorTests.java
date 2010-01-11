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
package org.eclipse.persistence.tools.workbench.test.utility.iterators;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.iterators.ReadOnlyListIterator;

public class ReadOnlyListIteratorTests extends TestCase {

	public static Test suite() {
		return new TestSuite(ReadOnlyListIteratorTests.class);
	}
	
	public ReadOnlyListIteratorTests(String name) {
		super(name);
	}
	
	public void testHasNextAndHasPrevious() {
		int i = 0;
		ListIterator stream = this.buildReadOnlyListIterator();
		while (stream.hasNext()) {
			stream.next();
			i++;
		}
		assertEquals(this.buildList().size(), i);

		while (stream.hasPrevious()) {
			stream.previous();
			i--;
		}
		assertEquals(0, i);
	}
	
	public void testNextAndPrevious() {
		ListIterator nestedListIterator = this.buildNestedListIterator();
		ListIterator stream = this.buildReadOnlyListIterator();
		while (stream.hasNext()) {
			assertEquals("bogus element", nestedListIterator.next(), stream.next());
		}
		while (stream.hasPrevious()) {
			assertEquals("bogus element", nestedListIterator.previous(), stream.previous());
		}
	}
	
	public void testNextIndexAndPreviousIndex() {
		ListIterator nestedListIterator = this.buildNestedListIterator();
		ListIterator stream = this.buildReadOnlyListIterator();
		while (stream.hasNext()) {
			assertEquals("bogus index", nestedListIterator.nextIndex(), stream.nextIndex());
			nestedListIterator.next();
			stream.next();
		}
		assertEquals("bogus index", this.buildList().size(), stream.nextIndex());
		while (stream.hasPrevious()) {
			assertEquals("bogus element", nestedListIterator.previousIndex(), stream.previousIndex());
			nestedListIterator.previous();
			stream.previous();
		}
		assertEquals("bogus index", -1, stream.previousIndex());
	}
	
	public void testNoSuchElementException() {
		boolean exCaught = false;
		ListIterator stream = this.buildReadOnlyListIterator();
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
	
	public void testRemove() {
		boolean exCaught = false;
		for (ListIterator stream = this.buildReadOnlyListIterator(); stream.hasNext(); ) {
			if (stream.next().equals("three")) {
				try {
					stream.remove();
				} catch (UnsupportedOperationException ex) {
					exCaught = true;
				}
			}
		}
		assertTrue("UnsupportedOperationException not thrown", exCaught);
	}
	
	public void testSet() {
		boolean exCaught = false;
		for (ListIterator stream = this.buildReadOnlyListIterator(); stream.hasNext(); ) {
			if (stream.next().equals("three")) {
				try {
					stream.set("bogus");
				} catch (UnsupportedOperationException ex) {
					exCaught = true;
				}
			}
		}
		assertTrue("UnsupportedOperationException not thrown", exCaught);
	}
	
	public void testAdd() {
		boolean exCaught = false;
		for (ListIterator stream = this.buildReadOnlyListIterator(); stream.hasNext(); ) {
			if (stream.next().equals("three")) {
				try {
					stream.add("bogus");
				} catch (UnsupportedOperationException ex) {
					exCaught = true;
				}
			}
		}
		assertTrue("UnsupportedOperationException not thrown", exCaught);
	}
	
	private ListIterator buildReadOnlyListIterator() {
		return this.buildReadOnlyListIterator(this.buildNestedListIterator());
	}
	
	private ListIterator buildReadOnlyListIterator(ListIterator nestedListIterator) {
		return new ReadOnlyListIterator(nestedListIterator);
	}
	
	private ListIterator buildNestedListIterator() {
		return this.buildList().listIterator();
	}
	
	private List buildList() {
		List l = new ArrayList();
		l.add("one");
		l.add("two");
		l.add("three");
		l.add("four");
		l.add("five");
		l.add("six");
		l.add("seven");
		l.add("eight");
		return l;
	}
	
}
