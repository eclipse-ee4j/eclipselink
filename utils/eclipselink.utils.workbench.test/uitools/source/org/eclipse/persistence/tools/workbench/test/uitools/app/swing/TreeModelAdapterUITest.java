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
package org.eclipse.persistence.tools.workbench.test.uitools.app.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.eclipse.persistence.tools.workbench.test.uitools.app.swing.TreeModelAdapterTests.SortedTestNode;
import org.eclipse.persistence.tools.workbench.test.uitools.app.swing.TreeModelAdapterTests.TestModel;
import org.eclipse.persistence.tools.workbench.test.uitools.app.swing.TreeModelAdapterTests.TestNode;
import org.eclipse.persistence.tools.workbench.test.uitools.app.swing.TreeModelAdapterTests.UnsortedTestNode;
import org.eclipse.persistence.tools.workbench.uitools.Displayable;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TreeModelAdapter;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.EnumerationIterator;


/**
 * an example UI for testing the TreeModelAdapter
 */
public class TreeModelAdapterUITest {

	// hold the tree so we can restore its expansion state
	private JTree tree;
	private PropertyValueModel rootNodeHolder;
	private boolean sorted;
	private TreeModel treeModel;
	private TreeSelectionModel treeSelectionModel;
	private TextField nameTextField;

	public static void main(String[] args) throws Exception {
		new TreeModelAdapterUITest().exec(args);
	}

	private TreeModelAdapterUITest() {
		super();
	}

	private void exec(String[] args) throws Exception {
		this.rootNodeHolder = this.buildRootNodeHolder();
		this.sorted = this.rootNodeHolder.getValue() instanceof SortedTestNode;
		this.treeModel = this.buildTreeModel();
		this.treeSelectionModel = this.buildTreeSelectionModel();
		this.nameTextField = new TextField();
		this.openWindow();
	}

	private PropertyValueModel buildRootNodeHolder() {
		return new SimplePropertyValueModel(this.buildSortedRootNode());
	}

	private TestNode buildSortedRootNode() {
		return new SortedTestNode(this.buildRoot());
	}

	private TestNode buildUnsortedRootNode() {
		return new UnsortedTestNode(this.buildRoot());
	}

	private TestModel buildRoot() {
		TestModel root = new TestModel("root");

		TestModel node_1 = root.addChild("node 1");
		/*Node node_1_1 = */node_1.addChild("node 1.1");

		TestModel node_2 = root.addChild("node 2");
		/*Node node_2_1 = */node_2.addChild("node 2.1");
		TestModel node_2_2 = node_2.addChild("node 2.2");
		/*Node node_2_2_1 = */node_2_2.addChild("node 2.2.1");
		/*Node node_2_2_2 = */node_2_2.addChild("node 2.2.2");
		/*Node node_2_3 = */node_2.addChild("node 2.3");
		/*Node node_2_4 = */node_2.addChild("node 2.4");
		/*Node node_2_5 = */node_2.addChild("node 2.5");

		TestModel node_3 = root.addChild("node 3");
		TestModel node_3_1 = node_3.addChild("node 3.1");
		TestModel node_3_1_1 = node_3_1.addChild("node 3.1.1");
		/*Node node_3_1_1_1 = */node_3_1_1.addChild("node 3.1.1.1");

		/*Node node_4 = */root.addChild("node 4");

		return root;
	}

	private TreeModel buildTreeModel() {
		return new TreeModelAdapter(this.rootNodeHolder);
	}

	private TreeSelectionModel buildTreeSelectionModel() {
		TreeSelectionModel tsm = new DefaultTreeSelectionModel();
		tsm.addTreeSelectionListener(this.buildTreeSelectionListener());
		tsm.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		return tsm;
	}

	private TreeSelectionListener buildTreeSelectionListener() {
		return new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				TreeModelAdapterUITest.this.treeSelectionChanged(e);
			}
		};
	}

	void treeSelectionChanged(TreeSelectionEvent e) {
		TestModel selectedTestModel = this.selectedTestModel();
		if (selectedTestModel != null) {
			this.nameTextField.setText(selectedTestModel.getName());
		}
	}

	private void openWindow() {
		JFrame window = new JFrame(this.getClass().getName());
		window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(this.buildWindowListener());
		window.getContentPane().add(this.buildMainPanel(), "Center");
		window.setLocation(300, 300);
		window.setSize(400, 400);
		window.setVisible(true);
	}

	private WindowListener buildWindowListener() {
		return new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().setVisible(false);
				System.exit(0);
			}
		};
	}

	private Component buildMainPanel() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(this.buildTreePane(), BorderLayout.CENTER);
		mainPanel.add(this.buildControlPanel(), BorderLayout.SOUTH);
		return mainPanel;
	}

	private Component buildTreePane() {
		return new JScrollPane(this.buildTree());
	}

	private JTree buildTree() {
		this.tree = new JTree(this.treeModel) {
			public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
				return ((Displayable) value).displayString();
			}
		};
		this.tree.setSelectionModel(this.treeSelectionModel);
		this.tree.setRootVisible(true);
		this.tree.setShowsRootHandles(true);
		this.tree.setRowHeight(20);
		this.tree.setDoubleBuffered(true);
		return this.tree;
	}

	private Component buildControlPanel() {
		JPanel controlPanel = new JPanel(new GridLayout(0, 1));
		controlPanel.add(this.buildAddRenameNodePanel());
		controlPanel.add(this.buildMiscPanel());
		return controlPanel;
	}

	private Component buildAddRenameNodePanel() {
		JPanel addRenameNodePanel = new JPanel(new BorderLayout());
		addRenameNodePanel.add(this.buildAddButton(), BorderLayout.WEST);
		addRenameNodePanel.add(this.nameTextField, BorderLayout.CENTER);
		addRenameNodePanel.add(this.buildRenameButton(), BorderLayout.EAST);
		return addRenameNodePanel;
	}

	private Component buildMiscPanel() {
		JPanel miscPanel = new JPanel(new GridLayout(1, 0));
		miscPanel.add(this.buildClearChildrenButton());
		miscPanel.add(this.buildRemoveButton());
		miscPanel.add(this.buildResetButton());
		return miscPanel;
	}

	private String getName() {
		return this.nameTextField.getText();
	}

	// ********** queries **********
	private TestNode selectedNode() {
		if (this.treeSelectionModel.isSelectionEmpty()) {
			return null;
		}
		return (TestNode) this.treeSelectionModel.getSelectionPath().getLastPathComponent();
	}

	private TestModel selectedTestModel() {
		if (this.treeSelectionModel.isSelectionEmpty()) {
			return null;
		}
		return (TestModel) this.selectedNode().getValue();
	}

	private TestNode rootNode() {
		return (TestNode) this.treeModel.getRoot();
	}

	private TestModel root() {
		return (TestModel) this.rootNode().getValue();
	}

	private Collection expandedPaths() {
		Enumeration stream = this.tree.getExpandedDescendants(new TreePath(this.rootNode()));
		if (stream == null) {
			return Collections.EMPTY_LIST;
		}
		return CollectionTools.list(new EnumerationIterator(stream));
	}

	// ********** behavior **********
	private void setSelectedNode(TestNode selectedNode) {
		this.treeSelectionModel.setSelectionPath(new TreePath(selectedNode.path()));
	}

	private void expandPaths(Collection paths) {
		for (Iterator stream = paths.iterator(); stream.hasNext(); ) {
			this.tree.expandPath((TreePath) stream.next());
		}
	}

	// ********** add **********
	private JButton buildAddButton() {
		return new JButton(this.buildAddAction());
	}

	private Action buildAddAction() {
		Action action = new AbstractAction("add") {
			public void actionPerformed(ActionEvent event) {
				TreeModelAdapterUITest.this.addNode();
			}
		};
		action.setEnabled(true);
		return action;
	}

	/**
	 * adding causes the tree to be sorted and nodes to be
	 * removed and re-added; so we have to fiddle with the expansion state
	 */
	void addNode() {
		TestModel selectedTestModel = this.selectedTestModel();
		if (selectedTestModel != null) {
			String name = this.getName();
			// save the expansion state and restore it after the add
			Collection paths = this.expandedPaths();

			selectedTestModel.addChild(name);

			this.expandPaths(paths);
			this.setSelectedNode(this.selectedNode().childNamed(name));
		}
	}

	// ********** remove **********
	private JButton buildRemoveButton() {
		return new JButton(this.buildRemoveAction());
	}

	private Action buildRemoveAction() {
		Action action = new AbstractAction("remove") {
			public void actionPerformed(ActionEvent event) {
				TreeModelAdapterUITest.this.removeNode();
			}
		};
		action.setEnabled(true);
		return action;
	}

	/**
	 * we need to figure out which node to select after
	 * the selected node is deleted
	 */
	void removeNode() {
		TestModel selectedTestModel = this.selectedTestModel();
		// do not allow the root to be removed
		if ((selectedTestModel != null) && (selectedTestModel != this.root())) {
			// save the parent and index, so we can select another, nearby, node
			// once the selected node is removed
			TestNode parentNode = (TestNode) this.selectedNode().getParent();
			int childIndex = parentNode.indexOfChild(this.selectedNode());

			selectedTestModel.getParent().removeChild(selectedTestModel);

			int childrenSize = parentNode.childrenSize();
			if (childIndex < childrenSize) {
				// select the child that moved up and replaced the just-deleted child
				this.setSelectedNode((TestNode) parentNode.getChild(childIndex));
			} else {
				if (childrenSize == 0) {
					// if there are no more children, select the parent
					this.setSelectedNode(parentNode);
				} else {
					// if the child at the bottom of the list was deleted, select the next child up
					this.setSelectedNode((TestNode) parentNode.getChild(childIndex - 1));
				}
			}
		}
	}

	// ********** rename **********
	private JButton buildRenameButton() {
		return new JButton(this.buildRenameAction());
	}

	private Action buildRenameAction() {
		Action action = new AbstractAction("rename") {
			public void actionPerformed(ActionEvent event) {
				TreeModelAdapterUITest.this.renameNode();
			}
		};
		action.setEnabled(true);
		return action;
	}

	/**
	 * renaming causes the tree to be sorted and nodes to be
	 * removed and re-added; so we have to fiddle with the expansion state
	 */
	void renameNode() {
		TestModel selectedTestModel = this.selectedTestModel();
		if (selectedTestModel != null) {
			// save the node and re-select it after the rename
			TestNode selectedNode = this.selectedNode();
			// save the expansion state and restore it after the rename
			Collection paths = this.expandedPaths();

			selectedTestModel.setName(this.getName());

			this.expandPaths(paths);
			this.setSelectedNode(selectedNode);
		}
	}

	// ********** clear children **********
	private JButton buildClearChildrenButton() {
		return new JButton(this.buildClearChildrenAction());
	}

	private Action buildClearChildrenAction() {
		Action action = new AbstractAction("clear children") {
			public void actionPerformed(ActionEvent event) {
				TreeModelAdapterUITest.this.clearChildren();
			}
		};
		action.setEnabled(true);
		return action;
	}

	/**
	 * nothing special, we just want to test #fireCollectionChanged(String)
	 */
	void clearChildren() {
		TestModel selectedTestModel = this.selectedTestModel();
		if (selectedTestModel != null) {
			selectedTestModel.clearChildren();
		}
	}

	// ********** reset **********
	private JButton buildResetButton() {
		return new JButton(this.buildResetAction());
	}

	private Action buildResetAction() {
		Action action = new AbstractAction("reset") {
			public void actionPerformed(ActionEvent event) {
				TreeModelAdapterUITest.this.reset();
			}
		};
		action.setEnabled(true);
		return action;
	}

	/**
	 * test the adapter's root node holder;
	 * toggle between sorted and unsorted lists
	 */
	void reset() {
		this.sorted = ! this.sorted;
		if (this.sorted) {
			this.rootNodeHolder.setValue(this.buildSortedRootNode());
		} else {
			this.rootNodeHolder.setValue(this.buildUnsortedRootNode());
		}
		this.tree.expandPath(new TreePath(this.rootNode()));
	}

}
