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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.schema;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeSelectionModel;

import org.eclipse.persistence.tools.workbench.framework.app.ApplicationNode;
import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPropertiesPage;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingTools;
import org.eclipse.persistence.tools.workbench.uitools.app.FilteringPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TreeModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.DisplayableTreeCellRenderer;


class SchemaStructurePanel 
	extends AbstractPropertiesPage
{
	private PropertyValueModel selectedSchemaComponentHolder;
	
	private JTree tree;
	
	SchemaStructurePanel(PropertyValueModel schemaNodeHolder, WorkbenchContextHolder contextHolder) {
		super(schemaNodeHolder, contextHolder);
	}
	
	protected void initialize(PropertyValueModel nodeHolder) {
		super.initialize(nodeHolder);
		this.selectedSchemaComponentHolder = this.buildSelectedSchemaComponentHolder();
		nodeHolder.addPropertyChangeListener(buildNodeListener());
	}
	
	private PropertyValueModel buildSelectedSchemaComponentHolder() {
		return new FilteringPropertyValueModel(new SimplePropertyValueModel()) {
			protected boolean accept(Object value) {
				return value instanceof SchemaComponentNode;
			}
		};
	}
	
	protected void initializeLayout() {
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.add(this.buildSplitPane(), BorderLayout.CENTER);
		addHelpTopicId(this, "schema.structure");
	}
	
	private JSplitPane buildSplitPane() {
		JSplitPane splitPane = new JSplitPane();
		splitPane.setDoubleBuffered(true);
		splitPane.setBorder(BorderFactory.createEmptyBorder());
		splitPane.setResizeWeight(1);
		splitPane.setDividerLocation(350);
		SwingTools.setSplitPaneDividerBorder(splitPane, BorderFactory.createEmptyBorder());
		splitPane.setDividerSize(3);
		splitPane.setContinuousLayout(false);
		
		splitPane.setTopComponent(this.buildTreePanel());

		JPanel panel = this.buildSchemaComponentPropertiesPanel();
		splitPane.setBottomComponent(new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		
		return splitPane;
	}
	
	protected JPanel buildTreePanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(this.buildTreePane(), BorderLayout.CENTER);
		return panel;
	}
	
	private JScrollPane buildTreePane() {
		return new JScrollPane(this.buildTree());
	}
	
	private JTree buildTree() {
		tree = SwingComponentFactory.buildTree(this.buildSchemaTreeModel());
		tree.setSelectionModel(this.buildTreeSelectionModel());
		
		// Use a default tree cell renderer, but set the icon to null.
		tree.setCellRenderer(this.buildTreeCellRenderer());
		
		// Show the root (schema node) and its handle.
		tree.setRootVisible(true);
		tree.setShowsRootHandles(true);
		
		tree.setRowHeight(20);
		tree.setDoubleBuffered(true);
		return tree;
	}
	
	private TreeModelAdapter buildSchemaTreeModel() {
		return new TreeModelAdapter(this.buildSchemaTreeRoot());
	}
	
	private SchemaNode buildSchemaTreeRoot() {
		return new SchemaNode(this.getSelectionHolder());
	}
	
	private TreeSelectionModel buildTreeSelectionModel() {
		DefaultTreeSelectionModel treeSelectionModel = new DefaultTreeSelectionModel();
		treeSelectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		treeSelectionModel.addTreeSelectionListener(this.buildTreeSelectionListener(treeSelectionModel));
		return treeSelectionModel;
	}
	
	private TreeSelectionListener buildTreeSelectionListener(final TreeSelectionModel treeSelectionModel) {
		return new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				Object selectedComponent = (treeSelectionModel.getSelectionCount() == 1) ? 
											treeSelectionModel.getSelectionPath().getLastPathComponent() :
											null;
				SchemaStructurePanel.this.selectedSchemaComponentHolder.setValue(selectedComponent);
			}
		};
	}
	
	private DefaultTreeCellRenderer buildTreeCellRenderer() {
		return new DisplayableTreeCellRenderer();
	}
	
	private JPanel buildSchemaComponentPropertiesPanel() {
		return new SchemaComponentDetailsPanel(this.getApplicationContext(), this.selectedSchemaComponentHolder);
	}
	
	/**
	 * This is used to overcome a limitation in JTree, the root of the tree can never be null.
	 * @see org.eclipse.persistence.tools.workbench.uitools.app.swing for more information.
	 * 
	 * TODO This should probably go away once we stop caching the Schema properties page.
	 */
	private PropertyChangeListener buildNodeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				resetNode((ApplicationNode) evt.getNewValue());
			}
		};
	}
	
	private void resetNode(ApplicationNode node) {
		if (node == null) {
			tree.setModel(null);
		}
		else {
			tree.setModel(buildSchemaTreeModel());
		}	
	}

}

