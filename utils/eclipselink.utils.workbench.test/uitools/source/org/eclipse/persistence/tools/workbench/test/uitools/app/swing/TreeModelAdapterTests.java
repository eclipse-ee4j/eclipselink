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
package org.eclipse.persistence.tools.workbench.test.uitools.app.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.uitools.Displayable;
import org.eclipse.persistence.tools.workbench.uitools.app.AbstractTreeNodeValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ItemPropertyListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.NullListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TreeModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.HashBag;
import org.eclipse.persistence.tools.workbench.utility.io.IndentingPrintWriter;
import org.eclipse.persistence.tools.workbench.utility.iterators.ReadOnlyIterator;

/**
 * 
 */
public class TreeModelAdapterTests extends TestCase {
	boolean eventFired;

	public static Test suite() {
		return new TestSuite(TreeModelAdapterTests.class);
	}
	
	public TreeModelAdapterTests(String name) {
		super(name);
	}

	public void testGetRoot() {
		TreeModel treeModel = this.buildSortedTreeModel();
		treeModel.addTreeModelListener(new TestTreeModelListener());
		TestNode rootNode = (TestNode) treeModel.getRoot();
		TestModel root = rootNode.getTestModel();
		assertEquals("root", root.getName());
//		root.dump();
//		rootNode.dump();
	}

	public void testGetChild() {
		TreeModel treeModel = this.buildSortedTreeModel();
		treeModel.addTreeModelListener(new TestTreeModelListener());
		TestNode rootNode = (TestNode) treeModel.getRoot();

		TestNode expected = rootNode.childNamed("node 1");
		TestNode actual = (TestNode) treeModel.getChild(rootNode, 1);
		assertEquals(expected, actual);

		expected = rootNode.childNamed("node 2");
		actual = (TestNode) treeModel.getChild(rootNode, 2);
		assertEquals(expected, actual);
	}

	public void testGetChildCount() {
		TreeModel treeModel = this.buildSortedTreeModel();
		treeModel.addTreeModelListener(new TestTreeModelListener());
		TestNode rootNode = (TestNode) treeModel.getRoot();

		assertEquals(5, treeModel.getChildCount(rootNode));

		TestNode node = rootNode.childNamed("node 1");
		assertEquals(1, treeModel.getChildCount(node));
	}

	public void testGetIndexOfChild() {
		TreeModel treeModel = this.buildSortedTreeModel();
		treeModel.addTreeModelListener(new TestTreeModelListener());
		TestNode rootNode = (TestNode) treeModel.getRoot();

		TestNode child = rootNode.childNamed("node 0");
		assertEquals(0, treeModel.getIndexOfChild(rootNode, child));

		child = rootNode.childNamed("node 1");
		assertEquals(1, treeModel.getIndexOfChild(rootNode, child));

		child = rootNode.childNamed("node 2");
		assertEquals(2, treeModel.getIndexOfChild(rootNode, child));
		TestNode grandchild = child.childNamed("node 2.2");
		assertEquals(2, treeModel.getIndexOfChild(child, grandchild));
	}

	public void testIsLeaf() {
		TreeModel treeModel = this.buildSortedTreeModel();
		treeModel.addTreeModelListener(new TestTreeModelListener());
		TestNode rootNode = (TestNode) treeModel.getRoot();
		assertFalse(treeModel.isLeaf(rootNode));
		TestNode node = rootNode.childNamed("node 1");
		assertFalse(treeModel.isLeaf(node));
		node = rootNode.childNamed("node 3");
		assertTrue(treeModel.isLeaf(node));
	}


	public void testTreeNodesChanged() {
		// the only way to trigger a "node changed" event is to use an unsorted tree;
		// a sorted tree will will trigger only "node removed" and "node inserted" events
		TreeModel treeModel = this.buildUnsortedTreeModel();
		this.eventFired = false;
		treeModel.addTreeModelListener(new TestTreeModelListener() {
			public void treeNodesChanged(TreeModelEvent e) {
				TreeModelAdapterTests.this.eventFired = true;
			}
		});
		TestNode rootNode = (TestNode) treeModel.getRoot();
		TestNode node = rootNode.childNamed("node 1");
		TestModel tm = node.getTestModel();
		tm.setName("node 1++");
		assertTrue(this.eventFired);

		this.eventFired = false;
		node = node.childNamed("node 1.1");
		tm = node.getTestModel();
		tm.setName("node 1.1++");
		assertTrue(this.eventFired);
	}

	public void testTreeNodesInserted() {
		// use an unsorted tree so the nodes are not re-shuffled...
		TreeModel treeModel = this.buildUnsortedTreeModel();
		this.eventFired = false;
		treeModel.addTreeModelListener(new TestTreeModelListener() {
			public void treeNodesInserted(TreeModelEvent e) {
				TreeModelAdapterTests.this.eventFired = true;
			}
		});
		TestNode rootNode = (TestNode) treeModel.getRoot();
		TestNode node = rootNode.childNamed("node 1");
		TestModel tm = node.getTestModel();
		tm.addChild("new child...");
		assertTrue(this.eventFired);

		this.eventFired = false;
		node = node.childNamed("node 1.1");
		tm = node.getTestModel();
		tm.addChild("another new child...");
		assertTrue(this.eventFired);
	}

	public void testTreeNodesRemoved() {
		TreeModel treeModel = this.buildUnsortedTreeModel();
		this.eventFired = false;
		treeModel.addTreeModelListener(new TestTreeModelListener() {
			public void treeNodesRemoved(TreeModelEvent e) {
				TreeModelAdapterTests.this.eventFired = true;
			}
		});
		TestNode rootNode = (TestNode) treeModel.getRoot();
		TestModel root = rootNode.getTestModel();
		root.removeChild(root.childNamed("node 3"));
		assertTrue(this.eventFired);

		this.eventFired = false;
		TestNode node = rootNode.childNamed("node 2");
		TestModel tm = node.getTestModel();
		tm.removeChild(tm.childNamed("node 2.2"));
		assertTrue(this.eventFired);
	}

	public void testTreeStructureChanged() {
		PropertyValueModel nodeHolder = new SimplePropertyValueModel(this.buildSortedRootNode());
		TreeModel treeModel = new TreeModelAdapter(nodeHolder);
		this.eventFired = false;
		treeModel.addTreeModelListener(new TestTreeModelListener() {
			public void treeNodesInserted(TreeModelEvent e) {
				// do nothing
			}
			public void treeNodesRemoved(TreeModelEvent e) {
				// do nothing
			}
			public void treeStructureChanged(TreeModelEvent e) {
				TreeModelAdapterTests.this.eventFired = true;
			}
		});
		nodeHolder.setValue(this.buildUnsortedRootNode());
		assertTrue(this.eventFired);
	}

	/**
	 * test a problem we had where removing a child from a tree would cause
	 * the JTree to call #equals(Object) on each node removed (actually, it was
	 * TreePath, but that was because its own #equals(Object) was called by
	 * JTree); and since we had already removed the last listener from the
	 * aspect adapter, the aspect adapter would say its value was null; this
	 * would cause a NPE until we tweaked TreeModelAdapter to remove its
	 * listeners from a node only *after* the node had been completely
	 * removed from the JTree
	 * @see TreeModelAdapter#removeNode(Object[], int, TreeNodeValueModel)
	 * @see TreeModelAdapter#addNode(Object[], int, TreeNodeValueModel)
	 */
	public void testLazyInitialization() {
		TreeModel treeModel = this.buildSpecialTreeModel();
		JTree jTree = new JTree(treeModel);
		TestNode rootNode = (TestNode) treeModel.getRoot();
		TestModel root = rootNode.getTestModel();
		// this would cause a NPE:
		root.removeChild(root.childNamed("node 3"));
		assertEquals(treeModel, jTree.getModel());
	}


	private TreeModel buildSortedTreeModel() {
		return new TreeModelAdapter(this.buildSortedRootNode());
	}

	private TestNode buildSortedRootNode() {
		return new SortedTestNode(this.buildRoot());
	}

	private TreeModel buildUnsortedTreeModel() {
		return new TreeModelAdapter(this.buildUnsortedRootNode());
	}

	private TestNode buildUnsortedRootNode() {
		return new UnsortedTestNode(this.buildRoot());
	}

	private TreeModel buildSpecialTreeModel() {
		return new TreeModelAdapter(this.buildSpecialRootNode());
	}

	private TestNode buildSpecialRootNode() {
		return new SpecialTestNode(this.buildRoot());
	}

	private TestModel buildRoot() {
		TestModel root = new TestModel("root");
		/*Node node_0 = */root.addChild("node 0");
		TestModel node_1 = root.addChild("node 1");
		TestModel node_1_1 = node_1.addChild("node 1.1");
		/*Node node_1_1_1 = */node_1_1.addChild("node 1.1.1");
		TestModel node_2 = root.addChild("node 2");
		/*Node node_2_0 = */node_2.addChild("node 2.0");
		/*Node node_2_1 = */node_2.addChild("node 2.1");
		/*Node node_2_2 = */node_2.addChild("node 2.2");
		/*Node node_2_3 = */node_2.addChild("node 2.3");
		/*Node node_2_4 = */node_2.addChild("node 2.4");
		/*Node node_2_5 = */node_2.addChild("node 2.5");
		/*Node node_3 = */root.addChild("node 3");
		/*Node node_4 = */root.addChild("node 4");
		return root;
	}


// ********** inner classes **********

/**
 * This is a typical model class with the typical change notifications
 * for #name and #children.
 */
public static class TestModel extends AbstractModel {

	// the  parent is immutable; the root's parent is null
	private TestModel parent;

	// the name is mutable; so I guess it isn't the "primary key" :-)
	private String name;
		public static final String NAME_PROPERTY = "name";

	private Collection children;
		public static final String CHILDREN_COLLECTION = "children";


	public TestModel(String name) {	// root ctor
		super();
		this.name = name;
		this.children = new HashBag();
	}
	private TestModel(TestModel parent, String name) {
		this(name);
		this.parent = parent;
	}

	public TestModel getParent() {
		return this.parent;
	}

	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		Object old = this.name;
		this.name = name;
		this.firePropertyChanged(NAME_PROPERTY, old, name);
	}

	public Iterator children() {
		return new ReadOnlyIterator(this.children);
	}
	public int childrenSize() {
		return this.children.size();
	}
	public TestModel addChild(String childName) {
		TestModel child = new TestModel(this, childName);
		this.children.add(child);
		this.fireItemAdded(CHILDREN_COLLECTION, child);
		return child;
	}
	public TestModel[] addChildren(String[] childNames) {
		TestModel[] newChildren = new TestModel[childNames.length];
		for (int i = 0; i < childNames.length; i++) {
			TestModel child = new TestModel(this, childNames[i]);
			this.children.add(child);
			newChildren[i] = child;
		}
		this.fireItemsAdded(CHILDREN_COLLECTION, Arrays.asList(newChildren));
		return newChildren;
	}
	public void removeChild(TestModel child) {
		if (this.children.remove(child)) {
			this.fireItemRemoved(CHILDREN_COLLECTION, child);
		}
	}
	public void removeChildren(TestModel[] testModels) {
		Collection removedChildren = new ArrayList();
		for (int i = 0; i < testModels.length; i++) {
			if (this.children.remove(testModels[i])) {
				removedChildren.add(testModels[i]);
			} else {
				throw new IllegalArgumentException(String.valueOf(testModels[i]));
			}
		}
		if ( ! removedChildren.isEmpty()) {
			this.fireItemsRemoved(CHILDREN_COLLECTION, removedChildren);
		}
	}
	public void clearChildren() {
		this.children.clear();
		this.fireCollectionChanged(CHILDREN_COLLECTION);
	}
	public TestModel childNamed(String childName) {
		for (Iterator stream = this.children(); stream.hasNext(); ) {
			TestModel child = (TestModel) stream.next();
			if (child.getName().equals(childName)) {
				return child;
			}
		}
		throw new RuntimeException("child not found: " + childName);
	}

	public String dumpString() {
		StringWriter sw = new StringWriter();
		IndentingPrintWriter ipw = new IndentingPrintWriter(sw);
		this.dumpOn(ipw);
		return sw.toString();
	}
	public void dumpOn(IndentingPrintWriter writer) {
		writer.println(this);
		writer.indent();
		for (Iterator stream = this.children(); stream.hasNext(); ) {
			((TestModel) stream.next()).dumpOn(writer);
		}
		writer.undent();
	}
	public void dumpOn(OutputStream stream) {
		IndentingPrintWriter writer = new IndentingPrintWriter(new OutputStreamWriter(stream));
		this.dumpOn(writer);
		writer.flush();
	}
	public void dump() {
		this.dumpOn(System.out);
	}

	public String toString() {
		return "TestModel(" + this.name + ")";
	}

}


/**
 * This Node wraps a TestModel and converts into something that can
 * be used by TreeModelAdapter. It converts changes to the TestModel's
 * name into "state changes" to the Node; and converts the
 * TestModel's children into a ListValueModel of Nodes whose order is
 * determined by subclass implementations.
 */
public static abstract class TestNode extends AbstractTreeNodeValueModel implements Displayable {
	/** the model object wrapped by this node */
	private TestModel testModel;
	/** this node's parent node; null for the root node */
	private TestNode parent;
	/** this node's child nodes */
	private ListValueModel childrenModel;
	/** a listener that notifies us when the model object's "internal state" changes */
	private PropertyChangeListener testModelListener;


	// ********** constructors **********

	/**
	 * root node constructor
	 */
	public TestNode(TestModel testModel) {
		this(null, testModel);
	}

	/**
	 * branch or leaf node constructor
	 */
	public TestNode(TestNode parent, TestModel testModel) {
		super();
		this.initialize(parent, testModel);
	}


	// ********** initialization **********

	protected void initialize() {
		super.initialize();
		this.testModelListener = this.buildTestModelListener();
	}

	private PropertyChangeListener buildTestModelListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				TestNode.this.testModelChanged(e);
			}
		};
	}

	protected void initialize(TestNode p, TestModel tm) {
		this.parent = p;
		this.testModel = tm;
		this.childrenModel = this.buildChildrenModel(tm);
	}

	/**
	 * subclasses decide the order of the child nodes
	 */
	protected abstract ListValueModel buildChildrenModel(TestModel model);

	/**
	 * used by subclasses;
	 * transform the test model children into nodes
	 */
	protected ListValueModel buildNodeAdapter(TestModel model) {
		return new TransformationListValueModelAdapter(this.buildChildrenAdapter(model)) {
			protected Object transformItem(Object item) {
				return TestNode.this.buildChildNode((TestModel) item);
			}
		};
	}

	/**
	 * subclasses must build a concrete node for the specified test model
	 */
	protected abstract TestNode buildChildNode(TestModel childTestModel);

	/**
	 * return a collection value model on the specified model's children
	 */
	protected CollectionValueModel buildChildrenAdapter(TestModel model) {
		return new CollectionAspectAdapter(TestModel.CHILDREN_COLLECTION, model) {
			protected Iterator getValueFromSubject() {
				return ((TestModel) this.subject).children();
			}
			protected int sizeFromSubject() {
				return ((TestModel) this.subject).childrenSize();
			}
		};
	}


	// ********** TreeNodeValueModel implementation **********

	public Object getValue() {
		return this.testModel;
	}

	/**
	 * this will probably never be called...
	 */
	public void setValue(Object value) {
		Object old = this.testModel;
		this.testModel = (TestModel) value;
		this.firePropertyChanged(VALUE, old, this.testModel);
	}

	public TreeNodeValueModel getParent() {
		return this.parent;
	}

	public ListValueModel getChildrenModel() {
		return this.childrenModel;
	}


	// ********** AbstractTreeNodeValueModel implementation **********

	protected void engageValue() {
		this.testModel.addPropertyChangeListener(TestModel.NAME_PROPERTY, this.testModelListener);
	}

	protected void disengageValue() {
		this.testModel.removePropertyChangeListener(TestModel.NAME_PROPERTY, this.testModelListener);
	}


	// ********** Displayable implementation **********

	public String displayString() {
		return this.testModel.getName();
	}

	public Icon icon() {
		return null;
	}


	// ********** debugging support **********

	public String dumpString() {
		StringWriter sw = new StringWriter();
		IndentingPrintWriter ipw = new IndentingPrintWriter(sw);
		this.dumpOn(ipw);
		return sw.toString();
	}

	public void dumpOn(IndentingPrintWriter writer) {
		writer.println(this);
		writer.indent();
		for (Iterator stream = (Iterator) this.childrenModel.getValue(); stream.hasNext(); ) {
			((TestNode) stream.next()).dumpOn(writer);
		}
		writer.undent();
	}

	public void dumpOn(OutputStream stream) {
		IndentingPrintWriter writer = new IndentingPrintWriter(new OutputStreamWriter(stream));
		this.dumpOn(writer);
		writer.flush();
	}

	public void dump() {
		this.dumpOn(System.out);
	}


	// ********** behavior **********

	/**
	 * the model's name has changed, forward the event to our listeners
	 */
	protected void testModelChanged(PropertyChangeEvent e) {
		// we need to notify listeners that our "internal state" has changed
		this.fireStateChanged();
		// our display string stays in synch with the model's name
		this.firePropertyChanged(DISPLAY_STRING_PROPERTY, e.getOldValue(), e.getNewValue());
	}


	// ********** queries **********

	public TestModel getTestModel() {
		return this.testModel;
	}

	/**
	 * testing convenience method
	 */
	public TestNode childNamed(String name) {
		for (Iterator stream = (Iterator) this.childrenModel.getValue(); stream.hasNext(); ) {
			TestNode childNode = (TestNode) stream.next();
			if (childNode.getTestModel().getName().equals(name)) {
				return childNode;
			}
		}
		throw new IllegalArgumentException("child not found: " + name);
	}


	// ********** standard methods **********

	/**
	 * use the standard Displayable comparator
	 */
	public int compareTo(Object o) {
		return DEFAULT_COMPARATOR.compare(this, o);
	}

	public String toString() {
		return "Node(" + this.testModel + ")";
	}

}

/**
 * concrete implementation that keeps its children sorted
 */
public static class SortedTestNode extends TestNode {

	// ********** constructors **********
	public SortedTestNode(TestModel testModel) {
		super(testModel);
	}
	public SortedTestNode(TestNode parent, TestModel testModel) {
		super(parent, testModel);
	}

	// ********** initialization **********
	/** the list should be sorted */
	protected ListValueModel buildChildrenModel(TestModel testModel) {
		return new SortedListValueModelAdapter(this.buildDisplayStringAdapter(testModel));
	}
	/** the display string (name) of each node can change */
	protected ListValueModel buildDisplayStringAdapter(TestModel testModel) {
		return new ItemPropertyListValueModelAdapter(this.buildNodeAdapter(testModel), DISPLAY_STRING_PROPERTY);
	}
	/** children are also sorted nodes */
	protected TestNode buildChildNode(TestModel childNode) {
		return new SortedTestNode(this, childNode);
	}

}


/**
 * concrete implementation that leaves its children unsorted
 */
public static class UnsortedTestNode extends TestNode {

	// ********** constructors **********
	public UnsortedTestNode(TestModel testModel) {
		super(testModel);
	}
	public UnsortedTestNode(TestNode parent, TestModel testModel) {
		super(parent, testModel);
	}

	// ********** initialization **********
	/** the list should NOT be sorted */
	protected ListValueModel buildChildrenModel(TestModel testModel) {
		return this.buildNodeAdapter(testModel);
	}
	/** children are also unsorted nodes */
	protected TestNode buildChildNode(TestModel childNode) {
		return new UnsortedTestNode(this, childNode);
	}

}


/**
 * concrete implementation that leaves its children unsorted
 * and has a special set of children for "node 3"
 */
public static class SpecialTestNode extends UnsortedTestNode {

	// ********** constructors **********
	public SpecialTestNode(TestModel testModel) {
		super(testModel);
	}
	public SpecialTestNode(TestNode parent, TestModel testModel) {
		super(parent, testModel);
	}

	// ********** initialization **********
	/** return a different list of children for "node 3" */
	protected ListValueModel buildChildrenModel(TestModel testModel) {
		if (testModel.getName().equals("node 3")) {
			return this.buildSpecialChildrenModel(testModel);
		}
		return super.buildChildrenModel(testModel);
	}
	protected ListValueModel buildSpecialChildrenModel(TestModel testModel) {
		Object[] children = new Object[1];
		children[0] = new NameTestNode(this);
		return new SimpleListValueModel(Arrays.asList(children));
	}
	/** children are also special nodes */
	protected TestNode buildChildNode(TestModel childNode) {
		return new SpecialTestNode(this, childNode);
	}

}


public static class NameTestNode extends AbstractTreeNodeValueModel {
	private PropertyValueModel nameAdapter;
	private SpecialTestNode specialNode;		// parent node
	private PropertyChangeListener nameListener;

	// ********** construction/initialization **********

	public NameTestNode(SpecialTestNode specialNode) {
		super();
		this.initialize(specialNode);
	}
	protected void initialize() {
		super.initialize();
		this.nameListener = this.buildNameListener();
	}
	protected PropertyChangeListener buildNameListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				NameTestNode.this.nameChanged(e);
			}
		};
	}
	protected void initialize(SpecialTestNode node) {
		this.specialNode = node;
		this.nameAdapter = this.buildNameAdapter();
	}

	protected PropertyValueModel buildNameAdapter() {
		return new PropertyAspectAdapter(TestModel.NAME_PROPERTY, this.getTestModel()) {
			protected Object getValueFromSubject() {
				return ((TestModel) this.subject).getName();
			}
			protected void setValueOnSubject(Object value) {
				((TestModel) this.subject).setName((String) value);
			}
		};
	}

	public TestModel getTestModel() {
		return this.specialNode.getTestModel();
	}

	// ********** TreeNodeValueModel implementation **********

	public Object getValue() {
		return this.nameAdapter.getValue();
	}
	public void setValue(Object value) {
		this.nameAdapter.setValue(value);
	}
	public TreeNodeValueModel getParent() {
		return this.specialNode;
	}
	public ListValueModel getChildrenModel() {
		return NullListValueModel.instance();
	}

	// ********** AbstractTreeNodeValueModel implementation **********

	protected void engageValue() {
		this.nameAdapter.addPropertyChangeListener(ValueModel.VALUE, this.nameListener);
	}
	protected void disengageValue() {
		this.nameAdapter.removePropertyChangeListener(ValueModel.VALUE, this.nameListener);
	}

	// ********** behavior **********

	protected void nameChanged(PropertyChangeEvent e) {
		// we need to notify listeners that our "value" has changed
		this.firePropertyChanged(VALUE, e.getOldValue(), e.getNewValue());
	}
}



/**
 * listener that will blow up with any event;
 * override and implement expected event methods
 */
public class TestTreeModelListener implements TreeModelListener {
	public void treeNodesChanged(TreeModelEvent e) {
		fail("unexpected event");
	}
	public void treeNodesInserted(TreeModelEvent e) {
		fail("unexpected event");
	}
	public void treeNodesRemoved(TreeModelEvent e) {
		fail("unexpected event");
	}
	public void treeStructureChanged(TreeModelEvent e) {
		fail("unexpected event");
	}
}

}
