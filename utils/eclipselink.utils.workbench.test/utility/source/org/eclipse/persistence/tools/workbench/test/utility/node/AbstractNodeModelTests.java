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
package org.eclipse.persistence.tools.workbench.test.utility.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.test.utility.TestTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.HashBag;
import org.eclipse.persistence.tools.workbench.utility.Range;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;
import org.eclipse.persistence.tools.workbench.utility.node.AbstractNodeModel;
import org.eclipse.persistence.tools.workbench.utility.node.Node;
import org.eclipse.persistence.tools.workbench.utility.node.Problem;


public class AbstractNodeModelTests extends TestCase {
	private TestWorkbenchModel root;

	public static Test suite() {
		return new TestSuite(AbstractNodeModelTests.class);
	}

	public AbstractNodeModelTests(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.root = this.buildRoot();
	}

	private TestWorkbenchModel buildRoot() {
		TestWorkbenchModel r = new RootTestWorkbenchModel("root");
			TestWorkbenchModel node1 = r.addTestChildNamed("node 1");
				TestWorkbenchModel node1_1 = node1.addTestChildNamed("node 1.1");
					node1_1.addTestChildNamed("node 1.1.1");
					node1_1.addTestChildNamed("node 1.1.2");
					node1_1.addTestChildNamed("node 1.1.3");
				node1.addTestChildNamed("node 1.2");
			TestWorkbenchModel node2 = r.addTestChildNamed("node 2");
				node2.addTestChildNamed("node 2.1");
				node2.addTestChildNamed("node 2.2");
			r.addTestChildNamed("node 3");
			r.addTestChildNamed("node 4");

		// mark the entire tree clean
		r.markEntireBranchClean();
		return r;
	}

	protected void tearDown() throws Exception {
		TestTools.clear(this);
		super.tearDown();
	}

	public void testTestWorkbenchModel() {
		// make sure our test class works OK...
		assertNull(this.root.testChildNamed(""));
		assertNotNull(this.root.testChildNamed("node 1"));
		assertTrue(this.root.testChildNamed("node 1").isClean());
		assertTrue(this.root.testChildNamed("node 1").isCleanBranch());
		assertNotNull(this.root.testChildNamed("node 2"));
		assertTrue(this.root.testChildNamed("node 2").isClean());
		assertTrue(this.root.testChildNamed("node 2").isCleanBranch());
		assertNull(this.root.testChildNamed("node 2.1"));

		assertNull(this.root.testDescendantNamed(""));
		assertNotNull(this.root.testDescendantNamed("node 1"));
		assertNotNull(this.root.testDescendantNamed("node 2"));
		assertNotNull(this.root.testDescendantNamed("node 2.1"));
		assertTrue(this.root.testDescendantNamed("node 2.1").isClean());
		assertTrue(this.root.testDescendantNamed("node 2.1").isCleanBranch());
		assertNotNull(this.root.testDescendantNamed("node 1.1.3"));
		assertTrue(this.root.testDescendantNamed("node 1.1.3").isClean());
		assertTrue(this.root.testDescendantNamed("node 1.1.3").isCleanBranch());
	}

	public void testParentAndChildren() {
		TestWorkbenchModel node = this.root.testDescendantNamed("node 1.1.3");
		assertEquals("node 1.1.3", node.getName());
		assertEquals(0, CollectionTools.size(node.children()));

		node = (TestWorkbenchModel) node.getParent();
		assertEquals("node 1.1", node.getName());
		assertEquals(3, CollectionTools.size(node.children()));

		node = (TestWorkbenchModel) node.getParent();
		assertEquals("node 1", node.getName());
		assertEquals(2, CollectionTools.size(node.children()));

		node = (TestWorkbenchModel) node.getParent();
		assertEquals("root", node.getName());
		assertEquals(4, CollectionTools.size(node.children()));

		node = (TestWorkbenchModel) node.getParent();
		assertNull(node);
	}

	public void testDirty() {
		TestWorkbenchModel node = this.root.testDescendantNamed("node 1.1.3");
		node.setSize(42);
		assertTrue(node.isDirty());

		TestWorkbenchModel parent = (TestWorkbenchModel) node.getParent();
		assertTrue(parent.isClean());
		assertTrue(this.root.isClean());
	}

	public void testDirtyUnchangedAttribute() {
		TestWorkbenchModel node = this.root.testDescendantNamed("node 1.1.3");
		node.setSize(42);
		assertTrue(node.isDirty());

		TestWorkbenchModel parent = (TestWorkbenchModel) node.getParent();
		assertTrue(parent.isClean());
		assertTrue(this.root.isClean());

		this.root.markEntireBranchClean();
		// set size to same number - should stay clean
		node.setSize(42);
		assertTrue(node.isClean());
		assertTrue(parent.isClean());
		assertTrue(this.root.isClean());
	}

	public void testDirtyBranch() {
		TestWorkbenchModel node = this.root.testDescendantNamed("node 1.1.3");
		node.setSize(42);
		assertTrue(node.isDirtyBranch());

		TestWorkbenchModel parent = (TestWorkbenchModel) node.getParent();
		assertTrue(parent.isDirtyBranch());
		assertTrue(this.root.isDirtyBranch());

		parent.setSize(77);
		assertTrue(parent.isDirty());
		assertTrue(parent.isDirtyBranch());

		node.markEntireBranchClean();
		assertTrue(parent.isDirty());
		assertTrue(parent.isDirtyBranch());
	}

	public void testDirtyBranchCleanChildDirtyParent() {
		TestWorkbenchModel node = this.root.testDescendantNamed("node 1.1.3");
		node.setSize(42);

		TestWorkbenchModel parent = (TestWorkbenchModel) node.getParent();
		parent.setSize(77);
		assertTrue(parent.isDirty());
		assertTrue(parent.isDirtyBranch());

		// now, clean the child, but leave the parent dirty
		node.markEntireBranchClean();
		assertTrue(parent.isDirty());
		assertTrue(parent.isDirtyBranch());
	}

	public void testDirtyBranchCleanChildDirtyChild() {
		TestWorkbenchModel node1 = this.root.testDescendantNamed("node 1.1.1");
		node1.setSize(41);
		TestWorkbenchModel node2 = this.root.testDescendantNamed("node 1.1.2");
		node2.setSize(42);

		TestWorkbenchModel parent = (TestWorkbenchModel) node1.getParent();
		assertTrue(parent.isClean());
		assertTrue(parent.isDirtyBranch());

		// now, clean the first child, but leave the second child dirty
		node1.markEntireBranchClean();
		assertTrue(parent.isClean());
		assertTrue(parent.isDirtyBranch());
	}

	public void testDirtyBranchForced() {
		TestWorkbenchModel node = this.root.testDescendantNamed("node 1.1.3");
		TestWorkbenchModel parent = (TestWorkbenchModel) node.getParent();

		assertTrue(node.isClean());
		assertTrue(node.isCleanBranch());
		assertTrue(parent.isClean());
		assertTrue(parent.isCleanBranch());
		assertTrue(this.root.isClean());
		assertTrue(this.root.isCleanBranch());

		this.root.markEntireBranchDirty();

		assertTrue(node.isDirty());
		assertTrue(node.isDirtyBranch());
		assertTrue(parent.isDirty());
		assertTrue(parent.isDirtyBranch());
		assertTrue(this.root.isDirty());
		assertTrue(this.root.isDirtyBranch());
	}

	public void testDirtyTransientAttribute() {
		TestWorkbenchModel node = this.root.testDescendantNamed("node 1.1.3");
		node.setName("BOGUS");
		assertTrue(node.isDirty());
		TestWorkbenchModel parent = (TestWorkbenchModel) node.getParent();
		assertTrue(parent.isClean());
		assertTrue(parent.isDirtyBranch());
		assertTrue(this.root.isClean());
		assertTrue(this.root.isDirtyBranch());

		this.root.markEntireBranchClean();

		this.root.validateBranch();

		assertTrue(this.root.problemsSize() == 0);
		assertTrue(node.branchProblems().hasNext());
		assertTrue(parent.problemsSize() == 0);
		assertTrue(parent.branchProblems().hasNext());
		assertTrue(node.problemsSize() > 0);

		// since problems are transient, everything should still be clean
		assertTrue(node.isClean());
		assertTrue(node.isCleanBranch());
		assertTrue(parent.isClean());
		assertTrue(parent.isCleanBranch());
		assertTrue(this.root.isClean());
		assertTrue(this.root.isCleanBranch());
	}

	public void testProblems() {
		TestWorkbenchModel node = this.root.testDescendantNamed("node 1.1.3");
		node.setName("BOGUS");
		TestWorkbenchModel parent = (TestWorkbenchModel) node.getParent();

		this.root.validateBranch();

		assertEquals(0, this.root.problemsSize());
		assertTrue(node.branchProblems().hasNext());
		assertEquals(0, parent.problemsSize());
		assertTrue(parent.branchProblems().hasNext());
		assertEquals(1, node.problemsSize());
		Problem problem1 = (Problem) node.problems().next();

		// now create another problem that should remove the old problem
		node.setName("STILL BOGUS");
		this.root.validateBranch();

		assertEquals(0, this.root.problemsSize());
		assertTrue(node.branchProblems().hasNext());
		assertEquals(0, parent.problemsSize());
		assertTrue(parent.branchProblems().hasNext());
		assertEquals(1, node.problemsSize());
		Problem problem2 = (Problem) node.problems().next();
		assertFalse(problem1 == problem2);
		problem1 = problem2;

		// now create another problem that should replace the old problem
		node.setName("STILL BOGUS");
		this.root.validateBranch();

		assertEquals(0, this.root.problemsSize());
		assertTrue(node.branchProblems().hasNext());
		assertEquals(0, parent.problemsSize());
		assertTrue(parent.branchProblems().hasNext());
		assertEquals(1, node.problemsSize());
		problem2 = (Problem) node.problems().next();
		// the same problem should be there
		assertTrue(problem1.equals(problem2));
	}

	public void testBranchProblems() {
		TestWorkbenchModel node = this.root.testDescendantNamed("node 1.1.3");
		node.setName("BOGUS");
		TestWorkbenchModel parent = (TestWorkbenchModel) node.getParent();
		parent.setName("BOGUS TOO");
		this.root.setName("BOGUS TOO TOO");

		this.root.validateBranch();

		assertEquals(1, this.root.problemsSize());
		assertEquals(3, this.root.branchProblemsSize());
		assertEquals(1, parent.problemsSize());
		assertEquals(2, parent.branchProblemsSize());
		assertEquals(1, node.problemsSize());
		assertEquals(1, node.branchProblemsSize());

		node.setName("okie-dokie");

		this.root.validateBranch();

		assertEquals(1, this.root.problemsSize());
		assertEquals(2, this.root.branchProblemsSize());
		assertEquals(1, parent.problemsSize());
		assertEquals(1, parent.branchProblemsSize());
		assertEquals(0, node.problemsSize());
		assertEquals(0, node.branchProblemsSize());
	}

	public void testClearAllBranchProblems() {
		TestWorkbenchModel node = this.root.testDescendantNamed("node 1.1.3");
		node.setName("BOGUS");
		TestWorkbenchModel parent = (TestWorkbenchModel) node.getParent();
		parent.setName("BOGUS TOO");
		this.root.setName("BOGUS TOO TOO");

		this.root.validateBranch();

		assertEquals(1, this.root.problemsSize());
		assertEquals(3, this.root.branchProblemsSize());
		assertEquals(1, parent.problemsSize());
		assertEquals(2, parent.branchProblemsSize());
		assertEquals(1, node.problemsSize());
		assertEquals(1, node.branchProblemsSize());

		parent.clearAllBranchProblems();

		assertEquals(1, this.root.problemsSize());
		assertEquals(1, this.root.branchProblemsSize());
		assertEquals(0, parent.problemsSize());
		assertEquals(0, parent.branchProblemsSize());
		assertEquals(0, node.problemsSize());
		assertEquals(0, CollectionTools.size(node.branchProblems()));
	}

	public void testRemovedBranchProblems() {
		TestWorkbenchModel node = this.root.testDescendantNamed("node 1.1.3");
		node.setName("BOGUS");
		TestWorkbenchModel parent = (TestWorkbenchModel) node.getParent();
		parent.setName("BOGUS TOO");
		this.root.setName("BOGUS TOO TOO");

		this.root.validateBranch();

		assertEquals(1, this.root.problemsSize());
		assertEquals(3, CollectionTools.size(this.root.branchProblems()));
		assertEquals(1, parent.problemsSize());
		assertEquals(2, parent.branchProblemsSize());
		assertEquals(1, node.problemsSize());
		assertEquals(1, CollectionTools.size(node.branchProblems()));

		// completely remove a node that has problems -
		// the entire tree should recalculate its "branch" problems
		parent.removeTestChild(node);

		this.root.validateBranch();

		assertEquals(1, this.root.problemsSize());
		assertEquals(2, CollectionTools.size(this.root.branchProblems()));
		assertEquals(1, parent.problemsSize());
		assertEquals(1, parent.branchProblemsSize());
	}

	public void testSort() {
		List nodes = this.buildSortedNodes();
		assertTrue(new Range(0, 1).includes(this.indexOf(nodes, "aaa")));
		assertTrue(new Range(0, 1).includes(this.indexOf(nodes, "AAA")));
		assertTrue(new Range(2, 3).includes(this.indexOf(nodes, "bbb")));
		assertTrue(new Range(2, 3).includes(this.indexOf(nodes, "BBB")));
		assertTrue(new Range(4, 6).includes(this.indexOf(nodes, "ccc")));
		assertTrue(new Range(4, 6).includes(this.indexOf(nodes, "CCC")));
		assertTrue(new Range(4, 6).includes(this.indexOf(nodes, "���")));
	}

	private int indexOf(List nodes, String nodeName) {
		for (int i = nodes.size(); i-- > 0; ) {
			if (((TestWorkbenchModel) nodes.get(i)).getName().equals(nodeName)) {
				return i;
			}
		}
		throw new IllegalArgumentException();
	}

	private List buildSortedNodes() {
		List result = new ArrayList();
		result.add(new RootTestWorkbenchModel("AAA"));
		result.add(new RootTestWorkbenchModel("BBB"));
		result.add(new RootTestWorkbenchModel("CCC"));
		result.add(new RootTestWorkbenchModel("���"));
		result.add(new RootTestWorkbenchModel("ccc"));
		result.add(new RootTestWorkbenchModel("bbb"));
		result.add(new RootTestWorkbenchModel("aaa"));
		return CollectionTools.sort(result);
	}


	// ********** inner classes **********

	private class TestWorkbenchModel extends AbstractNodeModel {
		private String name;
			public static final String NAME_PROPERTY = "name";
		private int size;
			public static final String SIZE_PROPERTY = "size";
		private Collection testChildren;
			public static final String TEST_CHILDREN_COLLECTION = "children";
	
		// ********** construction/initialization **********
		public TestWorkbenchModel(TestWorkbenchModel parent, String name) {
			super(parent);
			if (name == null) {
				throw new NullPointerException();
			}
			this.name = name;
		}
		protected void initialize() {
			super.initialize();
			this.size = 0;
			this.testChildren = new HashBag();
		}
		
		protected void checkParent(Node parent) {
			// do nothing
		}
	
	
		// ********** accessors **********
		public String getName() {
			return this.name;
		}
		public void setName(String name) {
			Object old = this.name;
			this.name = name;
			this.firePropertyChanged(NAME_PROPERTY, old, name);
		}
	
		public int getSize() {
			return this.size;
		}
		public void setSize(int size) {
			int old = this.size;
			this.size = size;
			this.firePropertyChanged(SIZE_PROPERTY, old, size);
		}
	
		public Iterator testChildren() {
			return new CloneIterator(this.testChildren) {
				protected void remove(Object current) {
					TestWorkbenchModel.this.removeTestChild((TestWorkbenchModel) current);
				}
			};
		}
		public int testChildrenSize() {
			return this.testChildren.size();
		}
		private TestWorkbenchModel addTestChild(TestWorkbenchModel testChild) {
			this.addItemToCollection(testChild, this.testChildren, TEST_CHILDREN_COLLECTION);
			return testChild;
		}
		public TestWorkbenchModel addTestChildNamed(String childName) {
			if (this.testChildNamed(childName) != null) {
				throw new IllegalArgumentException(childName);
			}
			return this.addTestChild(new TestWorkbenchModel(this, childName));
		}
		public void removeTestChild(TestWorkbenchModel testChild) {
			this.removeItemFromCollection(testChild, this.testChildren, TEST_CHILDREN_COLLECTION);
		}
	
		// ********** queries **********
		public String displayString() {
			return this.name;
		}
		public TestWorkbenchModel testChildNamed(String childName) {
			for (Iterator stream = this.testChildren.iterator(); stream.hasNext(); ) {
				TestWorkbenchModel testChild = (TestWorkbenchModel) stream.next();
				if (testChild.getName().equals(childName)) {
					return testChild;
				}
			}
			return null;
		}
		public TestWorkbenchModel testDescendantNamed(String descendantName) {
			for (Iterator stream = this.testChildren.iterator(); stream.hasNext(); ) {
				TestWorkbenchModel testDescendant = (TestWorkbenchModel) stream.next();
				if (testDescendant.getName().equals(descendantName)) {
					return testDescendant;
				}
				// recurse...
				testDescendant = testDescendant.testDescendantNamed(descendantName);
				if (testDescendant != null) {
					return testDescendant;
				}
			}
			return null;
		}
	
		// ********** behavior **********
		protected void addChildrenTo(List children) {
			super.addChildrenTo(children);
			children.addAll(this.testChildren);
		}
		protected void addProblemsTo(List currentProblems) {
			super.addProblemsTo(currentProblems);
			// names must be all lowercase...
			for (int i = this.name.length(); i-- > 0; ) {
				char c = this.name.charAt(i);
				if (Character.isLetter(c) && ! Character.isLowerCase(c)) {
					currentProblems.add(this.buildProblem("NAME_MUST_BE_LOWERCASE", this.name));
					return;
				}
			}
		}
		public void toString(StringBuffer sb) {
			sb.append(this.name);
		}
	}


	private class RootTestWorkbenchModel extends TestWorkbenchModel {
		public RootTestWorkbenchModel(String name) {
			super(null, name);
		}
		public Validator getValidator() {
			return Node.NULL_VALIDATOR;
		}
	}

}

