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
package org.eclipse.persistence.tools.workbench.test.utility.filters;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;
import org.eclipse.persistence.tools.workbench.utility.filters.SimpleFilter;
import org.eclipse.persistence.tools.workbench.utility.filters.XORFilter;


public class XORFilterTests extends TestCase {
	private XORFilter xorFilter;

	public static Test suite() {
		return new TestSuite(XORFilterTests.class);
	}
	
	public XORFilterTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.xorFilter = new XORFilter(buildMinFilter(1), buildMaxFilter(10));
	}

	private static Filter buildMinFilter(double min) {
		return new SimpleFilter(new Double(min)) {
			public boolean accept(Object next) {
				double minValue = ((Number) this.criterion).doubleValue();
				double value = ((Number) next).doubleValue();
				return value <= minValue;
			}
		};
	}

	private static Filter buildMaxFilter(double max) {
		return new SimpleFilter(new Double(max)) {
			public boolean accept(Object next) {
				double maxValue = ((Number) this.criterion).doubleValue();
				double value = ((Number) next).doubleValue();
				return value >= maxValue;
			}
		};
	}

	private static Filter buildEvenFilter() {
		return new Filter() {
			public boolean accept(Object next) {
				int value = ((Number) next).intValue();
				return (value & 1) == 0;
			}
		};
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testFiltering2() {
		assertFalse(this.xorFilter.accept(new Integer(7)));
		assertFalse(this.xorFilter.accept(new Integer(2)));
		assertFalse(this.xorFilter.accept(new Double(6.666)));
		assertTrue(this.xorFilter.accept(new Double(-99)));
		assertTrue(this.xorFilter.accept(new Double(-1)));
		assertTrue(this.xorFilter.accept(new Double(11)));
		assertTrue(this.xorFilter.accept(new Double(111)));
	}

	public void testFiltering3() {
		XORFilter xorFilter2 = new XORFilter(this.xorFilter, buildEvenFilter());
		assertFalse(xorFilter2.accept(new Integer(7)));
		assertFalse(xorFilter2.accept(new Integer(3)));
		assertFalse(xorFilter2.accept(new Integer(9)));
		assertTrue(xorFilter2.accept(new Integer(2)));
		assertTrue(xorFilter2.accept(new Double(6.1)));
		assertTrue(xorFilter2.accept(new Double(-99)));
		assertTrue(xorFilter2.accept(new Double(-1)));
		assertTrue(xorFilter2.accept(new Double(11)));
		assertTrue(xorFilter2.accept(new Double(111)));
		assertFalse(xorFilter2.accept(new Double(-98)));
		assertFalse(xorFilter2.accept(new Double(0)));
		assertFalse(xorFilter2.accept(new Double(-2)));
		assertFalse(xorFilter2.accept(new Double(12)));
		assertFalse(xorFilter2.accept(new Double(222)));
	}

	public void testClone() {
		XORFilter xorFilter2 = (XORFilter) this.xorFilter.clone();
		assertEquals(this.xorFilter.getFilter1(), xorFilter2.getFilter1());
		assertEquals(this.xorFilter.getFilter2(), xorFilter2.getFilter2());
		assertNotSame(this.xorFilter, xorFilter2);
	}

	public void testEquals() {
		XORFilter xorFilter2 = new XORFilter(buildMinFilter(1), buildMaxFilter(10));
		assertEquals(this.xorFilter, xorFilter2);
		assertEquals(this.xorFilter.hashCode(), xorFilter2.hashCode());

		xorFilter2 = new XORFilter(buildMaxFilter(10), buildMinFilter(1));
		assertEquals(this.xorFilter, xorFilter2);
		assertEquals(this.xorFilter.hashCode(), xorFilter2.hashCode());
	}

	public void testSerialization() throws Exception {
		XORFilter xorFilter2 = (XORFilter) TestTools.serialize(this.xorFilter);
		assertEquals(this.xorFilter.getFilter1(), xorFilter2.getFilter1());
		assertEquals(this.xorFilter.getFilter2(), xorFilter2.getFilter2());
		assertNotSame(this.xorFilter, xorFilter2);
	}

}
