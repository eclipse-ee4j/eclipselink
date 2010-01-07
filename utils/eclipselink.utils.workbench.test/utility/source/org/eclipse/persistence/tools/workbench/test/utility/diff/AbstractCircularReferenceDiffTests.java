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
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.diff.CompositeDiff;
import org.eclipse.persistence.tools.workbench.utility.diff.Diff;
import org.eclipse.persistence.tools.workbench.utility.diff.Diffable;
import org.eclipse.persistence.tools.workbench.utility.diff.DiffableDifferentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.Differentiator;
import org.eclipse.persistence.tools.workbench.utility.diff.ReflectiveDiff;
import org.eclipse.persistence.tools.workbench.utility.diff.ReflectiveFieldDiff;
import org.eclipse.persistence.tools.workbench.utility.diff.SimpleDiff;
import org.eclipse.persistence.tools.workbench.utility.string.StringTools;


public abstract class AbstractCircularReferenceDiffTests extends TestCase {
	Differentiator differentiator;
	TestRootNode testRootNode1;
	TestRootNode testRootNode2;

	
	AbstractCircularReferenceDiffTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.differentiator = this.buildDifferentiator();
		this.testRootNode1 = this.buildTestRootNode();
		this.testRootNode2 = this.buildTestRootNode();
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	abstract Differentiator buildDifferentiator();

	TestRootNode buildTestRootNode() {
		TestRootNode root = new TestRootNode("root");
		root.setComment("root comment");
		TestBranchNode parent1 = new TestBranchNode(root, "parent1");
		parent1.setComment("parent1 comment");
		TestBranchNode child1 = new TestBranchNode(parent1, "child1");
		child1.setComment("child1 comment");
		parent1.setChild(child1);
		root.setNode1(parent1);

		TestBranchNode parent2 = new TestBranchNode(root, "parent2");
		parent2.setComment("parent2 comment");
		root.setNode2(parent2);
		parent1.setSpouse(parent2);
		parent2.setSpouse(parent1);

		TestBranchNode parent3 = new TestBranchNode(root, "parent3");
		parent3.setComment("parent3 comment");
		TestBranchNode child3 = new TestBranchNode(parent3, "child3");
		child3.setComment("child3 comment");
		parent3.setChild(child3);
		root.setNode3(parent3);
		child1.setSpouse(child3);
		child3.setSpouse(child1);

		return root;
	}

	public void testMatch() {
		Diff diff = this.differentiator.diff(this.testRootNode1, this.testRootNode2);
		this.verifyDiffMatch(diff, this.testRootNode1, this.testRootNode2);
	}

	public void testMismatch01() {
		// changing the root's key modifies everything up...
		this.testRootNode2.setName("*" + this.testRootNode1.getName() + "*");
		Diff diff = this.differentiator.diff(this.testRootNode1, this.testRootNode2);
		this.verifyDiffMismatch(diff, this.testRootNode1, this.testRootNode2);
	}

	public void testMismatch02() {
		// tweak comment
		String differentComment = "*" + this.testRootNode1.getComment() + "*";
		this.testRootNode2.setComment(differentComment);
		Diff diff = (CompositeDiff) this.differentiator.diff(this.testRootNode1, this.testRootNode2);
		this.verifyDiffMismatch(diff, this.testRootNode1, this.testRootNode2);

		List leafMismatches = DiffTestTools.differentLeafDiffList(diff);
		assertEquals(1, leafMismatches.size());

		this.verifyMismatch02((CompositeDiff) diff, differentComment);
	}

	void verifyMismatch02(CompositeDiff cd, String differentComment) {
		ReflectiveDiff rd = (ReflectiveDiff) cd.getDiffs()[0];
		ReflectiveFieldDiff rfd = (ReflectiveFieldDiff) rd.getDiffs()[0];
		assertEquals("comment", rfd.getField().getName());
		SimpleDiff ed = (SimpleDiff) rfd.getDiff();
		assertTrue(ed.different());
		assertEquals(differentComment, ed.getObject2());
	}

	public void testMismatch03() {
		// swap nodes
		TestBranchNode node1 = this.testRootNode2.getNode1();
		TestBranchNode node2 = this.testRootNode2.getNode2();
		this.testRootNode2.setNode1(node2);
		this.testRootNode2.setNode2(node1);
		Diff diff = this.differentiator.diff(this.testRootNode1, this.testRootNode2);
		this.verifyDiffMismatch(diff, this.testRootNode1, this.testRootNode2);

		this.verifyMismatch03((CompositeDiff) diff);
	}

	abstract void verifyMismatch03(CompositeDiff cd);

	public void testMismatch04() {
		// tweak nested comment
		TestBranchNode node = this.testRootNode2.getNode1().getChild();
		String originalComment = node.getComment();
		String modifiedComment = "*" + originalComment + "*";
		node.setComment(modifiedComment);
		Diff diff = this.differentiator.diff(this.testRootNode1, this.testRootNode2);
		this.verifyDiffMismatch(diff, this.testRootNode1, this.testRootNode2);

		List leafMismatches = DiffTestTools.differentLeafDiffList(diff);
		assertEquals(1, leafMismatches.size());
		Diff leafDiff = (Diff) leafMismatches.get(0);
		assertEquals(originalComment, leafDiff.getObject1());
		assertEquals(modifiedComment, leafDiff.getObject2());
	}

	public void testMismatch05() {
		// null out nested comment
		TestBranchNode node = this.testRootNode2.getNode1().getChild();
		String originalComment = node.getComment();
		String modifiedComment = null;
		node.setComment(modifiedComment);
		Diff diff = this.differentiator.diff(this.testRootNode1, this.testRootNode2);
		this.verifyDiffMismatch(diff, this.testRootNode1, this.testRootNode2);

		List leafMismatches = DiffTestTools.differentLeafDiffList(diff);
		assertEquals(1, leafMismatches.size());
		Diff leafDiff = (Diff) leafMismatches.get(0);
		assertEquals(originalComment, leafDiff.getObject1());
		assertEquals(modifiedComment, leafDiff.getObject2());
	}

	public void testMismatch06() {
		// change reference
		TestBranchNode node1 = this.testRootNode2.getNode1();
		TestBranchNode node3 = this.testRootNode2.getNode3();
		String originalName = node1.getSpouse().getName();
		node1.setSpouse(node3);
		String modifiedName = node1.getSpouse().getName();
		Diff diff = this.differentiator.diff(this.testRootNode1, this.testRootNode2);
		this.verifyDiffMismatch(diff, this.testRootNode1, this.testRootNode2);

		List leafMismatches = DiffTestTools.differentLeafDiffList(diff);
		assertEquals(1, leafMismatches.size());
		Diff leafDiff = (Diff) leafMismatches.get(0);
		assertEquals(originalName, leafDiff.getObject1());
		assertEquals(modifiedName, leafDiff.getObject2());
	}

	public void testMismatch07() {
		// tweak nested key
		TestBranchNode node = this.testRootNode2.getNode1().getChild();
		String originalName = node.getName();
		String modifiedName = "*" + originalName + "*";
		node.setName(modifiedName);
		Diff diff = this.differentiator.diff(this.testRootNode1, this.testRootNode2);
		this.verifyDiffMismatch(diff, this.testRootNode1, this.testRootNode2);

		List leafMismatches = DiffTestTools.differentLeafDiffList(diff);
		// one as a child, one as a spouse
		assertEquals(2, leafMismatches.size());

		Diff leafDiff = (Diff) leafMismatches.get(0);
		assertEquals(originalName, leafDiff.getObject1());
		assertEquals(modifiedName, leafDiff.getObject2());

		leafDiff = (Diff) leafMismatches.get(1);
		assertEquals(originalName, leafDiff.getObject1());
		assertEquals(modifiedName, leafDiff.getObject2());
	}

	public void testMismatch08() {
		// null out nested key
		TestBranchNode node = this.testRootNode2.getNode1().getChild();
		String originalName = node.getName();
		String modifiedName = null;
		node.setName(modifiedName);
		Diff diff = this.differentiator.diff(this.testRootNode1, this.testRootNode2);
		this.verifyDiffMismatch(diff, this.testRootNode1, this.testRootNode2);

		List leafMismatches = DiffTestTools.differentLeafDiffList(diff);
		// one as a child, one as a spouse
		assertEquals(2, leafMismatches.size());

		Diff leafDiff = (Diff) leafMismatches.get(0);
		assertEquals(originalName, leafDiff.getObject1());
		assertEquals(modifiedName, leafDiff.getObject2());

		leafDiff = (Diff) leafMismatches.get(1);
		assertEquals(originalName, leafDiff.getObject1());
		assertEquals(modifiedName, leafDiff.getObject2());
	}

	public void testMismatch09() {
		// null out aggregate
		TestBranchNode node = this.testRootNode2.getNode1();
		node.setChild(null);
		Diff diff = this.differentiator.diff(this.testRootNode1, this.testRootNode2);
		this.verifyDiffMismatch(diff, this.testRootNode1, this.testRootNode2);
		this.verifyMismatch09(diff);
	}

	abstract void verifyMismatch09(Diff diff);

	public void testMismatch10() {
		// change aggregate class
		TestBranchNode node = this.testRootNode2.getNode1();
		TestBranchNode child = node.getChild();
		node.setChild(new TestBranchNode(node, child.getName() + " (wrong class)") {/* shell subclass */});
		Diff diff = this.differentiator.diff(this.testRootNode1, this.testRootNode2);
		this.verifyDiffMismatch(diff, this.testRootNode1, this.testRootNode2);
		this.verifyMismatch10(node, diff);
	}

	abstract void verifyMismatch10(TestBranchNode node, Diff diff);

	protected void verifyDiffMatch(Diff diff, Object object1, Object object2) {
		assertEquals(object1, diff.getObject1());
		assertEquals(object2, diff.getObject2());
		assertTrue(diff.identical());
		assertFalse(diff.different());
		assertEquals(0, diff.getDescription().length());
	}
	
	protected void verifyDiffMismatch(Diff diff, Object object1, Object object2) {
		assertEquals(object1, diff.getObject1());
		assertEquals(object2, diff.getObject2());
		assertFalse(diff.identical());
		assertTrue(diff.different());
		assertTrue(diff.getDescription().length() > 0);
	}
	

	// ********** member classes **********
	
	static abstract class TestNode implements Diffable {
		private String name;
		private String comment;
		TestNode(String name) {
			super();
			this.name = name;
		}
		public String toString() {
			return StringTools.buildToStringFor(this, this.name);
		}
		public void setName(String string) {
			this.name = string;
		}
		public String getName() {
			return this.name;
		}
		public void setComment(String string) {
			this.comment = string;
		}
		public String getComment() {
			return this.comment;
		}
		public Diff diff(Object o) {
			if ((o == null) || (this.getClass() != o.getClass())) {
				return new SimpleDiff(this, o, "Unable to compare", DiffableDifferentiator.instance());
			}
			Collection diffs = new ArrayList();
			this.addDiffsTo((TestNode) o, diffs);
			return new CompositeDiff(this, o, (Diff[]) diffs.toArray(new Diff[diffs.size()]), DiffableDifferentiator.instance());
		}
		void addDiffsTo(TestNode other, Collection diffs) {
			if ( ! this.name.equals(other.name)) {
				diffs.add(new SimpleDiff(this.name, other.name, "Names are different", DiffableDifferentiator.instance()));
			}
			if ( ! this.comment.equals(other.comment)) {
				diffs.add(new SimpleDiff(this.comment, other.comment, "Comments are different", DiffableDifferentiator.instance()));
			}
		}
		public Diff keyDiff(Object o) {
			if ((o == null) || (this.getClass() != o.getClass())) {
				return new SimpleDiff(this, o, "Unable to compare", DiffableDifferentiator.instance());
			}
			Collection diffs = new ArrayList();
			this.addKeyDiffsTo((TestNode) o, diffs);
			return new CompositeDiff(this, o, (Diff[]) diffs.toArray(new Diff[diffs.size()]), DiffableDifferentiator.instance());
		}
		void addKeyDiffsTo(TestNode other, Collection diffs) {
			if ( ! this.name.equals(other.name)) {
				diffs.add(new SimpleDiff(this.name, other.name, "Names are different", DiffableDifferentiator.instance()));
			}
		}
	}
	
	static class TestRootNode extends TestNode {
		private TestBranchNode node1;
		private TestBranchNode node2;
		private TestBranchNode node3;
		TestRootNode(String name) {
			super(name);
		}
		public void setNode1(TestBranchNode node) {
			this.node1 = node;
		}
		public TestBranchNode getNode1() {
			return this.node1;
		}
		public void setNode2(TestBranchNode node) {
			this.node2 = node;
		}
		public TestBranchNode getNode2() {
			return this.node2;
		}
		public void setNode3(TestBranchNode node) {
			this.node3 = node;
		}
		public TestBranchNode getNode3() {
			return this.node3;
		}
		void addDiffsTo(TestNode other, Collection diffs) {
			super.addDiffsTo(other, diffs);
			TestRootNode otherRoot = (TestRootNode) other;
			diffs.add(this.node1.diff(otherRoot.node1));
			diffs.add(this.node2.diff(otherRoot.node2));
			diffs.add(this.node3.diff(otherRoot.node3));
		}
	}
	
	static class TestBranchNode extends TestNode {
		private TestNode parent;
		private TestBranchNode child;
		private TestBranchNode spouse;
		TestBranchNode(TestNode parent, String name) {
			super(name);
			this.parent = parent;
		}
		public TestNode getParent() {
			return this.parent;
		}
		public void setChild(TestBranchNode node) {
			this.child = node;
		}
		public TestBranchNode getChild() {
			return this.child;
		}
		public void setSpouse(TestBranchNode node) {
			this.spouse = node;
		}
		public TestBranchNode getSpouse() {
			return this.spouse;
		}
		void addDiffsTo(TestNode other, Collection diffs) {
			super.addDiffsTo(other, diffs);
			TestBranchNode otherBranch = (TestBranchNode) other;
			diffs.add(this.parent.keyDiff(otherBranch.parent));
			if (this.child != null) {
				diffs.add(this.child.diff(otherBranch.child));
			}
			if (this.spouse != null) {
				diffs.add(this.spouse.keyDiff(otherBranch.spouse));
			}
		}
	}

}
