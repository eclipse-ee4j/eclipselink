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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.descriptor;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Stack;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClassRepository;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleTreeCellRenderer;


final class HierarchyClassSelector extends AbstractDialog {

	private MWClass leafClass;
	private MWClass selectedClass;
	private JTree hierarchyTree;
	
	HierarchyClassSelector(MWClass leafClass, WorkbenchContext context) throws ClassNotFoundException {
		super(context);
		this.leafClass = leafClass;
	}
	
	MWClass getSelectedClass() {
		return selectedClass;
	}
	
	protected String helpTopicId() {
		return "descriptor.hierarchyClassSelector";
	}

	protected void initialize() {
		super.initialize();
		getOKAction().setEnabled(false);
	}

	protected Component buildMainPanel() {
		setTitle(resourceRepository().getString("HIERARCHY_CLASS_SELECTOR_DIALOG.title"));
		
		GridBagConstraints constraints = new GridBagConstraints();
		JPanel panel = new JPanel(new GridBagLayout());

		//Add "Class Hierarchy" label and tree.
		JLabel hierarchyLabel = new JLabel(resourceRepository().getString("HIERARCHY_CLASS_SELECTOR_DIALOG_LABEL"));
		hierarchyLabel.setDisplayedMnemonic(resourceRepository().getMnemonic("HIERARCHY_CLASS_SELECTOR_DIALOG_LABEL"));
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 0;
		constraints.weighty = 0;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(5, 5, 0, 0);
		panel.add(hierarchyLabel, constraints);
		
		initializeHierarchyTree();
		JScrollPane hierarchyPane = new JScrollPane(this.hierarchyTree);
		hierarchyPane.setPreferredSize(new Dimension(200, 200));
		hierarchyLabel.setLabelFor(this.hierarchyTree);
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.weightx = 1;
		constraints.weighty = 1;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(0, 0, 0, 0);
		panel.add(hierarchyPane, constraints);
		
		return panel;
	}
	
	private void initializeHierarchyTree() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		root.setAllowsChildren(true);
		hierarchyTree = SwingComponentFactory.buildTree(new DefaultTreeModel(root));
		hierarchyTree.setCellRenderer(buildMWClassCellRenderer());
		hierarchyTree.setRootVisible(false);
		hierarchyTree.setShowsRootHandles(true);
		hierarchyTree.setRowHeight(20);
		hierarchyTree.setDoubleBuffered(true);
		hierarchyTree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				TreePath path = ((JTree) event.getSource()).getLeadSelectionPath();
				if (path != null) {
					hierarchyTree.setSelectionPaths(new TreePath[] { path });
					MWClass bldrClass = (MWClass) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
					setSelectedClass(bldrClass);
				} else
					setSelectedClass(null);
			}
		});
		Stack classStack = new Stack();
		MWClass currentClass = this.leafClass;
		MWClassRepository repos = currentClass.getRepository();
		while (currentClass != repos.typeFor(java.lang.Object.class)) {
			classStack.push(currentClass);
			currentClass = currentClass.getSuperclass();
		}
		DefaultMutableTreeNode nextParentNode = null;
		while (!classStack.empty()) {
			MWClass nextClass = (MWClass) classStack.pop();
			DefaultMutableTreeNode nextClassNode = new DefaultMutableTreeNode(nextClass);
			if (root.getChildCount() == 0)
				root.add(nextClassNode);
			else
				nextParentNode.add(nextClassNode);
			nextParentNode = nextClassNode;
		}
		((DefaultTreeModel) this.hierarchyTree.getModel()).reload();
		this.hierarchyTree.expandPath(new TreePath(nextParentNode.getPath()));
	}

	protected Component initialFocusComponent() {
		return this.hierarchyTree;
	}

	private TreeCellRenderer buildMWClassCellRenderer() {
		return new SimpleTreeCellRenderer() {
			protected String buildText(Object value) {
				MWClass mwClass = (MWClass) ((DefaultMutableTreeNode) value).getUserObject();
				return mwClass == null ? "" : mwClass.getName();
			}
			protected Icon buildIcon(Object value) {
				return null;
			}
		};
	}
	
	protected void setSelectedClass(MWClass newSelectedClass) {
		selectedClass = newSelectedClass;
		getOKAction().setEnabled(newSelectedClass != null);
	}
}
