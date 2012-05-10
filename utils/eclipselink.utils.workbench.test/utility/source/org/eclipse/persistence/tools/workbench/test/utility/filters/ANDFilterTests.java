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
package org.eclipse.persistence.tools.workbench.test.utility.filters;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.filters.ANDFilter;
import org.eclipse.persistence.tools.workbench.utility.filters.Filter;
import org.eclipse.persistence.tools.workbench.utility.filters.SimpleFilter;


public class ANDFilterTests extends TestCase {
	private ANDFilter andFilter;

	public static Test suite() {
		return new TestSuite(ANDFilterTests.class);
	}
	
	public ANDFilterTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.andFilter = new ANDFilter(buildMinFilter(1), buildMaxFilter(10));
	}

	private static Filter buildMinFilter(double min) {
		return new SimpleFilter(new Double(min)) {
			public boolean accept(Object next) {
				double minValue = ((Number) this.criterion).doubleValue();
				double value = ((Number) next).doubleValue();
				return value >= minValue;
			}
		};
	}

	private static Filter buildMaxFilter(double max) {
		return new SimpleFilter(new Double(max)) {
			public boolean accept(Object next) {
				double maxValue = ((Number) this.criterion).doubleValue();
				double value = ((Number) next).doubleValue();
				return value <= maxValue;
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
		assertTrue(this.andFilter.accept(new Integer(7)));
		assertTrue(this.andFilter.accept(new Integer(2)));
		assertTrue(this.andFilter.accept(new Double(6.666)));
		assertFalse(this.andFilter.accept(new Double(-99)));
		assertFalse(this.andFilter.accept(new Double(-1)));
		assertFalse(this.andFilter.accept(new Double(11)));
		assertFalse(this.andFilter.accept(new Double(111)));
	}

	public void testFiltering3() {
		ANDFilter andFilter2 = new ANDFilter(this.andFilter, buildEvenFilter());
		assertFalse(andFilter2.accept(new Integer(7)));
		assertTrue(andFilter2.accept(new Integer(2)));
		assertTrue(andFilter2.accept(new Double(6.1)));
		assertFalse(andFilter2.accept(new Double(-99)));
		assertFalse(andFilter2.accept(new Double(-1)));
		assertFalse(andFilter2.accept(new Double(11)));
		assertFalse(andFilter2.accept(new Double(111)));
	}

	public void testFilteringComposite() {
		Filter andFilter2 = ANDFilter.and(new Filter[] {buildMinFilter(1), buildMaxFilter(10), buildEvenFilter()});
		assertFalse(andFilter2.accept(new Integer(7)));
		assertTrue(andFilter2.accept(new Integer(2)));
		assertTrue(andFilter2.accept(new Double(6.1)));
		assertFalse(andFilter2.accept(new Double(-99)));
		assertFalse(andFilter2.accept(new Double(-1)));
		assertFalse(andFilter2.accept(new Double(11)));
		assertFalse(andFilter2.accept(new Double(111)));
	}

	public void testClone() {
		ANDFilter andFilter2 = (ANDFilter) this.andFilter.clone();
		assertEquals(this.andFilter.getFilter1(), andFilter2.getFilter1());
		assertEquals(this.andFilter.getFilter2(), andFilter2.getFilter2());
		assertNotSame(this.andFilter, andFilter2);
	}

	public void testEquals() {
		ANDFilter andFilter2 = new ANDFilter(buildMinFilter(1), buildMaxFilter(10));
		assertEquals(this.andFilter, andFilter2);
		assertEquals(this.andFilter.hashCode(), andFilter2.hashCode());

		andFilter2 = new ANDFilter(buildMaxFilter(10), buildMinFilter(1));
		assertEquals(this.andFilter, andFilter2);
		assertEquals(this.andFilter.hashCode(), andFilter2.hashCode());
	}

	public void testSerialization() throws Exception {
		ANDFilter andFilter2 = (ANDFilter) TestTools.serialize(this.andFilter);
		assertEquals(this.andFilter.getFilter1(), andFilter2.getFilter1());
		assertEquals(this.andFilter.getFilter2(), andFilter2.getFilter2());
		assertNotSame(this.andFilter, andFilter2);
	}

}
