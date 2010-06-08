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
package org.eclipse.persistence.tools.workbench.test.utility.diff;

import java.util.ArrayList;
import java.util.List;

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



public class ListDiffTests extends TestCase {
	private Differentiator differentiator;
	private List list1;
	private List list2;
	
	public static Test suite() {
		return new TestSuite(ListDiffTests.class);
	}
	
	public ListDiffTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.differentiator = OrderedContainerDifferentiator.forLists();
		this.list1 = this.buildList();
		this.list2 = this.buildList();
	}

	private List buildList() {
		List result = new ArrayList();
		result.add("zero");
		result.add("one");
		result.add("two");
		result.add("three");
		result.add("four");
		return result;
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testSameObject() {
		this.list2 = this.list1;
		Diff diff = this.differentiator.diff(this.list1, this.list2);
		this.verifyDiffMatch(diff, this.list1, this.list2);
	}

	public void testDifferentObjects() {
		Diff diff = this.differentiator.diff(this.list1, this.list2);
		this.verifyDiffMatch(diff, this.list1, this.list2);
	}

	public void testUnequalObjects() {
		this.list2.set(0, "xxx-" + this.list2.get(0) + "-xxx");
		this.list2.set(4, "xxx-" + this.list2.get(4) + "-xxx");
		OrderedContainerDiff diff = (OrderedContainerDiff) this.differentiator.diff(this.list1, this.list2);
		this.verifyDiffMatchMismatch(diff, this.list1, this.list2);

		Diff[] diffs = diff.getDiffs();
		assertEquals(this.list1.size(), diffs.length);
		OrderedContainerElementDiff oced;
		oced = (OrderedContainerElementDiff) diffs[0];
		assertTrue(oced.different());
			SimpleDiff ed;
			ed = (SimpleDiff) oced.getDiff();
			assertTrue(ed.getObject1().equals("zero"));
			assertTrue(ed.getObject2().equals("xxx-zero-xxx"));
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
			assertTrue(ed.getObject2().equals("xxx-four-xxx"));
	}

	public void testOneNull() {
		Object list3 = null;
		Diff diff = this.differentiator.diff(this.list1, list3);
		this.verifyDiffMatchMismatch(diff, this.list1, list3);
	}
	
	public void testNullElements() {
		this.list2.set(0, null);
		this.list2.set(4, null);
		OrderedContainerDiff diff = (OrderedContainerDiff) this.differentiator.diff(this.list1, this.list2);
		this.verifyDiffMatchMismatch(diff, this.list1, this.list2);

		Diff[] diffs = diff.getDiffs();
		assertEquals(this.list1.size(), diffs.length);
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
		this.list2.remove(4);
		OrderedContainerDiff diff = (OrderedContainerDiff) this.differentiator.diff(this.list1, this.list2);
		this.verifyDiffMatchMismatch(diff, this.list1, this.list2);

		Diff[] diffs = diff.getDiffs();
		assertEquals(this.list1.size(), diffs.length);
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

	private void verifyDiffMatchMismatch(Diff diff, Object object1, Object object2) {
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
