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

import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.iterators.ArrayIterator;

public class ArrayIteratorTests extends TestCase {
	
	public static Test suite() {
		return new TestSuite(ArrayIteratorTests.class);
	}
	
	public ArrayIteratorTests(String name) {
		super(name);
	}
	
	public void testHasNext() {
		int i = 0;
		for (Iterator stream = this.buildIterator(); stream.hasNext(); ) {
			stream.next();
			i++;
		}
		assertEquals(this.buildArray().length, i);
	}
	
	public void testNext() {
		int i = 0;
		for (Iterator stream = this.buildIterator(); stream.hasNext(); ) {
			assertEquals("bogus element", ++i, Integer.parseInt((String) stream.next()));
		}
	}
	
	public void testNoSuchElementException() {
		boolean exCaught = false;
		Iterator stream = this.buildIterator();
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
		boolean exCaught = false;
		for (Iterator stream = this.buildIterator(); stream.hasNext(); ) {
			if (stream.next().equals("3")) {
				try {
					stream.remove();
				} catch (UnsupportedOperationException ex) {
					exCaught = true;
				}
			}
		}
		assertTrue("UnsupportedOperationException not thrown", exCaught);
	}
	
	public void testIllegalArgumentException() {
		this.triggerIllegalArgumentException(-1, 1);
		this.triggerIllegalArgumentException(8, 1);
		this.triggerIllegalArgumentException(0, -1);
		this.triggerIllegalArgumentException(0, 9);
	}
	
	public void triggerIllegalArgumentException(int start, int length) {
		boolean exCaught = false;
		Iterator stream = null;
		try {
			stream = this.buildIterator(start, length);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue("IllegalArgumentException not thrown: " + stream, exCaught);
	}
	
	Iterator buildIterator() {
		return this.buildIterator(this.buildArray());
	}
	
	Iterator buildIterator(Object[] array) {
		return new ArrayIterator(array);
	}
	
	Iterator buildIterator(int start, int length) {
		return this.buildIterator(this.buildArray(), start, length);
	}
	
	Iterator buildIterator(Object[] array, int start, int length) {
		return new ArrayIterator(array, start, length);
	}
	
	Object[] buildArray() {
		return new Object[] {"1", "2", "3", "4", "5", "6", "7", "8"};
	}
	
}
