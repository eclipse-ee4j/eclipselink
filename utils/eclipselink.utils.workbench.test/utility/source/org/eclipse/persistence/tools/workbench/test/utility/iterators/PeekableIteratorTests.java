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
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.iterators.PeekableIterator;

public class PeekableIteratorTests extends TestCase {

	public static Test suite() {
		return new TestSuite(PeekableIteratorTests.class);
	}
	
	public PeekableIteratorTests(String name) {
		super(name);
	}
	
	public void testUnsupportedOperationException() {
		boolean exCaught = false;
		for (Iterator stream = this.buildPeekableIterator(); stream.hasNext(); ) {
			String string = (String) stream.next();
			if (string.equals("three")) {
				try {
					stream.remove();
				} catch (UnsupportedOperationException ex) {
					exCaught = true;
				}
			}
		}
		assertTrue("UnsupportedOperationException not thrown", exCaught);
	}
	
	public void testNoSuchElementException() {
		boolean exCaught = false;
		Iterator stream = this.buildPeekableIterator();
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
	
	public void testHasNext() {
		int i = 0;
		for (Iterator stream = this.buildPeekableIterator(); stream.hasNext(); ) {
			stream.next();
			i++;
		}
		assertEquals(6, i);
	}
	
	public void testNext() {
		Iterator stream = this.buildPeekableIterator();
		assertEquals("zero", stream.next());
		assertEquals("one", stream.next());
		assertEquals("two", stream.next());
		assertEquals("three", stream.next());
		assertEquals("four", stream.next());
		assertEquals("five", stream.next());
	}
	
	public void testPeek() {
		Object next = null;
		for (PeekableIterator stream = this.buildPeekableIterator(); stream.hasNext(); ) {
			Object peek = stream.peek();
			assertTrue("peek and next are prematurely identical", peek != next);
			next = stream.next();
			assertTrue("peek and next are not identical", peek == next);
		}
	}
	
	private PeekableIterator buildPeekableIterator() {
		return this.buildPeekableIterator(this.buildNestedIterator());
	}
	
	private PeekableIterator buildPeekableIterator(Iterator nestedIterator) {
		return new PeekableIterator(nestedIterator);
	}
	
	private Iterator buildNestedIterator() {
		Collection c = new ArrayList();
		c.add("zero");
		c.add("one");
		c.add("two");
		c.add("three");
		c.add("four");
		c.add("five");
		return c.iterator();
	}
	
}
