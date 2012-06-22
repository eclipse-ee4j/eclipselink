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
package org.eclipse.persistence.tools.workbench.test.framework.ui.chooser;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.eclipse.persistence.tools.workbench.test.framework.TestWorkbenchContext;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassDescriptionAdapter;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassDescriptionNode;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassDescriptionNodeContainer;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.ClassDescriptionPackagePoolNode;
import org.eclipse.persistence.tools.workbench.framework.ui.chooser.DefaultClassDescriptionAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TreeModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.DisplayableTreeCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.classfile.tools.ClassDependencyGraph;



/**
 * This dialog presents three trees to the user:
 * 	- the middle tree holds the nodes in a ClassDependencyGraph
 * 		the user will select one or more of these nodes and the
 * 		left- and right-hand trees will be populated appropriately
 * 	- the left-hand tree holds the nodes that reference the selected nodes
 * 	- the right-hand tree holds the nodes that the selected nodes reference
 */
public class ClassDependencyGraphPanel extends JPanel {
	/** this holds the class dependency graph, allowing clients to swap in a new graph */
	private PropertyValueModel graphHolder;

	/** these are the classes the user has to choose among (the middle tree) */
	private PropertyValueModel middleProjectNodeHolder;
	private TreeModel middleTreeModel;
	private TreeSelectionModel middleTreeSelectionModel;

	/** these are the classes that reference the chosen classes (the left-hand tree) */
	private JTree referencingTree;
	private PropertyValueModel referencingProjectNodeHolder;
	private TreeModel referencingTreeModel;

	/** these are the classes the chosen classes reference (the right-hand tree) */
	private JTree referencedTree;
	private PropertyValueModel referencedProjectNodeHolder;
	private TreeModel referencedTreeModel;

	/** converts the class dependency graph nodes into class names */
	private ClassDescriptionAdapter classDependencyGraphNodeAdapter;

	private WorkbenchContext context;
	

	// ********** constructors **********

	public ClassDependencyGraphPanel(PropertyValueModel graphHolder) {
		super();
		this.graphHolder = graphHolder;
		this.initialize();
	}


	// ********** initialization **********

	private void initialize() {
		this.graphHolder.addPropertyChangeListener(ValueModel.VALUE, this.buildGraphHolderListener());

		this.context = buildWorkbenchContext();
		
		this.classDependencyGraphNodeAdapter = this.buildClassDependencyGraphNodeAdapter();

		this.middleProjectNodeHolder = new SimplePropertyValueModel(this.buildMiddleProjectNode(this.graph()));
		this.middleTreeModel = new TreeModelAdapter(this.middleProjectNodeHolder);
		this.middleTreeSelectionModel = this.buildMiddleTreeSelectionModel();

		this.referencingProjectNodeHolder = new SimplePropertyValueModel(this.buildReferencingProjectNode(Collections.EMPTY_SET));
		this.referencingTreeModel = new TreeModelAdapter(this.referencingProjectNodeHolder);

		this.referencedProjectNodeHolder = new SimplePropertyValueModel(this.buildReferencedProjectNode(Collections.EMPTY_SET));
		this.referencedTreeModel = new TreeModelAdapter(this.referencedProjectNodeHolder);

		this.initializeLayout();
	}

	private PropertyChangeListener buildGraphHolderListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				ClassDependencyGraphPanel.this.graphChanged();
			}
		};
	}

	private WorkbenchContext buildWorkbenchContext() {
		return new TestWorkbenchContext();
	}

	private ClassDescriptionAdapter buildClassDependencyGraphNodeAdapter() {
		return new DefaultClassDescriptionAdapter() {
			public String className(Object classDescription) {
				return ((ClassDependencyGraph.Node) classDescription).getClassName();
			}
		};
	}

	private ClassDescriptionPackagePoolNode buildMiddleProjectNode(ClassDependencyGraph graph) {
		return new ClassDescriptionPackagePoolNode("middle", graph.nodes(), this.getClassDependencyGraphNodeAdapter(), this.context);
	}

	private ClassDescriptionAdapter getClassDependencyGraphNodeAdapter() {
		return this.classDependencyGraphNodeAdapter;
	}

	private TreeSelectionModel buildMiddleTreeSelectionModel() {
		TreeSelectionModel treeSelectionModel = new DefaultTreeSelectionModel();
		treeSelectionModel.addTreeSelectionListener(this.buildMiddleTreeSelectionListener());
		return treeSelectionModel;
	}
	
	private TreeSelectionListener buildMiddleTreeSelectionListener() {
		return new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				ClassDependencyGraphPanel.this.middleTreeSelectionChanged(e);
			}
		};
	}
	
	private void initializeLayout() {
		Border border = BorderFactory.createEmptyBorder(0, 2, 2, 0);
		Font font = new Font("Dialog", Font.PLAIN, 12);
		this.setLayout(new BorderLayout());

		// build the labels
		JPanel labelPanel = new JPanel(new GridLayout(1, 0));

		JLabel referencingLabel = new JLabel("Referencing Classes (within Scope)");
		referencingLabel.setFont(font);
		referencingLabel.setBorder(border);
		labelPanel.add(referencingLabel);

		JLabel middleLabel = new JLabel("Scope Classes");
		middleLabel.setFont(font);
		middleLabel.setBorder(border);
		labelPanel.add(middleLabel);

		JLabel referencedLabel = new JLabel("Referenced Classes");
		referencedLabel.setFont(font);
		referencedLabel.setBorder(border);
		labelPanel.add(referencedLabel);

		this.add(labelPanel, BorderLayout.NORTH);

		// build the trees
		JPanel treePanel = new JPanel(new GridLayout(1, 0));

		this.referencingTree = new JTree(this.referencingTreeModel);
		this.referencingTree.setCellRenderer(new DisplayableTreeCellRenderer());
		this.referencingTree.setRootVisible(false);
		this.referencingTree.setShowsRootHandles(true);
		this.referencingTree.setRowHeight(20);
		this.referencingTree.setDoubleBuffered(true);
		referencingLabel.setLabelFor(this.referencingTree);
		treePanel.add(new JScrollPane(this.referencingTree));

		JTree middleTree = new JTree(this.middleTreeModel);
		middleTree.setSelectionModel(this.middleTreeSelectionModel);
		middleTree.setCellRenderer(new DisplayableTreeCellRenderer());
		middleTree.setRootVisible(false);
		middleTree.setShowsRootHandles(true);
		middleTree.setRowHeight(20);
		middleTree.setDoubleBuffered(true);
		middleLabel.setLabelFor(middleTree);
		treePanel.add(new JScrollPane(middleTree));

		this.referencedTree = new JTree(this.referencedTreeModel);
		this.referencedTree.setCellRenderer(new DisplayableTreeCellRenderer());
		this.referencedTree.setRootVisible(false);
		this.referencedTree.setShowsRootHandles(true);
		this.referencedTree.setRowHeight(20);
		this.referencedTree.setDoubleBuffered(true);
		referencedLabel.setLabelFor(this.referencedTree);
		treePanel.add(new JScrollPane(this.referencedTree));

		this.add(treePanel, BorderLayout.CENTER);
	}


	// ********** queries **********

	private ClassDependencyGraph graph() {
		return (ClassDependencyGraph) this.graphHolder.getValue();
	}


	// ********** behavior **********

	/**
	 * rebuild the middle tree model
	 */
	void graphChanged() {
		this.middleProjectNodeHolder.setValue(this.buildMiddleProjectNode(this.graph()));
	}

	/**
	 * rebuild the outer tree models
	 */
	void middleTreeSelectionChanged(TreeSelectionEvent e) {
		TreePath[] selectedPaths = this.middleTreeSelectionModel.getSelectionPaths();
		// if a package is selected, all its classes are considered selected also;
		// use a HashSet because there will be duplicates
		// if both the class and its containing package are selected
		Collection selectedClassNodes = new HashSet();
		if (selectedPaths != null) {	// when everything is de-selected, this is null
			for (int i = selectedPaths.length; i-- > 0; ) {
				ClassDescriptionNodeContainer selectedNode = (ClassDescriptionNodeContainer) selectedPaths[i].getLastPathComponent();
				selectedNode.addClassDescriptionNodesTo(selectedClassNodes);
			}
		}

		// must force expansion of invisible root nodes so the packages are visible...
		ClassDescriptionPackagePoolNode referencingProjectNode = this.buildReferencingProjectNode(selectedClassNodes);
		this.referencingProjectNodeHolder.setValue(referencingProjectNode);
		this.referencingTree.expandPath(new TreePath(referencingProjectNode));

		ClassDescriptionPackagePoolNode referencedProjectNode = this.buildReferencedProjectNode(selectedClassNodes);
		this.referencedProjectNodeHolder.setValue(referencedProjectNode);
		this.referencedTree.expandPath(new TreePath(referencedProjectNode));

	}

	private ClassDescriptionPackagePoolNode buildReferencingProjectNode(Collection selectedClassNodes) {
		return new ClassDescriptionPackagePoolNode("referencing", this.referencing(selectedClassNodes), this.getClassDependencyGraphNodeAdapter(), this.context);
	}

	private ClassDescriptionPackagePoolNode buildReferencedProjectNode(Collection selectedClassNodes) {
		return new ClassDescriptionPackagePoolNode("referenced", this.referenced(selectedClassNodes), this.getClassDependencyGraphNodeAdapter(), this.context);
	}

	private Iterator referencing(Collection classNodes) {
		Map referencing = new IdentityHashMap();
		for (Iterator stream1 = classNodes.iterator(); stream1.hasNext(); ) {
			ClassDescriptionNode classNode = (ClassDescriptionNode) stream1.next();
			ClassDependencyGraph.Node cdgNode = (ClassDependencyGraph.Node) classNode.getUserClassDescription();
			for (Iterator stream2 = cdgNode.referencingNodes(); stream2.hasNext(); ) {
				Object next = stream2.next();
				referencing.put(next, next);	// use as identity set
			}
		}
		return referencing.keySet().iterator();
	}

	private Iterator referenced(Collection classNodes) {
		Map referenced = new IdentityHashMap();
		for (Iterator stream1 = classNodes.iterator(); stream1.hasNext(); ) {
			ClassDescriptionNode classNode = (ClassDescriptionNode) stream1.next();
			ClassDependencyGraph.Node cdgNode = (ClassDependencyGraph.Node) classNode.getUserClassDescription();
			for (Iterator stream2 = cdgNode.referencedNodes(); stream2.hasNext(); ) {
				Object next = stream2.next();
				referenced.put(next, next);	// use as identity set
			}
		}
		return referenced.keySet().iterator();
	}

}
