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
package org.eclipse.persistence.tools.workbench.test.utility;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.Range;

public class RangeTests extends TestCase {

	public static Test suite() {
		return new TestSuite(RangeTests.class);
	}

	public RangeTests(String name) {
		super(name);
	}

	public void testIncludes() {
		Range range = new Range(5, 17);
		assertFalse(range.includes(-55));
		assertFalse(range.includes(0));
		assertFalse(range.includes(4));
		assertTrue(range.includes(5));
		assertTrue(range.includes(6));
		assertTrue(range.includes(16));
		assertTrue(range.includes(17));
		assertFalse(range.includes(18));
		assertFalse(range.includes(200));
	}

	public void testEquals() {
		Range range1 = new Range(5, 17);
		Range range2 = new Range(5, 17);
		assertNotSame(range1, range2);
		assertEquals(range1, range1);
		assertEquals(range1, range2);
		assertEquals(range2, range1);
		assertEquals(range1.hashCode(), range2.hashCode());

		range2 = new Range(17, 5);
		assertFalse(range1.equals(range2));
		assertFalse(range2.equals(range1));
		// although they are unequal, they can have the same hash code
		assertEquals(range1.hashCode(), range2.hashCode());

		range2 = new Range(5, 15);
		assertFalse(range1.equals(range2));
		assertFalse(range2.equals(range1));
	}

	public void testClone() {
		Range range1 = new Range(5, 17);
		Range range2 = (Range) range1.clone();
		assertNotSame(range1, range2);
		assertEquals(range1, range1);
		assertEquals(range1, range2);
		assertEquals(range2, range1);
		assertEquals(range1.hashCode(), range2.hashCode());
	}

	public void testSerialization() throws Exception {
		Range range1 = new Range(5, 17);
		Range range2 = (Range) TestTools.serialize(range1);
		assertNotSame(range1, range2);
		assertEquals(range1, range1);
		assertEquals(range1, range2);
		assertEquals(range2, range1);
		assertEquals(range1.hashCode(), range2.hashCode());
	}

}
