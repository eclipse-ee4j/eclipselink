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

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.diff.CompositeDiff;
import org.eclipse.persistence.tools.workbench.utility.diff.Diff;
import org.eclipse.persistence.tools.workbench.utility.diff.DiffEngine;
import org.eclipse.persistence.tools.workbench.utility.diff.Differentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.ReflectiveDiff;
import org.eclipse.persistence.tools.workbench.utility.diff.ReflectiveDifferentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.ReflectiveFieldDiff;
import org.eclipse.persistence.tools.workbench.utility.diff.SimpleDiff;


public class DiffEngineCircularReferenceDiffTests extends AbstractCircularReferenceDiffTests {

	public static Test suite() {
		return new TestSuite(DiffEngineCircularReferenceDiffTests.class);
	}
	
	public DiffEngineCircularReferenceDiffTests(String name) {
		super(name);
	}

	Differentiator buildDifferentiator() {
		DiffEngine result = new DiffEngine();
		ReflectiveDifferentiator rd;
		rd = result.addReflectiveDifferentiator(TestNode.class);
		rd.addKeyFieldNamed("name");
		rd = result.addReflectiveDifferentiator(TestRootNode.class);
		rd = result.addReflectiveDifferentiator(TestBranchNode.class);
		rd.addKeyFieldNamed("parent");
		rd.addReferenceFieldsNamed("parent", "spouse");
		return result;
	}

	void verifyMismatch03(CompositeDiff cd) {
		ReflectiveDiff rd = (ReflectiveDiff) cd.getDiffs()[1];
		ReflectiveFieldDiff rfd = (ReflectiveFieldDiff) rd.getDiffs()[0];
		assertEquals("node1", rfd.getField().getName());
		CompositeDiff cd2 = (CompositeDiff) rfd.getDiff();
		ReflectiveDiff rd2 = (ReflectiveDiff) cd2.getDiffs()[0];
		ReflectiveFieldDiff rfd2 = (ReflectiveFieldDiff) rd2.getDiffs()[1];
		assertEquals("name", rfd2.getField().getName());
		SimpleDiff ed = (SimpleDiff) rfd2.getDiff();
		assertTrue(ed.different());
		assertEquals("parent1", ed.getObject1());
		assertEquals("parent2", ed.getObject2());
	}

	void verifyMismatch09(Diff diff) {
		List leafMismatches = DiffTestTools.differentLeafDiffList(diff);
		// one failure from TestNode reflective tester, one from TestBranchNode reflective tester
		assertEquals(2, leafMismatches.size());

		Diff leafDiff = (Diff) leafMismatches.get(0);
		assertEquals(this.testRootNode1.getNode1().getChild(), leafDiff.getObject1());
		assertEquals(null, leafDiff.getObject2());

		leafDiff = (Diff) leafMismatches.get(1);
		assertEquals(this.testRootNode1.getNode1().getChild(), leafDiff.getObject1());
		assertEquals(null, leafDiff.getObject2());
	}

	void verifyMismatch10(TestBranchNode node, Diff diff) {
		List leafMismatches = DiffTestTools.differentLeafDiffList(diff);
		// one failure from Object reflective tester, on from TestNode reflective tester, one from TestBranchNode reflective tester
		assertEquals(2, leafMismatches.size());

		Diff leafDiff = (Diff) leafMismatches.get(0);
		assertEquals(this.testRootNode1.getNode1().getChild(), leafDiff.getObject1());
		assertEquals(node.getChild(), leafDiff.getObject2());

		leafDiff = (Diff) leafMismatches.get(1);
		assertEquals(this.testRootNode1.getNode1().getChild(), leafDiff.getObject1());
		assertEquals(node.getChild(), leafDiff.getObject2());
	}

	public void testDuplicateDiff1() {
		// put an aggregate object in 2 places
		TestBranchNode node = this.testRootNode1.getNode1();
		this.testRootNode1.setNode2(node);
		boolean exCaught = false;
		try {
			Diff diff = this.differentiator.diff(this.testRootNode1, this.testRootNode2);
			fail("bogus diff: " + diff);
		} catch (IllegalArgumentException ex) {
			if (ex.getMessage().indexOf(node.toString()) != -1) {
				exCaught = true;
			} else {
				throw ex;
			}
		}
		assertTrue(exCaught);
	}

	public void testDuplicateDiff2() {
		// put an aggregate object in 2 places
		TestBranchNode node = this.testRootNode2.getNode1();
		this.testRootNode2.setNode2(node);
		boolean exCaught = false;
		try {
			Diff diff = this.differentiator.diff(this.testRootNode1, this.testRootNode2);
			fail("bogus diff: " + diff);
		} catch (IllegalArgumentException ex) {
			if (ex.getMessage().indexOf(node.toString()) != -1) {
				exCaught = true;
			} else {
				throw ex;
			}
		}
		assertTrue(exCaught);
	}

}
