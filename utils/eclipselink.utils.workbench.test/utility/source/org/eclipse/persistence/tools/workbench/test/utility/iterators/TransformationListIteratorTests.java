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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.Transformer;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationListIterator;

public class TransformationListIteratorTests 
	extends TestCase 
{	
	
	public static Test suite() {
		return new TestSuite(TransformationListIteratorTests.class);
	}
	
	public TransformationListIteratorTests(String name) {
		super(name);
	}
	
	public void testHasNextAndHasPrevious() {
		int i = 0;
		ListIterator stream = this.buildIterator();
		
		while (stream.hasNext()) {
			stream.next();
			i++;
		}
		assertEquals(8, i);
		
		while (stream.hasPrevious()) {
			stream.previous();
			i--;
		}
		assertEquals(0, i);
	}
	
	public void testInnerHasNextAndHasPrevious() {
		int i = 0;
		ListIterator stream = this.buildInnerIterator();
		
		while (stream.hasNext()) {
			stream.next();
			i++;
		}
		assertEquals(8, i);
		
		while (stream.hasPrevious()) {
			stream.previous();
			i--;
		}
		assertEquals(0, i);
	}
	
	public void testNextAndPrevious() {
		int i = 0;
		ListIterator stream = this.buildIterator();
		
		while (stream.hasNext()) {
			assertEquals("bogus transformation", ++i, ((Integer) stream.next()).intValue());
		}
		
		 ++i;
		
		while (stream.hasPrevious()) {
			assertEquals("bogus transformation", --i, ((Integer) stream.previous()).intValue());
		}
	}
	
	public void testInnerNextAndPrevious() {
		int i = 0;
		ListIterator stream = this.buildInnerIterator();
		
		while (stream.hasNext()) {
			assertEquals("bogus transformation", ++i, ((Integer) stream.next()).intValue());
		}
		
		++i;
		
		while (stream.hasPrevious()) {
			assertEquals("bogus transformation", --i, ((Integer) stream.previous()).intValue());
		}
	}
	
	public void testNextIndexAndPreviousIndex() {
		int i = -1;
		ListIterator stream = this.buildIterator();
		
		while (stream.hasNext()) {
			assertEquals("bogus transformation", ++i, stream.nextIndex());
			stream.next();
		}
		
		++i;
		
		while (stream.hasPrevious()) {
			assertEquals("bogus transformation", --i, stream.previousIndex());
			stream.previous();
		}
	}
	
	public void testInnerNextIndexAndPreviousIndex() {
		int i = -1;
		ListIterator stream = this.buildInnerIterator();
		
		while (stream.hasNext()) {
			assertEquals("bogus transformation", ++i, stream.nextIndex());
			stream.next();
		}
		
		++i;
		
		while (stream.hasPrevious()) {
			assertEquals("bogus transformation", --i, stream.previousIndex());
			stream.previous();
		}
	}
	
	public void testRemove() {
		List l = this.buildList();
		for (ListIterator stream = this.buildInnerTransformationListIterator(l.listIterator()); stream.hasNext(); ) {
			if (((Integer) stream.next()).intValue() == 3) {
				stream.remove();
			}
		}
		assertEquals("nothing removed", this.buildList().size() - 1, l.size());
		assertFalse("element still in list", l.contains("333"));
		assertTrue("wrong element removed", l.contains("22"));
	}
	
	public void testInnerRemove() {
		List l = this.buildList();
		for (ListIterator stream = this.buildTransformationListIterator(l.listIterator(), this.buildTransformer()); stream.hasNext(); ) {
			if (((Integer) stream.next()).intValue() == 3) {
				stream.remove();
			}
		}
		assertEquals("nothing removed", this.buildList().size() - 1, l.size());
		assertFalse("element still in list", l.contains("333"));
		assertTrue("wrong element removed", l.contains("22"));
	}
	
	public void testUnsupportedOperationExceptionOnAdd() {
		ListIterator stream = this.buildIterator();
		boolean exCaught = false;
		try {
			stream.add("0");
			fail("exception not thrown");
		} catch (UnsupportedOperationException e) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}
	
	public void testUnsupportedOperationExceptionOnSet() {
		ListIterator stream = this.buildIterator();
		boolean exCaught = false;
		try {
			stream.set("0");
			fail("exception not thrown");
		} catch (UnsupportedOperationException e) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}
	
	public void testNoSuchElementException() {
		boolean exCaught = false;
		ListIterator stream = this.buildIterator();
		Integer integer = null;
		while (stream.hasNext()) {
			integer = (Integer) stream.next();
		}
		try {
			integer = (Integer) stream.next();
		} catch (NoSuchElementException ex) {
			exCaught = true;
		}
		assertTrue("NoSuchElementException not thrown: " + integer, exCaught);
	}
	
	public void testUnsupportedOperationException() {
		boolean exCaught = false;
		for (Iterator stream = this.buildUnmodifiableIterator(); stream.hasNext(); ) {
			int i = ((Integer) stream.next()).intValue();
			if (i == 3) {
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
		boolean exCaught = false;
		try {
			this.buildIterator().remove();
		} catch (IllegalStateException ex) {
			exCaught = true;
		}
		assertTrue("IllegalStateException not thrown", exCaught);
	}
	
	private ListIterator buildIterator() {
		return this.buildTransformationListIterator(this.buildNestedIterator(), this.buildTransformer());
	}
	
	private ListIterator buildInnerIterator() {
		return this.buildInnerTransformationListIterator(this.buildNestedIterator());
	}
	
	private ListIterator buildUnmodifiableIterator() {
		return this.buildTransformationListIterator(this.buildUnmodifiableNestedIterator(), this.buildTransformer());
	}
	
	private ListIterator buildTransformationListIterator(ListIterator nestedIterator, Transformer transformer) {
		return new TransformationListIterator(nestedIterator, transformer);
	}
	
	private Transformer buildTransformer() {
		// transform each string into an integer with a value of the string's length
		return new Transformer() {
			public Object transform(Object next) {
				return new Integer(((String) next).length());
			}
		};
	}
	
	private ListIterator buildInnerTransformationListIterator(ListIterator nestedIterator) {
		// transform each string into an integer with a value of the string's length
		return new TransformationListIterator(nestedIterator) {
			protected Object transform(Object next) {
				return new Integer(((String) next).length());
			}
		};
	}
	
	private ListIterator buildNestedIterator() {
		return this.buildList().listIterator();
	}
	
	private ListIterator buildUnmodifiableNestedIterator() {
		return this.buildUnmodifiableList().listIterator();
	}
	
	private List buildList() {
		List l = new ArrayList();
		l.add("1");
		l.add("22");
		l.add("333");
		l.add("4444");
		l.add("55555");
		l.add("666666");
		l.add("7777777");
		l.add("88888888");
		return l;
	}
	
	private List buildUnmodifiableList() {
		return Collections.unmodifiableList(this.buildList());
	}
}
