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
import java.util.ListIterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.iterators.SingleElementListIterator;

public class SingleElementListIteratorTests extends SingleElementIteratorTests {

	public static Test suite() {
		return new TestSuite(SingleElementListIteratorTests.class);
	}
	
	public SingleElementListIteratorTests(String name) {
		super(name);
	}

	public void testNextIndex() {
		ListIterator stream = this.buildSingleElementListIterator(); 
		while (stream.hasNext()) {
			assertEquals("bogus index", 0, stream.nextIndex());
			stream.next();
		}
		assertEquals("bogus index", 1, stream.nextIndex());
	}
	
	public void testHasPrevious() {
		int i = 0;
		ListIterator stream = this.buildSingleElementListIterator();
		while (stream.hasNext()) {
			stream.next();
			i++;
		}
		assertEquals(1, i);
		
		while (stream.hasPrevious()) {
			stream.previous();
			i++;
		}
		assertEquals(2, i);
	}
	
	public void testPrevious() {
		ListIterator stream = this.buildSingleElementListIterator();
		
		while (stream.hasNext()) {
			assertEquals("bogus element", this.singleElement(), stream.next());
		}
		
		while (stream.hasPrevious()) {
			assertEquals("bogus element", this.singleElement(), stream.previous());
		}
	}
	
	public void testPreviousIndex() {
		ListIterator stream = this.buildSingleElementListIterator(); 
		
		while (stream.hasNext()) {
			assertEquals("bogus index", 0, stream.nextIndex());
			stream.next();
		}
		
		while (stream.hasPrevious()) {
			assertEquals("bogus index", 0, stream.previousIndex());
			stream.previous();
		}
		
		assertEquals("bogus index", -1, stream.previousIndex());
	}
	
	public void testAdd() {
		boolean exCaught = false;
		ListIterator stream = this.buildSingleElementListIterator();
		
		try {
			stream.add(new Object());
		} catch (UnsupportedOperationException ex) {
			exCaught = true;
		}
		
		assertTrue("UnsupportedOperationException not thrown", exCaught);
	}
	
	public void testSet() {
		boolean exCaught = false;
		for (ListIterator stream = this.buildSingleElementListIterator(); stream.hasNext(); ) {
			if (stream.next().equals(this.singleElement())) {
				try {
					stream.set(new Object());
				} catch (UnsupportedOperationException ex) {
					exCaught = true;
				}
			}
		}
		assertTrue("UnsupportedOperationException not thrown", exCaught);
	}
	
	protected Iterator buildSingleElementIterator() {
		return new SingleElementListIterator(this.singleElement());
	}

	protected ListIterator buildSingleElementListIterator() {
		return (ListIterator) this.buildSingleElementIterator();
	}

}
