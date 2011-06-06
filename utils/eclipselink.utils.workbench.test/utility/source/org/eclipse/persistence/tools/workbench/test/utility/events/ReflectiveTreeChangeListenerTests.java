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
package org.eclipse.persistence.tools.workbench.test.utility.events;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.events.CollectionChangeListener;
import org.eclipse.persistence.tools.workbench.utility.events.ReflectiveChangeListener;
import org.eclipse.persistence.tools.workbench.utility.events.TreeChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.TreeChangeListener;
import org.eclipse.persistence.tools.workbench.utility.iterators.CloneIterator;

public class ReflectiveTreeChangeListenerTests extends TestCase {
	
	public static Test suite() {
		return new TestSuite(ReflectiveTreeChangeListenerTests.class);
	}
	
	public ReflectiveTreeChangeListenerTests(String name) {
		super(name);
	}

	private TreeChangeListener buildZeroArgumentListener(Object target) {
		return ReflectiveChangeListener.buildTreeChangeListener(target, "nodeAddedZeroArgument", "nodeRemovedZeroArgument", "treeChangedZeroArgument");
	}

	private TreeChangeListener buildSingleArgumentListener(Object target) {
		return ReflectiveChangeListener.buildTreeChangeListener(target, "nodeAddedSingleArgument", "nodeRemovedSingleArgument", "treeChangedSingleArgument");
	}

	public void testNodeAddedZeroArgument() {
		TestModel testModel = new TestModel("root");
		Target target = new Target(testModel, TestModel.STRINGS_TREE, new String[]{"root", "child"});
		testModel.addTreeChangeListener(this.buildZeroArgumentListener(target));
		testModel.addNode("root", "child");
		assertTrue(target.nodeAddedZeroArgumentFlag);
		assertFalse(target.nodeAddedSingleArgumentFlag);
		assertFalse(target.nodeRemovedZeroArgumentFlag);
		assertFalse(target.nodeRemovedSingleArgumentFlag);
		assertFalse(target.treeChangedZeroArgumentFlag);
		assertFalse(target.treeChangedSingleArgumentFlag);
	}

	public void testNodeAddedZeroArgumentNamedTree() {
		TestModel testModel = new TestModel("root");
		Target target = new Target(testModel, TestModel.STRINGS_TREE, new String[]{"root", "child"});
		testModel.addTreeChangeListener(TestModel.STRINGS_TREE, this.buildZeroArgumentListener(target));
		testModel.addNode("root", "child");
		assertTrue(target.nodeAddedZeroArgumentFlag);
		assertFalse(target.nodeAddedSingleArgumentFlag);
		assertFalse(target.nodeRemovedZeroArgumentFlag);
		assertFalse(target.nodeRemovedSingleArgumentFlag);
		assertFalse(target.treeChangedZeroArgumentFlag);
		assertFalse(target.treeChangedSingleArgumentFlag);
	}

	public void testNodeAddedSingleArgument() {
		TestModel testModel = new TestModel("root");
		Target target = new Target(testModel, TestModel.STRINGS_TREE, new String[]{"root", "child"});
		testModel.addTreeChangeListener(this.buildSingleArgumentListener(target));
		testModel.addNode("root", "child");
		assertFalse(target.nodeAddedZeroArgumentFlag);
		assertTrue(target.nodeAddedSingleArgumentFlag);
		assertFalse(target.nodeRemovedZeroArgumentFlag);
		assertFalse(target.nodeRemovedSingleArgumentFlag);
		assertFalse(target.treeChangedZeroArgumentFlag);
		assertFalse(target.treeChangedSingleArgumentFlag);
	}

	public void testNodeAddedSingleArgumentNamedTree() {
		TestModel testModel = new TestModel("root");
		Target target = new Target(testModel, TestModel.STRINGS_TREE, new String[]{"root", "child"});
		testModel.addTreeChangeListener(TestModel.STRINGS_TREE, this.buildSingleArgumentListener(target));
		testModel.addNode("root", "child");
		assertFalse(target.nodeAddedZeroArgumentFlag);
		assertTrue(target.nodeAddedSingleArgumentFlag);
		assertFalse(target.nodeRemovedZeroArgumentFlag);
		assertFalse(target.nodeRemovedSingleArgumentFlag);
		assertFalse(target.treeChangedZeroArgumentFlag);
		assertFalse(target.treeChangedSingleArgumentFlag);
	}

	public void testNodeRemovedZeroArgument() {
		TestModel testModel = new TestModel("root");
		testModel.addNode("root", "child");
		Target target = new Target(testModel, TestModel.STRINGS_TREE, new String[]{"root", "child"});
		testModel.addTreeChangeListener(this.buildZeroArgumentListener(target));
		testModel.removeNode("child");
		assertFalse(target.nodeAddedZeroArgumentFlag);
		assertFalse(target.nodeAddedSingleArgumentFlag);
		assertTrue(target.nodeRemovedZeroArgumentFlag);
		assertFalse(target.nodeRemovedSingleArgumentFlag);
		assertFalse(target.treeChangedZeroArgumentFlag);
		assertFalse(target.treeChangedSingleArgumentFlag);
	}

	public void testNodeRemovedZeroArgumentNamedTree() {
		TestModel testModel = new TestModel("root");
		testModel.addNode("root", "child");
		Target target = new Target(testModel, TestModel.STRINGS_TREE, new String[]{"root", "child"});
		testModel.addTreeChangeListener(TestModel.STRINGS_TREE, this.buildZeroArgumentListener(target));
		testModel.removeNode("child");
		assertFalse(target.nodeAddedZeroArgumentFlag);
		assertFalse(target.nodeAddedSingleArgumentFlag);
		assertTrue(target.nodeRemovedZeroArgumentFlag);
		assertFalse(target.nodeRemovedSingleArgumentFlag);
		assertFalse(target.treeChangedZeroArgumentFlag);
		assertFalse(target.treeChangedSingleArgumentFlag);
	}

	public void testNodeRemovedSingleArgument() {
		TestModel testModel = new TestModel("root");
		testModel.addNode("root", "child");
		Target target = new Target(testModel, TestModel.STRINGS_TREE, new String[]{"root", "child"});
		testModel.addTreeChangeListener(this.buildSingleArgumentListener(target));
		testModel.removeNode("child");
		assertFalse(target.nodeAddedZeroArgumentFlag);
		assertFalse(target.nodeAddedSingleArgumentFlag);
		assertFalse(target.nodeRemovedZeroArgumentFlag);
		assertTrue(target.nodeRemovedSingleArgumentFlag);
		assertFalse(target.treeChangedZeroArgumentFlag);
		assertFalse(target.treeChangedSingleArgumentFlag);
	}

	public void testNodeRemovedSingleArgumentNamedTree() {
		TestModel testModel = new TestModel("root");
		testModel.addNode("root", "child");
		Target target = new Target(testModel, TestModel.STRINGS_TREE, new String[]{"root", "child"});
		testModel.addTreeChangeListener(TestModel.STRINGS_TREE, this.buildSingleArgumentListener(target));
		testModel.removeNode("child");
		assertFalse(target.nodeAddedZeroArgumentFlag);
		assertFalse(target.nodeAddedSingleArgumentFlag);
		assertFalse(target.nodeRemovedZeroArgumentFlag);
		assertTrue(target.nodeRemovedSingleArgumentFlag);
		assertFalse(target.treeChangedZeroArgumentFlag);
		assertFalse(target.treeChangedSingleArgumentFlag);
	}

	public void testTreeChangedZeroArgument() {
		TestModel testModel = new TestModel("root");
		testModel.addNode("root", "child");
		Target target = new Target(testModel, TestModel.STRINGS_TREE, new String[]{"root", "another child"});
		testModel.addTreeChangeListener(this.buildZeroArgumentListener(target));
		testModel.replaceNode("child", "another child");
		assertFalse(target.nodeAddedZeroArgumentFlag);
		assertFalse(target.nodeAddedSingleArgumentFlag);
		assertFalse(target.nodeRemovedZeroArgumentFlag);
		assertFalse(target.nodeRemovedSingleArgumentFlag);
		assertTrue(target.treeChangedZeroArgumentFlag);
		assertFalse(target.treeChangedSingleArgumentFlag);
	}

	public void testTreeChangedZeroArgumentNamedTree() {
		TestModel testModel = new TestModel("root");
		testModel.addNode("root", "child");
		Target target = new Target(testModel, TestModel.STRINGS_TREE, new String[]{"root", "another child"});
		testModel.addTreeChangeListener(TestModel.STRINGS_TREE, this.buildZeroArgumentListener(target));
		testModel.replaceNode("child", "another child");
		assertFalse(target.nodeAddedZeroArgumentFlag);
		assertFalse(target.nodeAddedSingleArgumentFlag);
		assertFalse(target.nodeRemovedZeroArgumentFlag);
		assertFalse(target.nodeRemovedSingleArgumentFlag);
		assertTrue(target.treeChangedZeroArgumentFlag);
		assertFalse(target.treeChangedSingleArgumentFlag);
	}

	public void testTreeChangedSingleArgument() {
		TestModel testModel = new TestModel("root");
		testModel.addNode("root", "child");
		Target target = new Target(testModel, TestModel.STRINGS_TREE, new String[]{"root", "another child"});
		testModel.addTreeChangeListener(this.buildSingleArgumentListener(target));
		testModel.replaceNode("child", "another child");
		assertFalse(target.nodeAddedZeroArgumentFlag);
		assertFalse(target.nodeAddedSingleArgumentFlag);
		assertFalse(target.nodeRemovedZeroArgumentFlag);
		assertFalse(target.nodeRemovedSingleArgumentFlag);
		assertFalse(target.treeChangedZeroArgumentFlag);
		assertTrue(target.treeChangedSingleArgumentFlag);
	}

	public void testTreeChangedSingleArgumentNamedTree() {
		TestModel testModel = new TestModel("root");
		testModel.addNode("root", "child");
		Target target = new Target(testModel, TestModel.STRINGS_TREE, new String[]{"root", "another child"});
		testModel.addTreeChangeListener(TestModel.STRINGS_TREE, this.buildSingleArgumentListener(target));
		testModel.replaceNode("child", "another child");
		assertFalse(target.nodeAddedZeroArgumentFlag);
		assertFalse(target.nodeAddedSingleArgumentFlag);
		assertFalse(target.nodeRemovedZeroArgumentFlag);
		assertFalse(target.nodeRemovedSingleArgumentFlag);
		assertFalse(target.treeChangedZeroArgumentFlag);
		assertTrue(target.treeChangedSingleArgumentFlag);
	}

	public void testBogusDoubleArgument1() {
		TestModel testModel = new TestModel("root");
		testModel.addNode("root", "child");
		Target target = new Target(testModel, TestModel.STRINGS_TREE, new String[]{"root", "child"});
		boolean exCaught = false;
		try {
			TreeChangeListener listener = ReflectiveChangeListener.buildTreeChangeListener(target, "collectionChangedDoubleArgument");
			fail("bogus listener: " + listener);
		} catch (RuntimeException ex) {
			if (ex.getCause().getClass() == NoSuchMethodException.class) {
				exCaught = true;
			}
		}
		assertTrue(exCaught);
	}

	public void testBogusDoubleArgument2() throws Exception {
		TestModel testModel = new TestModel("root");
		testModel.addNode("root", "child");
		Target target = new Target(testModel, TestModel.STRINGS_TREE, new String[]{"root", "child"});
		Method method = ClassTools.method(target, "collectionChangedDoubleArgument", new Class[] {TreeChangeEvent.class, Object.class});
		boolean exCaught = false;
		try {
			TreeChangeListener listener = ReflectiveChangeListener.buildTreeChangeListener(target, method);
			fail("bogus listener: " + listener);
		} catch (RuntimeException ex) {
			if (ex.getMessage().equals(method.toString())) {
				exCaught = true;
			}
		}
		assertTrue(exCaught);
	}

	public void testListenerMismatch() {
		TestModel testModel = new TestModel("root");
		testModel.addNode("root", "child");
		Target target = new Target(testModel, TestModel.STRINGS_TREE, new String[]{"root", "child"});
		// build a TREE change listener and hack it so we
		// can add it as a COLLECTION change listener
		Object listener = ReflectiveChangeListener.buildTreeChangeListener(target, "nodeAddedSingleArgument");
		testModel.addCollectionChangeListener((CollectionChangeListener) listener);

		boolean exCaught = false;
		try {
			testModel.changeCollection();
			fail("listener mismatch: " + listener);
		} catch (IllegalArgumentException ex) {
			exCaught = true;
		}
		assertTrue(exCaught);
	}


	private class TestModel extends AbstractModel {
		private final String root;
		private Map childrenLists = new HashMap();
		private Map parents = new HashMap();
			public static final String STRINGS_TREE = "strings";
		TestModel(String root) {
			super();
			if (root == null) {
				throw new NullPointerException();
			}
			this.root = root;
			this.childrenLists.put(root, new ArrayList());
			this.parents.put(root, null);
		}
		String getRoot() {
			return this.root;
		}
		private Object[] path(String node) {
			Object temp = node;
			List reversePath = new ArrayList();
			do {
				reversePath.add(temp);
				temp = this.parents.get(temp);
			} while (temp != null);
			return CollectionTools.reverse(reversePath).toArray();
		}
		Iterator strings() {
			return new CloneIterator(this.childrenLists.keySet()) {
				protected void remove(Object o) {
					TestModel.this.removeNode((String) o);
				}
			};
		}
		void addNode(String parent, String child) {
			if ((parent == null) || (child == null)) {
				throw new NullPointerException();
			}

			Collection children = (Collection) this.childrenLists.get(parent);
			if (children == null) {
				throw new IllegalStateException("cannot add a child to a non-existent parent");
			}

			if (this.childrenLists.get(child) != null) {
				throw new IllegalStateException("cannot add a child that is already in the tree");
			}
			
			children.add(child);
			this.childrenLists.put(child, new ArrayList());
			this.parents.put(child, parent);
			this.fireNodeAdded(STRINGS_TREE, this.path(child));
		}
		void removeNode(String node) {
			if (node == null) {
				throw new NullPointerException();
			}

			Collection children = (Collection) this.childrenLists.get(node);
			if (children == null) {
				throw new IllegalStateException("node is not in tree");
			}
			Object[] path = this.path(node);
			for (Iterator stream = children.iterator(); stream.hasNext(); ) {
				this.removeNode((String) stream.next());
			}
			this.childrenLists.remove(node);
			this.parents.remove(node);
			this.fireNodeRemoved(STRINGS_TREE, path);
		}
		void replaceNode(String oldNode, String newNode) {
			if ((oldNode == null) || (newNode == null)) {
				throw new NullPointerException();
			}

			Collection children = (Collection) this.childrenLists.remove(oldNode);
			if (children == null) {
				throw new IllegalStateException("old node is not in tree");
			}
			this.childrenLists.put(newNode, children);
			for (Iterator stream = children.iterator(); stream.hasNext(); ) {
				Object child = stream.next();
				this.parents.put(child, newNode);
			}

			Object parent = this.parents.remove(oldNode);
			this.parents.put(newNode, parent);

			this.fireTreeStructureChanged(STRINGS_TREE, this.path(newNode));
		}
		void changeCollection() {
			this.fireCollectionChanged("bogus collection");
		}
	}

	private class Target {
		TestModel testModel;
		String treeName;
		String[] path;
		boolean nodeAddedZeroArgumentFlag = false;
		boolean nodeAddedSingleArgumentFlag = false;
		boolean nodeRemovedZeroArgumentFlag = false;
		boolean nodeRemovedSingleArgumentFlag = false;
		boolean treeChangedZeroArgumentFlag = false;
		boolean treeChangedSingleArgumentFlag = false;
		Target(TestModel testModel, String treeName, String[] path) {
			super();
			this.testModel = testModel;
			this.treeName = treeName;
			this.path = path;
		}
		void nodeAddedZeroArgument() {
			this.nodeAddedZeroArgumentFlag = true;
		}
		void nodeAddedSingleArgument(TreeChangeEvent e) {
			this.nodeAddedSingleArgumentFlag = true;
			assertSame(this.testModel, e.getSource());
			assertEquals(this.treeName, e.getTreeName());
			assertTrue(Arrays.equals(this.path, e.getPath()));
		}
		void nodeRemovedZeroArgument() {
			this.nodeRemovedZeroArgumentFlag = true;
		}
		void nodeRemovedSingleArgument(TreeChangeEvent e) {
			this.nodeRemovedSingleArgumentFlag = true;
			assertSame(this.testModel, e.getSource());
			assertEquals(this.treeName, e.getTreeName());
			assertTrue(Arrays.equals(this.path, e.getPath()));
		}
		void treeChangedZeroArgument() {
			this.treeChangedZeroArgumentFlag = true;
		}
		void treeChangedSingleArgument(TreeChangeEvent e) {
			this.treeChangedSingleArgumentFlag = true;
			assertSame(this.testModel, e.getSource());
			assertEquals(this.treeName, e.getTreeName());
			assertTrue(Arrays.equals(this.path, e.getPath()));
		}
		void collectionChangedDoubleArgument(TreeChangeEvent e, Object o) {
			fail("bogus event: " + e);
		}
	}

}
