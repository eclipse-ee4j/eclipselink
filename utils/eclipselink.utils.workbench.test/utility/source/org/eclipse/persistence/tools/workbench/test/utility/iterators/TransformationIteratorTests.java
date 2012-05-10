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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.Transformer;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;

public class TransformationIteratorTests extends TestCase {

	public static Test suite() {
		return new TestSuite(TransformationIteratorTests.class);
	}
	
	public TransformationIteratorTests(String name) {
		super(name);
	}
	
	public void testHasNext() {
		int i = 0;
		for (Iterator stream = this.buildIterator(); stream.hasNext(); ) {
			stream.next();
			i++;
		}
		assertEquals(8, i);
	}
	
	public void testInnerHasNext() {
		int i = 0;
		for (Iterator stream = this.buildInnerIterator(); stream.hasNext(); ) {
			stream.next();
			i++;
		}
		assertEquals(8, i);
	}
	
	public void testNext() {
		int i = 0;
		for (Iterator stream = this.buildIterator(); stream.hasNext(); ) {
			assertEquals("bogus transformation", ++i, ((Integer) stream.next()).intValue());
		}
	}
	
	public void testInnerNext() {
		int i = 0;
		for (Iterator stream = this.buildInnerIterator(); stream.hasNext(); ) {
			assertEquals("bogus transformation", ++i, ((Integer) stream.next()).intValue());
		}
	}
	
	public void testRemove() {
		Collection c = this.buildCollection();
		for (Iterator stream = this.buildInnerTransformationIterator(c.iterator()); stream.hasNext(); ) {
			if (((Integer) stream.next()).intValue() == 3) {
				stream.remove();
			}
		}
		assertEquals("nothing removed", this.buildCollection().size() - 1, c.size());
		assertFalse("element still in collection", c.contains("333"));
		assertTrue("wrong element removed", c.contains("22"));
	}
	
	public void testInnerRemove() {
		Collection c = this.buildCollection();
		for (Iterator stream = this.buildTransformationIterator(c.iterator(), this.buildTransformer()); stream.hasNext(); ) {
			if (((Integer) stream.next()).intValue() == 3) {
				stream.remove();
			}
		}
		assertEquals("nothing removed", this.buildCollection().size() - 1, c.size());
		assertFalse("element still in collection", c.contains("333"));
		assertTrue("wrong element removed", c.contains("22"));
	}
	
	public void testNoSuchElementException() {
		boolean exCaught = false;
		Iterator stream = this.buildIterator();
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
	
	private Iterator buildIterator() {
		return this.buildTransformationIterator(this.buildNestedIterator(), this.buildTransformer());
	}
	
	private Iterator buildInnerIterator() {
		return this.buildInnerTransformationIterator(this.buildNestedIterator());
	}
	
	private Iterator buildUnmodifiableIterator() {
		return this.buildTransformationIterator(this.buildUnmodifiableNestedIterator(), this.buildTransformer());
	}
	
	private Iterator buildTransformationIterator(Iterator nestedIterator, Transformer transformer) {
		return new TransformationIterator(nestedIterator, transformer);
	}
	
	private Transformer buildTransformer() {
		// transform each string into an integer with a value of the string's length
		return new Transformer() {
			public Object transform(Object next) {
				return new Integer(((String) next).length());
			}
		};
	}
	
	private Iterator buildInnerTransformationIterator(Iterator nestedIterator) {
		// transform each string into an integer with a value of the string's length
		return new TransformationIterator(nestedIterator) {
			protected Object transform(Object next) {
				return new Integer(((String) next).length());
			}
		};
	}
	
	private Iterator buildNestedIterator() {
		return this.buildCollection().iterator();
	}
	
	private Iterator buildUnmodifiableNestedIterator() {
		return this.buildUnmodifiableCollection().iterator();
	}
	
	private Collection buildCollection() {
		Collection c = new ArrayList();
		c.add("1");
		c.add("22");
		c.add("333");
		c.add("4444");
		c.add("55555");
		c.add("666666");
		c.add("7777777");
		c.add("88888888");
		return c;
	}
	
	private Collection buildUnmodifiableCollection() {
		return Collections.unmodifiableCollection(this.buildCollection());
	}
	
}
