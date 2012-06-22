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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
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
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWModelGroupDefinition;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWNamedSchemaComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWNamespace;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaContextComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;
import org.eclipse.persistence.tools.workbench.uitools.Displayable;
import org.eclipse.persistence.tools.workbench.uitools.app.AbstractTreeNodeValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimpleListValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SortedListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationListValueModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TreeNodeValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.TreeModelAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.DisplayableTreeCellRenderer;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;


/**
 * This dialog presents a tree of the available contexts
 * within the provided schema repository.
 * When the user selects a tree node, the text preview 
 * of the selected context appears in the text field.
 * <p>
 * Once the user presses the OK button, clients can 
 * retrieve the selected schema context by calling
 * #selectedSchemaContext().  This will not be null.
 * <p>
 * Here is the layout of the dialog:
 * <pre>
 * ________________________________________
 * |                                      |
 * | Title                                |
 * |______________________________________|
 * | ____________________________________ |
 * | | schema                         |^| |
 * | |  |-context1                    | | |
 * | |  |-context2                    | | |
 * | |  |  |-subcontext1              | | |
 * | |  |-context3                    | | |
 * | |                                | | |
 * | |                                | | |
 * | |                                | | |
 * | |                                |v| |
 * | ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ |
 * | ____________________________________ |
 * | |(Schema context preview)          | |
 * | ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ |
 * |______________________________________|
 * |                    ________ ________ |
 * |                    |  OK  | |Cancel| |
 * |                    ¯¯¯¯¯¯¯¯ ¯¯¯¯¯¯¯¯ |
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯</pre>
 *
 */
final class SchemaContextChooserDialog 
	extends AbstractDialog 
{
	/** Schema repository passed to this dialog */
	private SchemaRepositoryValue schemaRepositoryValue;
	
	/** PropertyValueModel passed to this dialog within which the schema component is set */
	private PropertyValueModel schemaComponentHolder;
	
	/** PropertyValueModel used internally to keep track of selected component */
	private PropertyValueModel selectedSchemaComponentHolder;
	
	/** The tree selection model */
	private TreeSelectionModel schemaContextTreeSelectionModel;
	
	
	//***************** Constructors ******************************************
	
	SchemaContextChooserDialog(WorkbenchContext context, SchemaRepositoryValue schemaRepository, PropertyValueModel schemaComponentHolder) {
		super(context);
		this.initialize(schemaRepository, schemaComponentHolder);
	}
	
	
	//***************** Initialization ****************************************
	
	protected void initialize() {
		super.initialize();
		
		// initialize dialog settings
		this.setTitle(this.resourceRepository().getString("SCHEMA_CONTEXT_CHOOSER_DIALOG.TITLE"));
	}
	
	private void initialize(SchemaRepositoryValue schemaRepository, PropertyValueModel schemaComponentHolder) {
		this.schemaRepositoryValue = schemaRepository;
		this.schemaComponentHolder = schemaComponentHolder;
		this.selectedSchemaComponentHolder = this.buildSelectedSchemaComponentHolder();
		this.schemaContextTreeSelectionModel = this.buildSchemaContextTreeSelectionModel();
	}
	
	private PropertyValueModel buildSelectedSchemaComponentHolder() {
		return new SimplePropertyValueModel(null);
	}
	
	private TreeSelectionModel buildSchemaContextTreeSelectionModel() {
		TreeSelectionModel treeSelectionModel = new DefaultTreeSelectionModel();
		treeSelectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		treeSelectionModel.addTreeSelectionListener(this.buildTreeSelectionListener());
		return treeSelectionModel;
	}
	
	private TreeSelectionListener buildTreeSelectionListener() {
		return new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent tse) {
				SchemaContextChooserDialog.this.schemaContextTreeSelectionChanged();
			}
		};
	}
	
	protected Component buildMainPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panel.setPreferredSize(new Dimension(400, 400));
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		// build the tree
		constraints.gridx 		= 0;
		constraints.gridy 		= 0;
		constraints.gridwidth 	= 2;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 1;
		constraints.anchor 		= GridBagConstraints.PAGE_START;
		constraints.fill 		= GridBagConstraints.BOTH;
		constraints.insets 		= new Insets(0, 0, 0, 0);
		panel.add(this.buildSchemaContextTreePane(), constraints);
		
		// build the preview text field label
		JLabel schemaContextPreviewLabel = this.buildSchemaContextPreviewLabel();
		constraints.gridx 		= 0;
		constraints.gridy 		= 1;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 0;
		constraints.weighty 	= 0;
		constraints.anchor 		= GridBagConstraints.WEST;
		constraints.fill 		= GridBagConstraints.NONE;
		constraints.insets 		= new Insets(5, 0, 0, 5);
		panel.add(schemaContextPreviewLabel, constraints);
		
		// build the preview text field
		JTextField schemaContextPreviewTextField = this.buildSchemaContextPreviewTextField();
		schemaContextPreviewLabel.setLabelFor(schemaContextPreviewTextField);
		constraints.gridx 		= 1;
		constraints.gridy 		= 1;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 0;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.insets 		= new Insets(5, 0, 0, 0);
		panel.add(schemaContextPreviewTextField, constraints);
		
		return panel;
	}
	
	private JScrollPane buildSchemaContextTreePane() {
		return new JScrollPane(this.buildSchemaContextTree());
	}
	
	private JTree buildSchemaContextTree() {
		JTree tree = SwingComponentFactory.buildTree(this.buildSchemaContextTreeModel());
		tree.setSelectionModel(this.schemaContextTreeSelectionModel);
		
		tree.setCellRenderer(this.buildTreeCellRenderer());
		
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		
		tree.setRowHeight(20);
		tree.setDoubleBuffered(true);
		
		tree.expandRow(0);
		
		return tree;
	}
	
	private TreeModelAdapter buildSchemaContextTreeModel() {
		return new TreeModelAdapter(this.buildSchemaContextTreeRoot());
	}
	
	private SchemaRepositoryNode buildSchemaContextTreeRoot() {
		return new SchemaRepositoryNode(this.schemaRepositoryValue);
	}
	
	private TreeCellRenderer buildTreeCellRenderer() {
		return new DisplayableTreeCellRenderer();
	}
	
	private JLabel buildSchemaContextPreviewLabel() {
		JLabel schemaContextPreviewLabel = new JLabel(this.resourceRepository().getString("SCHEMA_CONTEXT_CHOOSER_DIALOG.PREVIEW_TEXT_FIELD"));
		schemaContextPreviewLabel.setDisplayedMnemonic(this.resourceRepository().getMnemonic("SCHEMA_CONTEXT_CHOOSER_DIALOG.PREVIEW_TEXT_FIELD"));
		return schemaContextPreviewLabel;
	}
	
	private JTextField buildSchemaContextPreviewTextField() {
		JTextField textField = new JTextField();
		textField.setEditable(false);
		textField.setEnabled(true);
		textField.setDocument(this.buildSchemaContextPreviewDocument());
		return textField;
	}
	
	private DocumentAdapter buildSchemaContextPreviewDocument() {
		return new DocumentAdapter(this.buildSelectedSchemaContextStringHolder());
	}
	
	private PropertyValueModel buildSelectedSchemaContextStringHolder() {
		return new TransformationPropertyValueModel(this.selectedSchemaComponentHolder) {
			protected Object transform(Object value) {
				return SchemaContextComponentDisplayer.displayString(
					SchemaContextChooserDialog.this.resourceRepository(),
					(MWSchemaContextComponent) value
				);
			}
		};
	}
	
	
	// **************** Internal **********************************************
	
	private void schemaContextTreeSelectionChanged() {
		TreePath selectedPath = this.schemaContextTreeSelectionModel.getSelectionPath();
		
		Object newComponent = null;
		
		if (selectedPath != null && selectedPath.getLastPathComponent() instanceof SchemaContextComponentNode) {
			newComponent = ((SchemaContextComponentNode) this.schemaContextTreeSelectionModel.getSelectionPath().getLastPathComponent()).getValue();
		}
		
		this.selectedSchemaComponentHolder.setValue(newComponent);
	}
	
	public MWNamedSchemaComponent selectedSchemaComponent() {
		return (MWNamedSchemaComponent) this.selectedSchemaComponentHolder.getValue();
	}
	
	
	// **************** AbstractDialog contract *******************************
	
	protected void okConfirmed() {
		this.schemaComponentHolder.setValue(this.selectedSchemaComponentHolder.getValue());
		super.okConfirmed();
	}
	
	protected String helpTopicId() {
		return "dialog.schemaContextChooser";
	}
	
	
	// **************** Behavior **********************************************
	
	public void show() {
		if (! this.schemaRepositoryValue.schemas().hasNext()) {
			this.showErrorDialog();
		}
		else {
			super.show();
		}
	}
	
	private void showErrorDialog() {
		Component currentWindow = this.currentWindow();
		ResourceRepository repo = this.resourceRepository();
		String errorTitle = repo.getString("SCHEMA_CONTEXT_CHOOSER_DIALOG.NO_SCHEMAS_LOADED_TITLE");
		String errorMessage = repo.getString("SCHEMA_CONTEXT_CHOOSER_DIALOG.NO_SCHEMAS_LOADED_MESSAGE");
		JOptionPane.showMessageDialog(currentWindow, errorMessage, errorTitle, JOptionPane.WARNING_MESSAGE);
	}
	
	
	// **************** Member classes ****************************************
	
	private final static class SchemaRepositoryNode
		extends AbstractTreeNodeValueModel
		implements Displayable 
	{
		// **************** Instance variables ************************************
		
		/** The schema repository for this node. */
		private SchemaRepositoryValue schemaRepository;
		
		/** The children of this node. */
		private ListValueModel childrenModel;
		
		
		// **************** Constructors ******************************************
		
		public SchemaRepositoryNode(SchemaRepositoryValue schemaRepository) {
			super();
			this.initialize(schemaRepository);
		}
		
		
		// **************** Initialization ****************************************
		
		private void initialize(SchemaRepositoryValue schemaRepository) {
			this.schemaRepository = schemaRepository;
			this.childrenModel = this.buildChildrenModel();
		}
		
		protected ListValueModel buildChildrenModel() {
			return new SortedListValueModelAdapter(this.buildSchemaNodesAdapter());
		}
		
		protected ListValueModel buildSchemaNodesAdapter() {
			return new TransformationListValueModelAdapter(this.buildSchemasAdapter()) {
				protected Object transformItem(Object item) {
					return SchemaRepositoryNode.this.buildSchemaNode((MWXmlSchema) item);
				}
			};
		}
		
		protected ListValueModel buildSchemasAdapter() {
			return new SimpleListValueModel(CollectionTools.list(this.schemaRepository.schemas()));
		}
		
		protected SchemaNode buildSchemaNode(MWXmlSchema schema) {
			return new SchemaNode(this, schema);
		}
		
		
		// **************** ValueModel contract ***********************************
		
		public Object getValue() {
			return this.schemaRepository;
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
	
	
	private final static class SchemaNode
		extends AbstractTreeNodeValueModel
		implements Displayable
	{
		// **************** Instance Variables ************************************
		
		/** The parent of this node */
		private SchemaRepositoryNode parent;
		
		/** The schema for this node. */
		private MWXmlSchema schema;
		
		/** The children of this node. */
		private ListValueModel childrenModel;
		
		
		// **************** Constructors ******************************************
		
		SchemaNode(SchemaRepositoryNode parent, MWXmlSchema schema) {
			super();
			this.initialize(parent, schema);
		}
		
		
		// **************** Initialization ****************************************
		
		private void initialize(SchemaRepositoryNode parent, MWXmlSchema schema) {
			this.parent = parent;
			this.schema = schema;
			this.childrenModel = this.buildChildrenModel();
		}
		
		private ListValueModel buildChildrenModel() {
			return new SortedListValueModelAdapter(this.buildContextComponentNodesAdapter());
		}
		
		private ListValueModel buildContextComponentNodesAdapter() {
			return new TransformationListValueModelAdapter(this.buildContextComponentsAdapter()) {
				protected Object transformItem(Object item) {
					return SchemaNode.this.buildContextComponentNode((MWNamedSchemaComponent) item);
				}
			};
		}
		
		private ListValueModel buildContextComponentsAdapter() {
			return new SimpleListValueModel(CollectionTools.list(this.schema.contextComponents()));
		}
		
		private SchemaContextComponentNode buildContextComponentNode(MWNamedSchemaComponent component) {
			return new SchemaContextComponentNode(this, component);
		}
		
		
		// **************** ValueModel contract ***********************************
		
		public Object getValue() {
			return this.schema;
		}
		
		
		// **************** TreeNodeValueModel contract ***************************
		
		public TreeNodeValueModel getParent() {
			return this.parent;
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
			return "schema::" + this.schema.getName();
		}
		
		public Icon icon() {
			return null;
		}
	}
	
	
	private final static class SchemaContextComponentNode
		extends AbstractTreeNodeValueModel
		implements Displayable 
	{
		// **************** Instance Variables ************************************
		
		/** The parent of this node */
		private AbstractTreeNodeValueModel parent;
		
		/** The component for this node. */
		private MWNamedSchemaComponent component;
		
		/** The children of this node. */
		private ListValueModel childrenModel;
		
		
		// **************** Constructors ******************************************
		
		SchemaContextComponentNode(AbstractTreeNodeValueModel parent, MWNamedSchemaComponent component) {
			super();
			this.initialize(parent, component);
		}
		
		
		// **************** Initialization ****************************************
		
		private void initialize(AbstractTreeNodeValueModel parent, MWNamedSchemaComponent component) {
			this.parent = parent;
			this.component = component;
			this.childrenModel = this.buildChildrenModel();
		}
		
		protected ListValueModel buildChildrenModel() {
			return new TransformationListValueModelAdapter(this.buildContextComponentsAdapter()) {
				protected Object transformItem(Object item) {
					return SchemaContextComponentNode.this.buildContextComponentNode((MWNamedSchemaComponent) item);
				}
			};
		}
		
		protected ListValueModel buildContextComponentsAdapter() {
			return new SimpleListValueModel(CollectionTools.list(this.component.descriptorContextComponents()));
		}
		
		protected SchemaContextComponentNode buildContextComponentNode(MWNamedSchemaComponent component) {
			return new SchemaContextComponentNode(this, component);
		}
		
		
		// **************** ValueModel contract ***********************************
		
		public Object getValue() {
			return this.component;
		}
		
		
		// **************** TreeNodeValueModel contract ***************************
		
		public TreeNodeValueModel getParent() {
			return this.parent;
		}
		
		public ListValueModel getChildrenModel() {
			return this.childrenModel;
		}
		
		
		// ********** AbstractTreeNodeValueModel implementation **********
		
		protected void engageValue() {}
		
		protected void disengageValue() {}
		
		
		// **************** Comparable contract ***********************************
		
		public int compareTo(Object o) {
			return SchemaContextComponentNodeComparator.compare(this, o);
		}
		
		
		// **************** Displayable contract **********************************
		
		public String displayString() {
			return this.component.componentTypeName() + "::" + this.component.qName();
		}
		
		public Icon icon() {
			return null;
		}
		
		
		// **************** Member classes ****************************************
		
		private static class SchemaContextComponentNodeComparator
		{
			/** 
			 * Sort nodes first by namespace (root namespace first, then alphabetically by prefix),
			 * then by node type
			 * then by prefixed name.
			 */
			public static int compare(Object o1, Object o2) {
				SchemaContextComponentNode node1 = (SchemaContextComponentNode) o1;
				SchemaContextComponentNode node2 = (SchemaContextComponentNode) o2;
				MWNamedSchemaComponent comp1 = node1.component;
				MWNamedSchemaComponent comp2 = node2.component;
				
				int comparison = compare(comp1.getTargetNamespace(), comp2.getTargetNamespace());
		
				if (comparison == 0) {
					comparison = compareComponentType(comp1, comp2);
				}
				
				if (comparison == 0) {
					comparison = comp1.qName().compareTo(comp2.qName());
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
			
			private static int compareComponentType(MWNamedSchemaComponent comp1, MWNamedSchemaComponent comp2) {
				int rank1, rank2 = 0;
				
				if (comp1 instanceof MWElementDeclaration) {
					rank1 = 1;
				}
				else if (comp1 instanceof MWModelGroupDefinition) {
					rank1 = 2;
				}
				else if (comp1 instanceof MWComplexTypeDefinition) {
					rank1 = 3;
				}
				else {
					rank1 = 4;
				}
				
				
				if (comp2 instanceof MWElementDeclaration) {
					rank2 = 1;
				}
				else if (comp2 instanceof MWModelGroupDefinition) {
					rank2 = 2;
				}
				else if (comp2 instanceof MWComplexTypeDefinition) {
					rank2 = 3;
				}
				else {
					rank2 = 4;
				}
				
				return rank1 - rank2;
			}
		}
	}
}
