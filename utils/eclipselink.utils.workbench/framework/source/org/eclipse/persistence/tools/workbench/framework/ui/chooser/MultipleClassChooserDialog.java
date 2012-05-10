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
package org.eclipse.persistence.tools.workbench.framework.ui.chooser;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.eclipse.persistence.tools.workbench.framework.action.AbstractFrameworkAction;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TreeModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.DisplayableTreeCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.iterators.EnumerationIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.SingleElementIterator;


/**
 * This dialog presents two trees to the user:
 * - the left hand tree holds the "available" classes
 * - the right hand tree holds the "selected" classes
 * 
 * Typically the dialog opens with an empty "selected" classes tree.
 * The two arrow buttons are used to move the classes back
 * and forth between the two trees. Once the user presses
 * the OK button, clients of this dialog can retrieve the
 * "selected" classes by calling #selectedClassDescriptions().
 * 
 * The "classes" can be anything the resembles a class and can
 * be wrapped with a MetaClassAdapter (e.g. java.lang.Class,
 * java.lang.String, org.eclipse.persistence.tools.workbench.mappingsmodel.spi.meta.ExternalClass).
 */
public class MultipleClassChooserDialog extends AbstractDialog {

	/**
	 * we need to hold the trees, so we can tweak their behavior;
	 * see #moveClassNodes()		Rats!  ~bjv
	 */
	private JTree availableTree;
	private JTree selectedTree;

	/** the repository that holds all the "class descriptions" */
	private ClassDescriptionRepository repository;

	/** an adapter for displaying the "class descriptions" */
	private ClassDescriptionAdapter adapter;

	/** internally, the "class descriptions" are stored in package pools */
	private ClassDescriptionPackagePoolNode availableRootNode;
	private ClassDescriptionPackagePoolNode selectedRootNode;

	/** these are the "class descriptions" the user has to choose among (the left-hand tree) */
	private TreeModel availableTreeModel;
	TreeSelectionModel availableTreeSelectionModel;

	/** these are the "class descriptions" the user has already chosen (the right-hand tree) */
	private TreeModel selectedTreeModel;
	TreeSelectionModel selectedTreeSelectionModel;

	/** this will move "class descriptions" from the available list to the selected list */
	private Action selectAction;

	/** this will move "class descriptions" from the selected list back to the available list */
	private Action deselectAction;


	// ********** constructors **********
	
	private MultipleClassChooserDialog(WorkbenchContext context) {
		super(context);
	}

	/**
	 * use the default adapter, which expects the class descriptions to be strings
	 */
	public MultipleClassChooserDialog(WorkbenchContext context, ClassDescriptionRepository repository) {
		this(context, repository, Collections.EMPTY_LIST, DefaultClassDescriptionAdapter.instance());
	}
	
	public MultipleClassChooserDialog(WorkbenchContext context, ClassDescriptionRepository repository, ClassDescriptionAdapter adapter) {
		this(context, repository, Collections.EMPTY_LIST, adapter);
	}
	
	public MultipleClassChooserDialog(WorkbenchContext context, ClassDescriptionRepository repository, Collection preSelectedMetaClasses, ClassDescriptionAdapter adapter) {
		this(context);
		this.repository = repository;
		this.adapter = adapter;
		this.initialize(preSelectedMetaClasses);
	}
	
	// repetition of constructors taking a Dialog owner
	
	private MultipleClassChooserDialog(WorkbenchContext context, Dialog owner) {
		super(context, owner);
	}
	
	/**
	 * use the default adapter, which expects the class descriptions to be strings
	 */
	public MultipleClassChooserDialog(WorkbenchContext context, ClassDescriptionRepository repository, ClassDescriptionAdapter adapter, Dialog owner) {
		this(context, repository, Collections.EMPTY_LIST, adapter, owner);
	}
	
	public MultipleClassChooserDialog(WorkbenchContext context, ClassDescriptionRepository repository, Collection preSelectedMetaClasses, ClassDescriptionAdapter adapter, Dialog owner) {
		this(context, owner);
		this.repository = repository;
		this.adapter = adapter;
		this.initialize(preSelectedMetaClasses);
	}


	// ********** initialization **********
	
	protected void initialize() {
		super.initialize();
		this.setSize(800, 400);
	}
	
	protected void prepareToShow() {
		this.setLocationRelativeTo(this.getParent());
	}

	protected String helpTopicId() {
		return "dialog.mutipleClassChooser";
	}

	private Action buildRefreshAction() {
		return new AbstractFrameworkAction(getWorkbenchContext()) {
			protected void initialize() {
				this.initializeText("refresh");
			}
			public void actionPerformed(ActionEvent e) {
				MultipleClassChooserDialog.this.refresh();
			}
		};
	}
	
	private void initialize(Collection preSelectedMetaClasses) {
		this.availableRootNode = this.buildAvailableRootNode(preSelectedMetaClasses);
		this.availableTreeModel = new TreeModelAdapter(this.availableRootNode);
		this.availableTreeSelectionModel = this.buildAvailableTreeSelectionModel();

		this.selectedRootNode = this.buildSelectedRootNode(preSelectedMetaClasses);
		this.selectedTreeModel = new TreeModelAdapter(this.selectedRootNode);
		this.selectedTreeSelectionModel = this.buildSelectedTreeSelectionModel();

		this.selectAction = this.buildSelectAction();
		this.deselectAction = this.buildDeselectAction();

		this.setTitle(resourceRepository().getString("selectClasses.title"));
	}

	private ClassDescriptionPackagePoolNode buildAvailableRootNode(Collection preSelectedMetaClasses) {
		return new ClassDescriptionPackagePoolNode("available", this.repository.classDescriptions(), this.adapter, preSelectedMetaClasses, getWorkbenchContext());
	}

	private ClassDescriptionPackagePoolNode buildSelectedRootNode(Collection preSelectedMetaClasses) {
		return new ClassDescriptionPackagePoolNode("selected", preSelectedMetaClasses.iterator(), this.adapter, getWorkbenchContext());
	}

	private TreeSelectionModel buildTreeSelectionModel(TreeSelectionListener listener) {
		TreeSelectionModel treeSelectionModel = new DefaultTreeSelectionModel();
		treeSelectionModel.addTreeSelectionListener(listener);
		return treeSelectionModel;
	}
	
	private TreeSelectionModel buildAvailableTreeSelectionModel() {
		return this.buildTreeSelectionModel(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				MultipleClassChooserDialog.this.availableTreeSelectionChanged(e);
			}
		});
	}
	
	private TreeSelectionModel buildSelectedTreeSelectionModel() {
		return this.buildTreeSelectionModel(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				MultipleClassChooserDialog.this.selectedTreeSelectionChanged(e);
			}
		});
	}
	
	private Action buildSelectAction() {
		Action action = new AbstractFrameworkAction(getWorkbenchContext()) {
			public void actionPerformed(ActionEvent event) {
				MultipleClassChooserDialog.this.selectClassNodes();
			}
			@Override
			protected void initialize() {
				super.initializeIcon("shuttle.right");
			}
		};
		action.setEnabled(false);
		return action;
	}
	
	private Action buildDeselectAction() {
		Action action = new AbstractFrameworkAction(getWorkbenchContext()) {
			public void actionPerformed(ActionEvent event) {
				MultipleClassChooserDialog.this.deselectClassNodes();
			}
			@Override
			protected void initialize() {
				super.initializeIcon("shuttle.left");
			}
		};
		action.setEnabled(false);
		return action;
	}


	// ********** queries **********
	
	private ClassDescriptionPackagePoolNode getAvailableRootNode() {
		return this.availableRootNode;
	}
	
	private ClassDescriptionPackagePoolNode getSelectedRootNode() {
		return this.selectedRootNode;
	}
	
	public Iterator selectedClassDescriptions() {
		return this.getSelectedRootNode().userClassDescriptions();
	}


	// ********** behavior **********
	
	void availableTreeSelectionChanged(TreeSelectionEvent e) {
		// only enable the select button when something is selected on the left
		this.selectAction.setEnabled(this.availableTreeSelectionModel.getSelectionCount() != 0);
	}
	
	void selectedTreeSelectionChanged(TreeSelectionEvent e) {
		// only enable the deselect button when something is selected on the right
		this.deselectAction.setEnabled(this.selectedTreeSelectionModel.getSelectionCount() != 0);
	}

	private Collection expandedPaths(JTree tree, Object root) {
		Enumeration stream = tree.getExpandedDescendants(new TreePath(root));
		if (stream == null) {
			return Collections.EMPTY_LIST;
		}
		return CollectionTools.list(new EnumerationIterator(stream));
	}

	private void moveClassNodes(ClassDescriptionPackagePoolNode source, ClassDescriptionPackagePoolNode target, TreeSelectionModel sourceSelectionModel, TreeSelectionModel targetSelectionModel, JTree targetTree) {
		// unsorted trees: if the root node is hidden and it does not have any children
		// (which is how the "selected" tree typically starts out),
		// then we need to programmatically force it to expand after adding children
		boolean expandTargetRootNode = target.isLeaf();
		
		// sorted trees: try to preserve the expansion state of target tree
		Collection expandedPaths = this.expandedPaths(targetTree, target);

		TreePath[] selectedPaths = sourceSelectionModel.getSelectionPaths();
		// if a package is selected, all its classes are considered selected also;
		// use a HashSet because there will be duplicates
		// if both the class and its containing package are selected
		Collection selectedClassNodes = new HashSet();
		for (int i = selectedPaths.length; i-- > 0; ) {
			ClassDescriptionNodeContainer selectedNode = (ClassDescriptionNodeContainer) selectedPaths[i].getLastPathComponent();
			selectedNode.addClassDescriptionNodesTo(selectedClassNodes);
		}
		Collection selectedPackageNodes = new HashSet();
		for (Iterator stream = selectedClassNodes.iterator(); stream.hasNext(); ) {
			ClassDescriptionNode classNode = (ClassDescriptionNode) stream.next();
			source.removeClassNode(classNode);
			target.addClassNode(classNode);
			selectedPackageNodes.add(classNode.getPackageNode());
		}
		// expand any packages that had classes added to them
		for (Iterator stream = selectedPackageNodes.iterator(); stream.hasNext(); ) {
			ClassDescriptionPackageNode packageNode = (ClassDescriptionPackageNode) stream.next();
			targetTree.expandPath(new TreePath(packageNode.path()));
		}

		// sorted trees: re-expand nodes that were closed during the update
		for (Iterator stream = expandedPaths.iterator(); stream.hasNext(); ) {
			targetTree.expandPath((TreePath) stream.next());
		}

		// unsorted trees: force the hidden root node to expand, so its children are visible
		if (expandTargetRootNode) {
			targetTree.expandPath(new TreePath(target));
		}

		// now, select the classes that were just moved
		TreePath[] newSelectedPaths = new TreePath[selectedClassNodes.size()];
		Iterator stream = selectedClassNodes.iterator();
		for (int i = 0; i < newSelectedPaths.length; i++) {
			ClassDescriptionNode classNode = (ClassDescriptionNode) stream.next();
			newSelectedPaths[i] = new TreePath(classNode.path());
		}
		targetSelectionModel.setSelectionPaths(newSelectedPaths);
	}
	
	void selectClassNodes() {
		this.moveClassNodes(this.getAvailableRootNode(), this.getSelectedRootNode(), this.availableTreeSelectionModel, this.selectedTreeSelectionModel, this.selectedTree);
	}
	
	void deselectClassNodes() {
		this.moveClassNodes(this.getSelectedRootNode(), this.getAvailableRootNode(), this.selectedTreeSelectionModel, this.availableTreeSelectionModel, this.availableTree);
	}
	
	void refresh() {
		this.repository.refreshClassDescriptions();
		this.availableRootNode = this.buildAvailableRootNode(Collections.EMPTY_LIST);
		this.availableTreeModel = new TreeModelAdapter(this.availableRootNode);
		this.availableTree.setModel(this.availableTreeModel);

		this.selectedRootNode = this.buildSelectedRootNode(Collections.EMPTY_LIST);
		this.selectedTreeModel = new TreeModelAdapter(this.selectedRootNode);
		this.selectedTree.setModel(this.selectedTreeModel);
	}
	

	// ********** trees **********

	private JTree buildTree(TreeModel treeModel, TreeSelectionModel treeSelectionModel) {
		JTree tree = SwingComponentFactory.buildTree(treeModel);
		tree.setSelectionModel(treeSelectionModel);
		tree.setCellRenderer(new DisplayableTreeCellRenderer());
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setRowHeight(20);
		tree.setDoubleBuffered(true);
		return tree;
	}
	
	private JTree buildAvailableTree() {
		JTree tree = this.buildTree(this.availableTreeModel, this.availableTreeSelectionModel);
		tree.addMouseListener(this.buildDoubleClickSelect());
		return tree;
	}

	/**
	 * double-click short-cut for selecting nodes
	 */
	private MouseListener buildDoubleClickSelect() {
		return new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					// check whether anything is actually selected;
					// nothing will be selected if the user double-clicks on a blank part of the tree
					if (MultipleClassChooserDialog.this.availableTreeSelectionModel.getSelectionPaths() != null) {
						MultipleClassChooserDialog.this.selectClassNodes();
					}
				}
			}
		};
	}

	private JTree buildSelectedTree() {
		JTree tree = this.buildTree(this.selectedTreeModel, this.selectedTreeSelectionModel);
		tree.addMouseListener(this.buildDoubleClickDeselect());
		return tree;
	}

	/**
	 * double-click short-cut for deselecting nodes
	 */
	private MouseListener buildDoubleClickDeselect() {
		return new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					// check whether anything is actually selected;
					// nothing will be selected if the user double-clicks on a blank part of the tree
					if (MultipleClassChooserDialog.this.selectedTreeSelectionModel.getSelectionPaths() != null) {
						MultipleClassChooserDialog.this.deselectClassNodes();
					}
				}
			}
		};
	}

	// ********** layout **********
	
	protected Component buildMainPanel() {
		JPanel mainPanel = new JPanel(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
	
		// available packages and classes label...
		JLabel availableLabel = new JLabel(resourceRepository().getString("availablePackages/classes"));
		availableLabel.setDisplayedMnemonic(resourceRepository().getMnemonic("availablePackages/classes"));
	
		constraints.gridx = 			0;
		constraints.gridy = 			0;
		constraints.gridwidth = 	1;
		constraints.gridheight = 	1;
		constraints.weightx = 		0;
		constraints.weighty = 		0;
		constraints.anchor = 		GridBagConstraints.LINE_START;
		constraints.fill = 			GridBagConstraints.NONE;
		constraints.insets = 		new Insets(5, 5, 0, 0);
	
		mainPanel.add(availableLabel, constraints);
	
		// ...and tree
		this.availableTree = this.buildAvailableTree();
		helpManager().addTopicID(this.availableTree, helpTopicId() + ".available");
		JScrollPane availablePane = new JScrollPane(this.availableTree);
		availablePane.setPreferredSize(new Dimension(200, 100));
		availablePane.setMinimumSize(new Dimension(200, 100));
	
		constraints.gridx = 			0;
		constraints.gridy = 			1;
		constraints.gridwidth = 	1;
		constraints.gridheight = 	3;	// make it 3, so we can swap in the test button
		constraints.weightx = 		1;
		constraints.weighty = 		1;
		constraints.fill = 			GridBagConstraints.BOTH;
		constraints.anchor = 		GridBagConstraints.CENTER;
		constraints.insets = 		new Insets(1, 5, 0, 0);
	
		mainPanel.add(availablePane, constraints);
		availableLabel.setLabelFor(this.availableTree);
	
		// selected packages and classes label...
		JLabel selectedLabel = 	new JLabel(resourceRepository().getString("selectedClasses"));
		selectedLabel.setDisplayedMnemonic(resourceRepository().getMnemonic("selectedClasses"));
	
		constraints.gridx = 			2;
		constraints.gridy = 			0;
		constraints.gridwidth = 	1;
		constraints.gridheight = 	1;
		constraints.weightx = 		0;
		constraints.weighty = 		0;
		constraints.fill = 			GridBagConstraints.NONE;
		constraints.anchor = 		GridBagConstraints.LINE_START;
		constraints.insets = 		new Insets(5, 5, 0, 5);
	
		mainPanel.add(selectedLabel, constraints);
	
		// ...and tree
		this.selectedTree = this.buildSelectedTree();
		helpManager().addTopicID(this.selectedTree, helpTopicId() + ".selected");
		JScrollPane selectedPane = new JScrollPane(this.selectedTree);
		selectedPane.setPreferredSize(new Dimension(200, 100));
		selectedPane.setMinimumSize(new Dimension(200, 100));
	
		constraints.gridx = 			2;
		constraints.gridy = 			1;
		constraints.gridwidth = 	1;
		constraints.gridheight = 	3;	// make it 3, so we can swap in the test button
		constraints.weightx = 		1;
		constraints.weighty = 		1;
		constraints.fill = 			GridBagConstraints.BOTH;
		constraints.anchor = 		GridBagConstraints.CENTER;
		constraints.insets = 		new Insets(1, 5, 0, 5);
	
		mainPanel.add(selectedPane, constraints);
		selectedLabel.setLabelFor(this.selectedTree);
	

		// select button
		JButton selectButton = new JButton(this.selectAction);
		selectButton.setToolTipText(resourceRepository().getString("addSelectedClassesToList"));
	
		constraints.gridx			= 1;
		constraints.gridy			= 1;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(1, 5, 0, 0);
	
		mainPanel.add(selectButton, constraints);
	
		// deselect button
		JButton deselectButton = new JButton(this.deselectAction);
		deselectButton.setToolTipText(resourceRepository().getString("removeSelectedClassesFromList"));
	
		constraints.gridx			= 1;
		constraints.gridy			= 2;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill			= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.PAGE_START;
		constraints.insets		= new Insets(5, 5, 0, 0);
	
		mainPanel.add(deselectButton, constraints);
	
		// test button
//		Action testAction = new AbstractAction() {
//			public void actionPerformed(ActionEvent event) {
//				// mess around with the model to see if the tree updates properly
//				ClassDescriptionPackageNode selectedRootNode = MultipleClassChooserDialog.this.getSelectedRootNode();
//				Collection expandedPaths = MultipleClassChooserDialog.this.expandedPaths(selectedTree, selectedRootNode);
//				Iterator selectedClassNodes = MultipleClassChooserDialog.this.getSelectedRootNode().classNodes();
//				if (selectedClassNodes.hasNext()) {
//					ClassNode classNode = (ClassNode) selectedClassNodes.next();
//					classNode.setUserMetaClass(classNode.packageName() + '.' + "AAAAA");
//				}
//				Iterator selectedPackageNodes = MultipleClassChooserDialog.this.getSelectedRootNode().packageNodes();
//				if (selectedPackageNodes.hasNext()) {
//					PackageNode packageNode = (PackageNode) selectedPackageNodes.next();
//					packageNode.setName("XXXXX");
//				}
//				for (Iterator stream = expandedPaths.iterator(); stream.hasNext(); ) {
//					selectedTree.expandPath((TreePath) stream.next());
//				}
//			}
//		};
//		JButton testButton = new JButton(testAction);
//		testButton.setText("test");
//		testButton.setMinimumSize(buttonSize);
//		testButton.setMaximumSize(buttonSize);
//		testButton.setPreferredSize(buttonSize);
//
//		constraints.gridx			= 1;
//		constraints.gridy			= 3;
//		constraints.gridwidth	= 1;
//		constraints.gridheight	= 1;
//		constraints.weightx		= 0;
//		constraints.weighty		= 1;
//		constraints.fill			= GridBagConstraints.NONE;
//		constraints.anchor		= GridBagConstraints.PAGE_START;
//		constraints.insets		= new Insets(5, 5, 0, 0);
//	
//		this.getMainPanel().add(testButton, constraints);

		return mainPanel;
	}
	
	protected Iterator buildCustomActions() {	
		return new SingleElementIterator(this.buildRefreshAction());
	}


	// ******************** static helper method ********************

	public static void gc() {
		ClassChooserDialog.gc();
	}

}
