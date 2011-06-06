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
package org.eclipse.persistence.tools.workbench.test.utility.diff;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.diff.CompositeDiff;
import org.eclipse.persistence.tools.workbench.utility.diff.Diff;
import org.eclipse.persistence.tools.workbench.utility.diff.DiffableDifferentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.Differentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.SimpleDiff;


public class DiffableCircularReferenceDiffTests extends AbstractCircularReferenceDiffTests {

	public static Test suite() {
		return new TestSuite(DiffableCircularReferenceDiffTests.class);
	}
	
	public DiffableCircularReferenceDiffTests(String name) {
		super(name);
	}

	protected Differentiator buildDifferentiator() {
		return DiffableDifferentiator.instance();
	}

	void verifyMismatch02(CompositeDiff cd, String differentComment) {
		SimpleDiff sd = (SimpleDiff) cd.getDiffs()[0];
		assertTrue(sd.different());
		assertEquals("Comments are different", sd.getDescriptionTitle());
		assertEquals(differentComment, sd.getObject2());
	}

	void verifyMismatch03(CompositeDiff cd) {
		Diff[] diffs = cd.getDiffs();
		CompositeDiff parentDiff;

		parentDiff = (CompositeDiff) diffs[0];
		assertTrue(parentDiff.different());
		assertEquals("parent1", ((TestNode) parentDiff.getObject1()).getName());
		assertEquals("parent2", ((TestNode) parentDiff.getObject2()).getName());

		parentDiff = (CompositeDiff) diffs[1];
		assertTrue(parentDiff.different());
		assertEquals("parent2", ((TestNode) parentDiff.getObject1()).getName());
		assertEquals("parent1", ((TestNode) parentDiff.getObject2()).getName());

		parentDiff = (CompositeDiff) diffs[2];
		assertTrue(parentDiff.identical());
	}

	void verifyMismatch09(Diff diff) {
		List leafMismatches = DiffTestTools.differentLeafDiffList(diff);
		assertEquals(1, leafMismatches.size());

		Diff leafDiff = (Diff) leafMismatches.get(0);
		assertEquals(this.testRootNode1.getNode1().getChild(), leafDiff.getObject1());
		assertEquals(null, leafDiff.getObject2());
	}

	void verifyMismatch10(TestBranchNode node, Diff diff) {
		List leafMismatches = DiffTestTools.differentLeafDiffList(diff);
		assertEquals(1, leafMismatches.size());

		Diff leafDiff = (Diff) leafMismatches.get(0);
		assertEquals(this.testRootNode1.getNode1().getChild(), leafDiff.getObject1());
		assertEquals(node.getChild(), leafDiff.getObject2());
	}

}
