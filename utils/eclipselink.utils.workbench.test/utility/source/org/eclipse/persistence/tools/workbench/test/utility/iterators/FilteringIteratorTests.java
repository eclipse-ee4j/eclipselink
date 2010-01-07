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

import org.eclipse.persistence.tools.workbench.utility.filters.Filter;
import org.eclipse.persistence.tools.workbench.utility.filters.SimpleFilter;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;

public class FilteringIteratorTests extends TestCase {

	private static final String PREFIX = "prefix";

	public static Test suite() {
		return new TestSuite(FilteringIteratorTests.class);
	}
	
	public FilteringIteratorTests(String name) {
		super(name);
	}
	
	public void testUnsupportedOperationException() {
		boolean exCaught = false;
		for (Iterator stream = this.buildAcceptIterator(); stream.hasNext(); ) {
			String string = (String) stream.next();
			if (string.equals(PREFIX + "3")) {
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
		Iterator stream = this.buildAcceptIterator();
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
	
	public void testAcceptHasNext() {
		int i = 0;
		for (Iterator stream = this.buildAcceptIterator(); stream.hasNext(); ) {
			stream.next();
			i++;
		}
		assertEquals(6, i);
	}
	
	public void testAcceptNext() {
		for (Iterator stream = this.buildAcceptIterator(); stream.hasNext(); ) {
			assertTrue("bogus accept", ((String) stream.next()).startsWith(PREFIX));
		}
	}
	
	public void testInnerHasNext() {
		int i = 0;
		for (Iterator stream = this.buildInnerIterator(); stream.hasNext(); ) {
			stream.next();
			i++;
		}
		assertEquals(6, i);
	}
	
	public void testInnerNext() {
		for (Iterator stream = this.buildInnerIterator(); stream.hasNext(); ) {
			assertTrue("bogus accept", ((String) stream.next()).startsWith(PREFIX));
		}
	}
	
	public void testRejectHasNext() {
		int i = 0;
		for (Iterator stream = this.buildRejectIterator(); stream.hasNext(); ) {
			stream.next();
			i++;
		}
		assertEquals(2, i);
	}
	
	public void testRejectNext() {
		for (Iterator stream = this.buildRejectIterator(); stream.hasNext(); ) {
			assertFalse("bogus reject", ((String) stream.next()).startsWith(PREFIX));
		}
	}
	
	public void testBothHasNext() {
		// if both accept() and reject() are overridden, accept() is used
		int i = 0;
		for (Iterator stream = this.buildBothIterator(); stream.hasNext(); ) {
			stream.next();
			i++;
		}
		assertEquals(6, i);
	}
	
	public void testLoadNext() {
		// loadNext() used to cause a NPE when executing during the
		// constructor because the "outer" class is not bound until completion
		// of the constructor
		for (Iterator stream = this.buildInnerIterator2(); stream.hasNext(); ) {
			assertTrue("bogus accept", ((String) stream.next()).startsWith(PREFIX));
		}
	}
	
	private Iterator buildFilteredIterator(Iterator nestedIterator, Filter filter) {
		return new FilteringIterator(nestedIterator, filter);
	}
	
	private Iterator buildInnerFilteredIterator(Iterator nestedIterator) {
		return new FilteringIterator(nestedIterator) {
			protected boolean accept(Object next) {
				return ((String) next).startsWith(PREFIX);
			}
		};
	}
	
	String getPrefix() {
		return PREFIX;
	}
	
	// this inner iterator will call the "outer" object
	private Iterator buildInnerFilteredIterator2(Iterator nestedIterator) {
		return new FilteringIterator(nestedIterator) {
			protected boolean accept(Object next) {
				return ((String) next).startsWith(FilteringIteratorTests.this.getPrefix());
			}
		};
	}
	
	private Iterator buildNestedIterator() {
		Collection c = new ArrayList();
		c.add(PREFIX + "1");
		c.add(PREFIX + "2");
		c.add(PREFIX + "3");
		c.add("4");
		c.add(PREFIX + "5");
		c.add(PREFIX + "6");
		c.add(PREFIX + "7");
		c.add("8");
		return c.iterator();
	}
	
	private Iterator buildAcceptIterator() {
		return this.buildFilteredIterator(this.buildNestedIterator(), this.buildAcceptFilter(PREFIX));
	}
	
	private Iterator buildInnerIterator() {
		return this.buildInnerFilteredIterator(this.buildNestedIterator());
	}
	
	// this inner iterator will call the "outer" object
	private Iterator buildInnerIterator2() {
		return this.buildInnerFilteredIterator2(this.buildNestedIterator());
	}
	
	private Filter buildAcceptFilter(String prefix) {
		return new SimpleFilter(prefix) {
			public boolean accept(Object next) {
				return ((String) next).startsWith((String) this.criterion);
			}
		};
	}
	
	private Iterator buildRejectIterator() {
		return this.buildFilteredIterator(this.buildNestedIterator(), this.buildRejectFilter(PREFIX));
	}
	
	private Filter buildRejectFilter(String prefix) {
		return new SimpleFilter(prefix) {
			public boolean reject(Object next) {
				return ((String) next).startsWith((String) this.criterion);
			}
		};
	}
	
	private Iterator buildBothIterator() {
		return this.buildFilteredIterator(this.buildNestedIterator(), this.buildBothFilter(PREFIX));
	}
	
	private Filter buildBothFilter(String prefix) {
		return new SimpleFilter(prefix) {
			public boolean reject(Object next) {
				return ((String) next).startsWith((String) this.criterion);
			}
			public boolean accept(Object next) {
				return ((String) next).startsWith((String) this.criterion);
			}
		};
	}
	
}
