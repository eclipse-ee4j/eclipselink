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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWComplexTypeDefinition;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWElementDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWNamespace;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;
import org.eclipse.persistence.tools.workbench.uitools.Displayable;
import org.eclipse.persistence.tools.workbench.uitools.app.AbstractTreeNodeValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.NullListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TreeModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.DisplayableTreeCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


/**
 * This dialog presents a tree of the available complex types
 *  within the provided schema.
 * This dialog is pretty much only a tree to make it similar to
 *  other schema component choosers.
 * If the dialog is confirmed, the value of the passed in element
 *  holder will be set with the value of the selected element.
 * <p>
 * Here is the layout of the dialog:
 * <pre>
 * ________________________________________
 * |                                      |
 * | Title                                |
 * |______________________________________|
 * | ____________________________________ |
 * | | complextType1                  |^| |
 * | | complextType2                  | | |
 * | | complextType3                  | | |
 * | |                                | | |
 * | |                                | | |
 * | |                                | | |
 * | |                                |v| |
 * | ------------------------------------ |
 * |______________________________________|
 * |                    ________ ________ |
 * |                    |  OK  | |Cancel| |
 * |                    -------- -------- |
 * ----------------------------------------</pre>
 *
 */

final class SchemaComplexTypeChooserDialog extends AbstractDialog 
{
	/** Schema passed to this dialog */
	private MWXmlSchema schema;
	
	/** PropertyValueModel passed to this dialog within which the complext type is set */
	private PropertyValueModel complexTypeHolder;
	
	/** PropertyValueModel used internally to keep track of selected complex type */
	private PropertyValueModel selectedComplexTypeHolder;
	
	/** The tree selection model */
	private TreeSelectionModel treeSelectionModel;
	
	
	//***************** Constructors ******************************************
	
	SchemaComplexTypeChooserDialog(WorkbenchContext context, MWXmlSchema schema, PropertyValueModel complexTypeHolder) {
		super(context);
		this.initialize(schema, complexTypeHolder);
	}
	
	
	//***************** Initialization ****************************************
	
	protected void initialize() {
		super.initialize();
		
		// initialize dialog settings
		this.setTitle(this.resourceRepository().getString("SCHEMA_COMPLEX_TYPE_CHOOSER_DIALOG.TITLE"));
		this.getOKAction().setEnabled(false);
	}
	
	private void initialize(MWXmlSchema schema, PropertyValueModel complexTypeHolder) {
		this.schema = schema;
		this.complexTypeHolder = complexTypeHolder;
		this.selectedComplexTypeHolder = this.buildSelectedComplexTypeHolder();
		this.treeSelectionModel = this.buildTreeSelectionModel();
	}
	
	private PropertyValueModel buildSelectedComplexTypeHolder() {
		PropertyValueModel selectedComplexTypeHolder = new SimplePropertyValueModel(null);
		selectedComplexTypeHolder.addPropertyChangeListener(PropertyValueModel.VALUE, this.buildSelectedComplexTypeListener());
		return selectedComplexTypeHolder;
	}
	
	private PropertyChangeListener buildSelectedComplexTypeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				SchemaComplexTypeChooserDialog.this.getOKAction().setEnabled(evt.getNewValue() != null);
			}
		};
	}
	
	private TreeSelectionModel buildTreeSelectionModel() {
		TreeSelectionModel treeSelectionModel = new DefaultTreeSelectionModel();
		treeSelectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		treeSelectionModel.addTreeSelectionListener(this.buildTreeSelectionListener());
		return treeSelectionModel;
	}
	
	private TreeSelectionListener buildTreeSelectionListener() {
		return new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent tse) {
				SchemaComplexTypeChooserDialog.this.treeSelectionChanged();
			}
		};
	}
	
	protected Component buildMainPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panel.setPreferredSize(new Dimension(350, 350));
		
		// build the tree
		panel.add(this.buildTreePane(), BorderLayout.CENTER);
		
		return panel;
	}
	
	private JScrollPane buildTreePane() {
		return new JScrollPane(this.buildTree());
	}
	
	private JTree buildTree() {
		JTree tree = SwingComponentFactory.buildTree(this.buildTreeModel());
		tree.setSelectionModel(this.treeSelectionModel);
		tree.setCellRenderer(this.buildTreeCellRenderer());
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setRowHeight(20);
		tree.setDoubleBuffered(true);
		
		tree.expandRow(0);
		
		return tree;
	}
	
	private TreeModelAdapter buildTreeModel() {
		return new TreeModelAdapter(this.buildTreeRoot());
	}
	
	private SchemaNode buildTreeRoot() {
		return new SchemaNode(this.schema);
	}
	
	private TreeCellRenderer buildTreeCellRenderer() {
		return new DisplayableTreeCellRenderer();
	}
	
	
	// **************** Internal **********************************************
	
	private void treeSelectionChanged() {
		TreePath selectedPath = this.treeSelectionModel.getSelectionPath();
		
		Object newElement = null;
		
		if (selectedPath != null) {
			newElement = ((ComplexTypeNode) this.treeSelectionModel.getSelectionPath().getLastPathComponent()).getValue();
		}
		
		this.selectedComplexTypeHolder.setValue(newElement);
	}
	
	public MWElementDeclaration selectedRootElement() {
		return (MWElementDeclaration) this.selectedComplexTypeHolder.getValue();
	}
	
	
	// **************** AbstractDialog contract *******************************
	
	protected void okConfirmed() {
		this.complexTypeHolder.setValue(this.selectedComplexTypeHolder.getValue());
		super.okConfirmed();
	}
	
	protected String helpTopicId() {
		return "dialog.schemaComplexTypeChooser";
	}
	
	
	// **************** Behavior **********************************************
	
	public void show() {
		if (this.schema == null) {
			this.showErrorDialog();
		}
		else {
			super.show();
		}
	}
	
	private void showErrorDialog() {
		Component currentWindow = this.currentWindow();
		ResourceRepository repo = this.resourceRepository();
		String errorTitle = repo.getString("SCHEMA_COMPLEX_TYPE_CHOOSER_DIALOG.NO_CONTEXT_SPECIFIED_TITLE");
		String errorMessage = repo.getString("SCHEMA_COMPLEX_TYPE_CHOOSER_DIALOG.NO_CONTEXT_SPECIFIED_MESSAGE");
		JOptionPane.showMessageDialog(currentWindow, errorMessage, errorTitle, JOptionPane.WARNING_MESSAGE);
	}
	
	
	// **************** Member classes ****************************************
	
	private final static class SchemaNode
		extends AbstractTreeNodeValueModel
		implements Displayable
	{
		// **************** Instance variables ************************************
		
		/** The schema for this node. */
		private MWXmlSchema schema;
		
		/** The children of this node. */
		private ListValueModel childrenModel;
		
		
		// **************** Constructors ******************************************
		
		public SchemaNode(MWXmlSchema schema) {
			super();
			this.initialize(schema);
		}
		
		
		// **************** Initialization ****************************************
		
		private void initialize(MWXmlSchema schema) {
			this.schema = schema;
			this.childrenModel = this.buildChildrenModel();
		}
		
		private ListValueModel buildChildrenModel() {
			return new SortedListValueModelAdapter(this.buildComplexTypeNodesAdapter());
		}
		
		private ListValueModel buildComplexTypeNodesAdapter() {
			return new TransformationListValueModelAdapter(this.buildComplexTypesAdapter()) {
				protected Object transformItem(Object item) {
					return SchemaNode.this.buildComplexTypeNode((MWComplexTypeDefinition) item);
				}
			};
		}
		
		private ListValueModel buildComplexTypesAdapter() {
			return new SimpleListValueModel(CollectionTools.list(this.schema.complexTypes()));
		}
		
		private ComplexTypeNode buildComplexTypeNode(MWComplexTypeDefinition element) {
			return new ComplexTypeNode(this, element);
		}
		
		
		// **************** ValueModel contract ***********************************
		
		public Object getValue() {
			return this.schema;
		}
		
		
		// **************** TreeNodeValueModel contract ***************************
		
		public TreeNodeValueModel getParent() {
			return null;
		}
		
		public ListValueModel getChildrenModel() {
			return this.childrenModel;
		}
		
		
		// ********** AbstractTreeNodeValueModel implementation **********
		
		protected void engageValue() {}
		
		protected void disengageValue() {}
		
		
		// **************** Comparable contract ***********************************
		
		public int compareTo(Object o) {
			return DEFAULT_COMPARATOR.compare(this, o);
		}
		
		
		// **************** Displayable contract **********************************
		
		public String displayString() {
			return null;
		}
		
		public Icon icon() {
			return null;
		}
	}
	
	
	private final static class ComplexTypeNode
		extends AbstractTreeNodeValueModel
		implements Displayable 
	{
		// **************** Instance Variables ************************************
		
		/** The parent of this node */
		private AbstractTreeNodeValueModel parent;
		
		/** The element for this node. */
		private MWComplexTypeDefinition typeDefinition;
		
		
		// **************** Constructors ******************************************
		
		ComplexTypeNode(AbstractTreeNodeValueModel parent, MWComplexTypeDefinition typeDefinition) {
			super();
			this.initialize(parent, typeDefinition);
		}
		
		
		// **************** Initialization ****************************************
		
		private void initialize(AbstractTreeNodeValueModel parent, MWComplexTypeDefinition complexType) {
			this.parent = parent;
			this.typeDefinition = complexType;
		}
		
		
		// **************** ValueModel contract ***********************************
		
		public Object getValue() {
			return this.typeDefinition;
		}
		
		
		// **************** TreeNodeValueModel contract ***************************
		
		public TreeNodeValueModel getParent() {
			return this.parent;
		}
		
		public ListValueModel getChildrenModel() {
			return NullListValueModel.instance();
		}
		
		
		// ********** AbstractTreeNodeValueModel implementation **********
		
		protected void engageValue() {}
		
		protected void disengageValue() {}
		
		
		// **************** Comparable contract ***********************************
		
		public int compareTo(Object o) {
			return ComplexTypeNodeComparator.compare(this, o);
		}
		
		
		// **************** Displayable contract **********************************
		
		public String displayString() {
			return this.typeDefinition.qName();
		}
		
		public Icon icon() {
			return null;
		}
		
		
		// **************** Member classes ****************************************
		
		private static class ComplexTypeNodeComparator
		{
			/** 
			 * Sort nodes first by namespace (root namespace first, then alphabetically by prefix),
			 * then by prefixed name.
			 */
			public static int compare(Object o1, Object o2) {
				ComplexTypeNode node1 = (ComplexTypeNode) o1;
				ComplexTypeNode node2 = (ComplexTypeNode) o2;
				MWComplexTypeDefinition type1 = node1.typeDefinition;
				MWComplexTypeDefinition type2 = node2.typeDefinition;
				
				int comparison = compare(type1.getTargetNamespace(), type2.getTargetNamespace());
				
				if (comparison == 0) {
					comparison = type1.qName().compareTo(type2.qName());
				}
				
				return comparison;
			}
			
			private static int compare(MWNamespace thisNamespace, MWNamespace otherNamespace) {
				if (thisNamespace.isTargetNamespace() && ! otherNamespace.isTargetNamespace()) {
					return -1;
				}
				else if (! thisNamespace.isTargetNamespace() && otherNamespace.isTargetNamespace()) {
					return 1;
				}
				else if(thisNamespace.getNamespacePrefix() != otherNamespace.getNamespacePrefix()) {
					return thisNamespace.getNamespacePrefix().compareToIgnoreCase(otherNamespace.getNamespacePrefix());
				}
				else {
					return 0;
				}
			}
		}
	}
}
