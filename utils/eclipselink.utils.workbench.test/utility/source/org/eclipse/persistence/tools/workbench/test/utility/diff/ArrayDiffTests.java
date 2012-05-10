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
package org.eclipse.persistence.tools.workbench.test.utility.diff;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.diff.Diff;
import org.eclipse.persistence.tools.workbench.utility.diff.Differentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.OrderedContainerDiff;
import org.eclipse.persistence.tools.workbench.utility.diff.OrderedContainerDifferentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.OrderedContainerElementDiff;
import org.eclipse.persistence.tools.workbench.utility.diff.SimpleDiff;



public class ArrayDiffTests extends TestCase {
	private Differentiator differentiator;
	private Object[] array1;
	private Object[] array2;

	
	public static Test suite() {
		return new TestSuite(ArrayDiffTests.class);
	}
	
	public ArrayDiffTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.differentiator = OrderedContainerDifferentiator.forArrays();
		this.array1 = this.buildArray();
		this.array2 = this.buildArray();
	}

	private Object[] buildArray() {
		Object[] result = new Object[5];
		result[0] = "zero";
		result[1] = "one";
		result[2] = "two";
		result[3] = "three";
		result[4] = "four";
		return result;
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testSameObject() {
		this.array2 = this.array1;
		Diff diff = this.differentiator.diff(this.array1, this.array2);
		this.verifyDiffMatch(diff, this.array1, this.array2);
	}

	public void testDifferentObjects() {
		Diff diff = this.differentiator.diff(this.array1, this.array2);
		this.verifyDiffMatch(diff, this.array1, this.array2);
	}

	public void testUnequalObjects() {
		this.array2[0] = "xxx-" + this.array2[0] + "-xxx";
		this.array2[4] = "xxx-" + this.array2[4] + "-xxx";
		OrderedContainerDiff diff = (OrderedContainerDiff) this.differentiator.diff(this.array1, this.array2);
		this.verifyDiffMismatch(diff, this.array1, this.array2);

		Diff[] elementDiffs = diff.getDiffs();
		assertEquals(this.array1.length, elementDiffs.length);
		OrderedContainerElementDiff oced;
		oced = (OrderedContainerElementDiff) elementDiffs[0];
		assertTrue(oced.different());
			SimpleDiff ed;
			ed = (SimpleDiff) oced.getDiff();
			assertTrue(ed.getObject1().equals("zero"));
			assertTrue(ed.getObject2().equals("xxx-zero-xxx"));
		oced = (OrderedContainerElementDiff) elementDiffs[1];
		assertTrue(oced.identical());
		oced = (OrderedContainerElementDiff) elementDiffs[2];
		assertTrue(oced.identical());
		oced = (OrderedContainerElementDiff) elementDiffs[3];
		assertTrue(oced.identical());
		oced = (OrderedContainerElementDiff) elementDiffs[4];
		assertTrue(oced.different());
			ed = (SimpleDiff) oced.getDiff();
			assertTrue(ed.getObject1().equals("four"));
			assertTrue(ed.getObject2().equals("xxx-four-xxx"));
	}

	public void testOneNull() {
		Object a2 = null;
		Diff diff = this.differentiator.diff(this.array1, a2);
		this.verifyDiffMismatch(diff, this.array1, a2);
	}
	
	public void testNullElements() {
		this.array2[0] = null;
		this.array2[4] = null;
		OrderedContainerDiff diff = (OrderedContainerDiff) this.differentiator.diff(this.array1, this.array2);
		this.verifyDiffMismatch(diff, this.array1, this.array2);

		Diff[] diffs = diff.getDiffs();
		assertEquals(this.array1.length, diffs.length);
		OrderedContainerElementDiff oced;
		oced = (OrderedContainerElementDiff) diffs[0];
		assertTrue(oced.different());
			SimpleDiff ed;
			ed = (SimpleDiff) oced.getDiff();
			assertTrue(ed.getObject1().equals("zero"));
			assertNull(ed.getObject2());
		oced = (OrderedContainerElementDiff) diffs[1];
		assertTrue(oced.identical());
		oced = (OrderedContainerElementDiff) diffs[2];
		assertTrue(oced.identical());
		oced = (OrderedContainerElementDiff) diffs[3];
		assertTrue(oced.identical());
		oced = (OrderedContainerElementDiff) diffs[4];
		assertTrue(oced.different());
			ed = (SimpleDiff) oced.getDiff();
			assertTrue(ed.getObject1().equals("four"));
			assertNull(ed.getObject2());
	}

	public void testMissingElements() {
		Object[] array3 = new Object[this.array2.length - 1];
		System.arraycopy(this.array2, 0, array3, 0, array3.length);
		this.array2 = array3;
		OrderedContainerDiff diff = (OrderedContainerDiff) this.differentiator.diff(this.array1, this.array2);
		this.verifyDiffMismatch(diff, this.array1, this.array2);

		Diff[] diffs = diff.getDiffs();
		assertEquals(this.array1.length, diffs.length);
		OrderedContainerElementDiff oced;
		oced = (OrderedContainerElementDiff) diffs[0];
		assertTrue(oced.identical());
		oced = (OrderedContainerElementDiff) diffs[1];
		assertTrue(oced.identical());
		oced = (OrderedContainerElementDiff) diffs[2];
		assertTrue(oced.identical());
		oced = (OrderedContainerElementDiff) diffs[3];
		assertTrue(oced.identical());
		Diff moced = diffs[4];
		assertTrue(moced.different());
			assertTrue(moced.getObject1().equals("four"));
			assertTrue(moced.getObject2().toString().equals("<undefined>"));
	}

	public void testMultiDimensionalArraysMatch() {
		Object[][] twoDArray1 = this.buildTwoDArray();
		Object[][] twoDArray2 = this.buildTwoDArray();
		this.differentiator = OrderedContainerDifferentiator.forArrays(OrderedContainerDifferentiator.forArrays());
		OrderedContainerDiff diff = (OrderedContainerDiff) this.differentiator.diff(twoDArray1, twoDArray2);
		this.verifyDiffMatch(diff, twoDArray1, twoDArray2);
	}

	public void testMultiDimensionalArraysMismatch() {
		Object[][] twoDArray1 = this.buildTwoDArray();
		Object[][] twoDArray2 = this.buildTwoDArray();
		Object original = twoDArray2[3][1];
		Object modified = "xxx-" + original + "-xxx";
		twoDArray2[3][1] = modified;
		this.differentiator = OrderedContainerDifferentiator.forArrays(OrderedContainerDifferentiator.forArrays());
		OrderedContainerDiff diff = (OrderedContainerDiff) this.differentiator.diff(twoDArray1, twoDArray2);
		this.verifyDiffMismatch(diff, twoDArray1, twoDArray2);
		OrderedContainerElementDiff oced = (OrderedContainerElementDiff) diff.getDiff(3);
		assertTrue(oced.different());
		diff = (OrderedContainerDiff) oced.getDiff();
		oced = (OrderedContainerElementDiff) diff.getDiff(1);
		assertTrue(oced.different());
		SimpleDiff sd = (SimpleDiff) oced.getDiff();
		assertEquals(original, sd.getObject1());
		assertEquals(modified, sd.getObject2());
	}

	private Object[][] buildTwoDArray() {
		Object[] array = this.buildArray();
		int len = array.length;
		Object[][] twoDArray = new Object[len][len];
		for (int i = 0; i < len; i++) {
			for (int j = 0; j < len; j++) {
				twoDArray[i][j] = array[i] + "-" + array[j];
			}
		}
		return twoDArray;
	}

	private void verifyDiffMismatch(Diff diff, Object object1, Object object2) {
		assertEquals(object1, diff.getObject1());
		assertEquals(object2, diff.getObject2());
		assertFalse(diff.identical());
		assertTrue(diff.different());
		assertTrue(diff.getDescription().length() > 0);
	}

	private void verifyDiffMatch(Diff diff, Object object1, Object object2) {
		assertEquals(object1, diff.getObject1());
		assertEquals(object2, diff.getObject2());
		assertTrue(diff.identical());
		assertFalse(diff.different());
		assertEquals(0, diff.getDescription().length());
	}
	
}
