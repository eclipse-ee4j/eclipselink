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
import java.util.NoSuchElementException;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.iterators.ReadOnlyIterator;

public class ReadOnlyIteratorTests extends TestCase {

	public static Test suite() {
		return new TestSuite(ReadOnlyIteratorTests.class);
	}
	
	public ReadOnlyIteratorTests(String name) {
		super(name);
	}
	
	public void testHasNext() {
		int i = 0;
		for (Iterator stream = this.buildReadOnlyIterator(); stream.hasNext(); ) {
			stream.next();
			i++;
		}
		assertEquals(this.buildVector().size(), i);
	}
	
	public void testNext() {
		Iterator nestedIterator = this.buildNestedIterator();
		for (Iterator stream = this.buildReadOnlyIterator(); stream.hasNext(); ) {
			assertEquals("bogus element", nestedIterator.next(), stream.next());
		}
	}
	
	public void testNoSuchElementException() {
		boolean exCaught = false;
		Iterator stream = this.buildReadOnlyIterator();
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
		for (Iterator stream = this.buildReadOnlyIterator(); stream.hasNext(); ) {
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
	
	private Iterator buildReadOnlyIterator() {
		return this.buildReadOnlyIterator(this.buildNestedIterator());
	}
	
	private Iterator buildReadOnlyIterator(Iterator nestedIterator) {
		return new ReadOnlyIterator(nestedIterator);
	}
	
	private Iterator buildNestedIterator() {
		return this.buildVector().iterator();
	}
	
	private Vector buildVector() {
		Vector v = new Vector();
		v.addElement("one");
		v.addElement("two");
		v.addElement("three");
		v.addElement("four");
		v.addElement("five");
		v.addElement("six");
		v.addElement("seven");
		v.addElement("eight");
		return v;
	}
	
}
