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

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.iterators.ArrayListIterator;

public class ArrayListIteratorTests extends ArrayIteratorTests {

	public static Test suite() {
		return new TestSuite(ArrayListIteratorTests.class);
	}
	
	public ArrayListIteratorTests(String name) {
		super(name);
	}
	
	public void testHasPrevious() {
		ListIterator stream = this.buildListIterator();
		while (stream.hasNext()) {
			stream.next();
		}
		int i = 0;
		while (stream.hasPrevious()) {
			stream.previous();
			i++;
		}
		assertEquals(this.buildArray().length, i);
	}
	
	public void testPrevious() {
		ListIterator stream = this.buildListIterator();
		while (stream.hasNext()) {
			stream.next();
		}
		int i = this.buildArray().length;
		while (stream.hasPrevious()) {
			assertEquals("bogus element", i--, Integer.parseInt((String) stream.previous()));
		}
	}
	
	public void testNextIndex() {
		int i = 0;
		ListIterator stream = this.buildListIterator();
		while (stream.hasNext()) {
			assertEquals(i, stream.nextIndex());
			stream.next();
			i++;
		}
		assertEquals(i, stream.nextIndex());
	}
	
	public void testPreviousIndex() {
		int i = 0;
		ListIterator stream = this.buildListIterator();
		while (stream.hasNext()) {
			assertEquals(i - 1, stream.previousIndex());
			stream.next();
			i++;
		}
		assertEquals(i - 1, stream.previousIndex());
	}
	
	public void testNoSuchElementException() {
		boolean exCaught = false;
		ListIterator stream = this.buildListIterator();
		String string = null;
		try {
			string = (String) stream.previous();
		} catch (NoSuchElementException ex) {
			exCaught = true;
		}
		assertTrue("NoSuchElementException not thrown: " + string, exCaught);
	}
	
	public void testUnsupportedOperationExceptionAdd() {
		boolean exCaught = false;
		for (ListIterator stream = this.buildListIterator(); stream.hasNext(); ) {
			if (stream.next().equals("3")) {
				try {
					stream.add("3.5");
				} catch (UnsupportedOperationException ex) {
					exCaught = true;
				}
			}
		}
		assertTrue("UnsupportedOperationException not thrown", exCaught);
	}
	
	public void testUnsupportedOperationExceptionSet() {
		boolean exCaught = false;
		for (ListIterator stream = this.buildListIterator(); stream.hasNext(); ) {
			if (stream.next().equals("3")) {
				try {
					stream.set("three");
				} catch (UnsupportedOperationException ex) {
					exCaught = true;
				}
			}
		}
		assertTrue("UnsupportedOperationException not thrown", exCaught);
	}
	
	private ListIterator buildListIterator() {
		return this.buildListIterator(this.buildArray());
	}
	
	private ListIterator buildListIterator(Object[] array) {
		return new ArrayListIterator(array);
	}
	
	Iterator buildIterator(Object[] array) {
		return new ArrayListIterator(array);
	}
	
	Iterator buildIterator(Object[] array, int start, int length) {
		return new ArrayListIterator(array, start, length);
	}
	
}
