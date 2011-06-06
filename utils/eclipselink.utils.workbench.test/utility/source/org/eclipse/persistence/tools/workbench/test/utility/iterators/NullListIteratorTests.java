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

import java.util.ListIterator;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.iterators.NullListIterator;

public class NullListIteratorTests extends TestCase {

	public static Test suite() {
		return new TestSuite(NullListIteratorTests.class);
	}
	
	public NullListIteratorTests(String name) {
		super(name);
	}
	
	public void testHasNext() {
		int i = 0;
		for (ListIterator stream = NullListIterator.instance(); stream.hasNext(); ) {
			stream.next();
			i++;
		}
		assertEquals(0, i);
	}
	
	public void testNext() {
		for (ListIterator stream = NullListIterator.instance(); stream.hasNext(); ) {
			fail("bogus element: " + stream.next());
		}
	}
	
	public void testNextIndex() {
		ListIterator stream = NullListIterator.instance();
		assertEquals(0, stream.nextIndex());
	}

	public void testHasPrevious() {
		ListIterator stream = NullListIterator.instance();
		int i = 0;
		while (stream.hasPrevious()) {
			stream.previous();
			i++;
		}
		assertEquals(0, i);

		while (stream.hasNext()) {
			stream.next();
		}
		i = 0;
		while (stream.hasPrevious()) {
			stream.previous();
			i++;
		}
		assertEquals(0, i);
	}
	
	public void testPrevious() {
		ListIterator stream = NullListIterator.instance();
		while (stream.hasPrevious()) {
			fail("bogus element: " + stream.previous());
		}
		while (stream.hasNext()) {
			stream.next();
		}
		while (stream.hasPrevious()) {
			fail("bogus element: " + stream.previous());
		}
	}

	public void testPreviousIndex() {
		ListIterator stream = NullListIterator.instance();
		assertEquals(-1, stream.previousIndex());
	}

	public void testNoSuchElementException() {
		boolean exCaught = false;
		ListIterator stream = NullListIterator.instance();
		Object element = null;
		while (stream.hasNext()) {
			element = stream.next();
		}
		try {
			element = stream.next();
		} catch (NoSuchElementException ex) {
			exCaught = true;
		}
		assertTrue("NoSuchElementException not thrown (next): " + element, exCaught);
		while (stream.hasPrevious()) {
			element = stream.previous();
		}
		try {
			element = stream.previous();
		} catch (NoSuchElementException ex) {
			exCaught = true;
		}
		assertTrue("NoSuchElementException not thrown (previous): " + element, exCaught);
	}
	
	public void testUnsupportedOperationException() {
		boolean exCaught = false;
		try {
			NullListIterator.instance().remove();
		} catch (UnsupportedOperationException ex) {
			exCaught = true;
		}
		assertTrue("UnsupportedOperationException not thrown (remove)", exCaught);
		try {
			NullListIterator.instance().set(new Object());
		} catch (UnsupportedOperationException ex) {
			exCaught = true;
		}
		assertTrue("UnsupportedOperationException not thrown (set)", exCaught);
		try {
			NullListIterator.instance().add(new Object());
		} catch (UnsupportedOperationException ex) {
			exCaught = true;
		}
		assertTrue("UnsupportedOperationException not thrown (add)", exCaught);
	}
	
}
