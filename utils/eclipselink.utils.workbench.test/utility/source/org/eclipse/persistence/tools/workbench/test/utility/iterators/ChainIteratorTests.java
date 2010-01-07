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

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.iterators.ChainIterator;

public class ChainIteratorTests extends TestCase {
	private final static Class[] VECTOR_HIERARCHY =
		{Vector.class, AbstractList.class, AbstractCollection.class, Object.class};

	public static Test suite() {
		return new TestSuite(ChainIteratorTests.class);
	}
	
	public ChainIteratorTests(String name) {
		super(name);
	}
	
	public void testHasNext() {
		int i = 0;
		for (Iterator stream = this.buildIterator(); stream.hasNext(); ) {
			stream.next();
			i++;
		}
		assertEquals(VECTOR_HIERARCHY.length, i);
	}
	
	public void testInnerHasNext() {
		int i = 0;
		for (Iterator stream = this.buildInnerIterator(); stream.hasNext(); ) {
			stream.next();
			i++;
		}
		assertEquals(VECTOR_HIERARCHY.length, i);
	}
	
	public void testNext() {
		int i = 0;
		for (Iterator stream = this.buildIterator(); stream.hasNext(); i++) {
			assertEquals("bogus link", VECTOR_HIERARCHY[i], stream.next());
		}
	}
	
	public void testInnerNext() {
		int i = 0;
		for (Iterator stream = this.buildInnerIterator(); stream.hasNext(); i++) {
			assertEquals("bogus link", VECTOR_HIERARCHY[i], stream.next());
		}
	}
	
	public void testNoSuchElementException() {
		boolean exCaught = false;
		Iterator stream = this.buildIterator();
		Class javaClass = null;
		while (stream.hasNext()) {
			javaClass = (Class) stream.next();
		}
		try {
			javaClass = (Class) stream.next();
		} catch (NoSuchElementException ex) {
			exCaught = true;
		}
		assertTrue("NoSuchElementException not thrown: " + javaClass, exCaught);
	}
	
	public void testUnsupportedOperationException() {
		boolean exCaught = false;
		for (Iterator stream = this.buildIterator(); stream.hasNext(); ) {
			if (stream.next() == AbstractCollection.class) {
				try {
					stream.remove();
				} catch (UnsupportedOperationException ex) {
					exCaught = true;
				}
			}
		}
		assertTrue("UnsupportedOperationException not thrown", exCaught);
	}
	
	private Iterator buildIterator() {
		return this.buildChainIterator(Vector.class, this.buildLinker());
	}
	
	private Iterator buildInnerIterator() {
		return this.buildInnerChainIterator(Vector.class);
	}
	
	private Iterator buildChainIterator(Class startLink, ChainIterator.Linker linker) {
		return new ChainIterator(startLink, linker);
	}
	
	private ChainIterator.Linker buildLinker() {
		// chain up the class's hierarchy
		return new ChainIterator.Linker() {
			public Object nextLink(Object currentLink) {
				return ((Class) currentLink).getSuperclass();
			}
		};
	}
	
	private Iterator buildInnerChainIterator(Class startLink) {
		// chain up the class's hierarchy
		return new ChainIterator(startLink) {
			protected Object nextLink(Object currentLink) {
				return ((Class) currentLink).getSuperclass();
			}
		};
	}
	
}
