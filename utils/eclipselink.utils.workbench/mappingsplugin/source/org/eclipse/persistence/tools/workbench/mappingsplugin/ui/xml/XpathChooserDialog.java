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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.xml;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractSpinnerModel;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SpinnerModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContext;
import org.eclipse.persistence.tools.workbench.framework.resources.ResourceRepository;
import org.eclipse.persistence.tools.workbench.framework.ui.dialog.AbstractDialog;
import org.eclipse.persistence.tools.workbench.framework.uitools.SwingComponentFactory;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWAttributeDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWElementDeclaration;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWSchemaContextComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXpathableSchemaComponent;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXmlField;
import org.eclipse.persistence.tools.workbench.mappingsmodel.xml.MWXpathSpec;
import org.eclipse.persistence.tools.workbench.uitools.app.BufferedPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.TransformationPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.AbstractTreeModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.DocumentAdapter;
import org.eclipse.persistence.tools.workbench.uitools.cell.EditingNode;
import org.eclipse.persistence.tools.workbench.uitools.cell.NodeTreeCellEditor;
import org.eclipse.persistence.tools.workbench.uitools.cell.NodeTreeCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.NullTreeCellEditor;
import org.eclipse.persistence.tools.workbench.uitools.cell.RenderingNode;
import org.eclipse.persistence.tools.workbench.uitools.cell.SimpleTreeCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.SpinnerTreeCellRenderer;
import org.eclipse.persistence.tools.workbench.uitools.cell.TreeCellEditorAdapter;
import org.eclipse.persistence.tools.workbench.utility.AbstractModel;
import org.eclipse.persistence.tools.workbench.utility.CollectionTools;
import org.eclipse.persistence.tools.workbench.utility.events.StateChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.StateChangeListener;
import org.eclipse.persistence.tools.workbench.utility.iterators.CompositeIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.FilteringIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.NullIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.SingleElementIterator;
import org.eclipse.persistence.tools.workbench.utility.iterators.TransformationIterator;


/**
 * This dialog presents a tree of the available "XPath-able"
 * elements or attibutes within a given schema context.
 * When the user selects a tree node, the text preview 
 * of the selected XPath appears in the text field.
 * <p>
 * Once the user presses the OK button, the selected XPath
 * string is set on the provided MWXpath (through the
 * provided xpathHolder).
 * <p>
 * Here is the layout of the dialog:
 * <pre>
 * ________________________________________
 * |                                      |
 * | Title                                |
 * |______________________________________|
 * | ____________________________________ |
 * | | schema context                 |^| |
 * | |  |-attribute1                  | | |
 * | |  |-attribute2                  | | |
 * | |  |-element1                    | | |
 * | |  |  |-subattribute1            ||| |
 * | |  |  |-subelement1              | | |
 * | |  |-element2                    | | |
 * | |                                | | |
 * | |                                |v| |
 * | ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ |
 * | ____________________________________ |
 * | |(XPath preview)                 | |
 * | ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯ |
 * |______________________________________|
 * |                    ________ ________ |
 * |                    |  OK  | |Cancel| |
 * |                    ¯¯¯¯¯¯¯¯ ¯¯¯¯¯¯¯¯ |
 * ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯</pre>
 *
 */
public final class XpathChooserDialog 
	extends AbstractDialog
{
	// **************** Variables *********************************************
	
	/** Holds the xml field from which context information is retrieved */
	private ValueModel xmlFieldHolder;
	
	/** Holds the xpath initially passed to or set by the dialog */
	private PropertyValueModel xpathHolder;
	
	/** Holds the xpath until the dialog is committed */
	private BufferedPropertyValueModel selectedXpathHolder;
	
	/** The trigger to accept the selected xpath */
	private BufferedPropertyValueModel.Trigger xpathTrigger;
	
	/** The xpath tree model */
	private XpathTreeModel xpathTreeModel;
	
	/** The xpath tree selection model */
	private TreeSelectionModel xpathTreeSelectionModel;
	private Object[] selectedPath;
	private StateChangeListener positionListener;
	
	
	// **************** Static methods ****************************************
	
	/**
	 * Use this prompter if there is no convenient way to create a value holder for the 
	 * xml field
	 */
	public static void promptToSelectXpath(
		MWXmlField xmlField, WorkbenchContext context
	) {
		promptToSelectXpath(new SimplePropertyValueModel(xmlField), context);
	}
	
	/** 
	 * Use this prompter if you wish to automatically set the xpath on the xml field
	 * on dialog confirmation
	 */
	public static void promptToSelectXpath(
		ValueModel xmlFieldHolder, WorkbenchContext context
	) {
		promptToSelectXpath(xmlFieldHolder, buildDefaultXpathHolder(xmlFieldHolder), context);
	}
	
	/** 
	 * Use this prompter if you wish to allow further processing of information before
	 * the xpath is set on the xml field, by passing in some other sort of PVM (such as
	 * a BufferedPropertyValueModel) for the xpath holder.
	 */
	public static void promptToSelectXpath(
		ValueModel xmlFieldHolder, PropertyValueModel xpathHolder, WorkbenchContext context
	) {
		Window currentWindow = context.getCurrentWindow();
		XpathChooserDialog dialog;
		
		if (currentWindow instanceof Dialog) {
			dialog = new XpathChooserDialog(context, (Dialog) currentWindow, xmlFieldHolder, xpathHolder);
		}
		else {
			dialog = new XpathChooserDialog(context, xmlFieldHolder, xpathHolder);
		}
		
		dialog.show();
	}
	
	private static PropertyValueModel buildDefaultXpathHolder(ValueModel xmlFieldHolder) {
		return new PropertyAspectAdapter(xmlFieldHolder, MWXmlField.XPATH_PROPERTY) {
			protected Object getValueFromSubject() {
				return ((MWXmlField) this.subject).getXpath();
			}
			
			protected void setValueOnSubject(Object value) {
				((MWXmlField) this.subject).setXpath((String) value);
			}
		};
	}
	
	
	
	//***************** Constructors ******************************************
	
	private XpathChooserDialog(
		WorkbenchContext context, ValueModel xmlFieldHolder, PropertyValueModel xpathHolder
	) {
		super(context);
		this.initialize(xmlFieldHolder, xpathHolder);
	}
	
	private XpathChooserDialog(
		WorkbenchContext context, Dialog dialog, ValueModel xmlFieldHolder, PropertyValueModel xpathHolder
	) {
		super(context, dialog);
		this.initialize(xmlFieldHolder, xpathHolder);
	}
	
	
	//***************** Initialization ****************************************
	
	protected void initialize() {
		super.initialize();
		
		// initialize dialog settings
		this.setTitle(this.resourceRepository().getString("XPATH_CHOOSER_DIALOG.TITLE"));
		this.getOKAction().setEnabled(false);
	}
	
	private void initialize(ValueModel xmlFieldHolder, PropertyValueModel xpathHolder) {
		this.xmlFieldHolder = xmlFieldHolder;
		this.xpathHolder = xpathHolder;
		this.xpathTrigger = new BufferedPropertyValueModel.Trigger();
		this.selectedXpathHolder = this.buildSelectedXpathHolder(this.xpathHolder, this.xpathTrigger);
		this.xpathTreeModel = this.buildXpathTreeModel();
		this.xpathTreeSelectionModel = this.buildXpathTreeSelectionModel();
		this.selectedPath = new Object[0];
		this.positionListener = this.buildPositionListener();
	}
	
	private BufferedPropertyValueModel buildSelectedXpathHolder(
		PropertyValueModel xpathHolder, BufferedPropertyValueModel.Trigger trigger
	) {
		BufferedPropertyValueModel holder = new BufferedPropertyValueModel(xpathHolder, trigger);
		holder.addPropertyChangeListener(PropertyValueModel.VALUE, this.buildSelectedXpathListener());
		return holder;
	}
	
	private PropertyChangeListener buildSelectedXpathListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent pce) {
				XpathChooserDialog.this.getOKAction().setEnabled(! "".equals(pce.getNewValue()));
			}
		};
	}
	
	private XpathTreeModel buildXpathTreeModel() {
		return new XpathTreeModel(this.xmlField().schemaContext(), this.xmlField().xpathSpec());
	}
	
	private TreeSelectionModel buildXpathTreeSelectionModel() {
		TreeSelectionModel treeSelectionModel = new DefaultTreeSelectionModel();
		treeSelectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		return treeSelectionModel;
	}
	
	private StateChangeListener buildPositionListener() {
		return new StateChangeListener() {
			public void stateChanged(StateChangeEvent e) {
				XpathChooserDialog.this.rebuildXpath();
			}
		};
	}
	
	protected Component buildMainPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panel.setPreferredSize(new Dimension(350, 400));
		
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
		panel.add(this.buildXpathTreePane(), constraints);
		
		// build the preview text field label
		JLabel xpathPreviewLabel = this.buildXpathPreviewLabel();
		constraints.gridx 		= 0;
		constraints.gridy 		= 1;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 0;
		constraints.weighty 	= 0;
		constraints.anchor 		= GridBagConstraints.WEST;
		constraints.fill 		= GridBagConstraints.NONE;
		constraints.insets 		= new Insets(5, 0, 0, 5);
		panel.add(xpathPreviewLabel, constraints);
		
		// build the preview text field
		JTextField xpathPreviewTextField = this.buildXpathPreviewTextField();
		xpathPreviewLabel.setLabelFor(xpathPreviewTextField);
		constraints.gridx 		= 1;
		constraints.gridy 		= 1;
		constraints.gridwidth 	= 1;
		constraints.gridheight 	= 1;
		constraints.weightx 	= 1;
		constraints.weighty 	= 0;
		constraints.anchor 		= GridBagConstraints.CENTER;
		constraints.fill 		= GridBagConstraints.HORIZONTAL;
		constraints.insets 		= new Insets(5, 0, 0, 0);
		panel.add(xpathPreviewTextField, constraints);
		
		return panel;
	}
	
	private JComponent buildXpathTreePane() {
		return new JScrollPane(this.buildXpathTree());
	}
	
	private JTree buildXpathTree() {
		JTree tree = SwingComponentFactory.buildTree(this.xpathTreeModel);
		tree.setSelectionModel(this.xpathTreeSelectionModel);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setEditable(true);
		tree.setInvokesStopCellEditing(true);
		tree.setCellRenderer(this.buildTreeCellRenderer());
		tree.setCellEditor(this.buildTreeCellEditor());
		tree.setRowHeight(20);
		tree.addTreeSelectionListener(this.buildTreeSelectionListener());
		return tree;
	}
	
	private TreeCellRenderer buildTreeCellRenderer() {
		return new NodeTreeCellRenderer();
	}
	
	private TreeCellEditor buildTreeCellEditor() {
		return new NodeTreeCellEditor();
	}
	
	private TreeSelectionListener buildTreeSelectionListener() {
		return new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent tse) {
				XpathChooserDialog.this.xpathTreeSelectionChanged();
			}
		};
	}
	
	private JLabel buildXpathPreviewLabel() {
		// "XPath" does not need to be translated
		JLabel xpathPreviewLabel = new JLabel("XPath:");
		xpathPreviewLabel.setDisplayedMnemonic('X');
		return xpathPreviewLabel;
	}
	
	private JTextField buildXpathPreviewTextField() {
		JTextField textField = new JTextField();
		textField.setEditable(false);
		textField.setEnabled(true);
		textField.setDocument(this.buildXpathPreviewDocument());
		return textField;
	}
	
	private DocumentAdapter buildXpathPreviewDocument() {
		return new DocumentAdapter(this.buildSelectedXpathDisplayStringHolder());
	}
	
	private PropertyValueModel buildSelectedXpathDisplayStringHolder() {
		return new TransformationPropertyValueModel(this.selectedXpathHolder) {
			protected Object transform(Object value) {
				if ("".equals(value)) {
					return  XpathChooserDialog.this.resourceRepository().getString("NONE_SELECTED");
				}
				else {
					return  (String) value;
				}
			}
		};
	}
	
	
	// **************** AbstractDialog contract *******************************
	
	protected void okConfirmed() {
		this.xpathTrigger.accept();
		super.okConfirmed();
	}
	
	protected String helpTopicId() {
		return "dialog.xpathChooser";
	}
	
	
	// **************** Convenience *******************************************
	
	private MWXmlField xmlField() {
		return (MWXmlField) this.xmlFieldHolder.getValue();
	}
	
	
	// **************** Behavior **********************************************
	
	private void xpathTreeSelectionChanged() {
		for (int i = this.selectedPath.length; i-- > 0; ) {
			((XpathTreeNode) this.selectedPath[i]).removeStateChangeListener(this.positionListener);
		}
		
		TreePath tempPath = this.xpathTreeSelectionModel.getSelectionPath();
		if (tempPath == null) {
			this.selectedPath = new Object[0];
		} else {
			this.selectedPath = tempPath.getPath();
		}
		
		for (int i = this.selectedPath.length; i-- > 0; ) {
			((XpathTreeNode) this.selectedPath[i]).addStateChangeListener(this.positionListener);
		}
		rebuildXpath();
	}

	private void rebuildXpath() {
		String newXpath = "";
		TreePath selectedTreePath = this.xpathTreeSelectionModel.getSelectionPath();
		
		if (selectedTreePath != null) {
			newXpath = ((XpathTreeNode) selectedTreePath.getLastPathComponent()).xpath();
		}
		
		this.selectedXpathHolder.setValue(newXpath);
	}
	
	public void show() {
		if (this.xmlField().schemaContext() == null) {
			this.showErrorDialog();
		}
		else {
			super.show();
		}
	}
	
	private void showErrorDialog() {
		Component currentWindow = this.currentWindow();
		ResourceRepository repo = this.resourceRepository();
		String errorTitle = repo.getString("XPATH_CHOOSER_DIALOG.NO_CONTEXT_SPECIFIED_TITLE");
		String errorMessage = repo.getString("XPATH_CHOOSER_DIALOG.NO_CONTEXT_SPECIFIED_MESSAGE");
		JOptionPane.showMessageDialog(currentWindow, errorMessage, errorTitle, JOptionPane.WARNING_MESSAGE);
	}
	
	
	// **************** Member classes ****************************************
	
	private final static class XpathTreeModel 
		extends AbstractTreeModel
	{
		// **************** Instance variables ********************************
		
		private SchemaContextNode contextNode;
		
		
		// **************** Constructors **************************************
		
		private XpathTreeModel() {
			super();
		}
		
		XpathTreeModel(MWSchemaContextComponent contextComponent, MWXpathSpec xpathSpec) {
			this();
			this.initialize(contextComponent, xpathSpec);
		}
		
		
		// ************ Initialization ****************************************
		
		private void initialize(MWSchemaContextComponent contextComponent, MWXpathSpec xpathSpec) {
			this.contextNode = new SchemaContextNode(contextComponent, xpathSpec);
		}
		
		
		// **************** TreeModel contract ********************************
		
		public Object getRoot() {
			return this.contextNode;
		}
		
		public Object getChild(Object parent, int index) {
			return ((XpathTreeNode) parent).child(index);
		}
		
		public int getChildCount(Object parent) {
			return ((XpathTreeNode) parent).childrenSize();
		}
		
		public boolean isLeaf(Object node) {
			return ((XpathTreeNode) node).isLeaf();
		}
		
		public int getIndexOfChild(Object parent, Object child) {
			return ((XpathTreeNode) parent).childIndex((XpathTreeNode) child);
		}
		
		public void valueForPathChanged(TreePath path, Object newValue) {
			ElementNode elementNode = (ElementNode) path.getLastPathComponent();
			elementNode.setPosition((String) newValue);
			
			this.fireTreeNodeChanged(path.getParentPath().getPath(),
									 this.getIndexOfChild(elementNode.getParent(), elementNode),
									 elementNode);
		}
	}
	
	
	private abstract static class XpathTreeNode
		extends AbstractModel
		implements RenderingNode, EditingNode
	{
		private XpathTreeNode parent;
		private List children;
		
		private TreeCellRenderer renderer;
		private TreeCellEditor editor;
		
		
		protected XpathTreeNode(XpathTreeNode parent) {
			super();
			this.parent = parent;
		}
		
		
		// **************** Internal ******************************************
		
		private List getChildren() {
			if (this.children == null) {
				this.children = this.buildChildren();
			}
			
			return this.children;
		}
		
		private List buildChildren() {
			return CollectionTools.list(this.newChildren());
		}
		
		protected abstract Iterator newChildren();
		
		
		// **************** RenderingNode contract ****************************
		
		public TreeCellRenderer getRenderer() {
			if (this.renderer == null) {
				this.renderer = this.buildRenderer();
			}
			
			return this.renderer;
		}
		
		protected abstract TreeCellRenderer buildRenderer();
		
		
		// **************** EditingNode contract ******************************
		
		public TreeCellEditor getEditor() {
			if (this.editor == null) {
				this.editor = this.buildEditor();
			}
			
			return this.editor;
		}
		
		protected abstract TreeCellEditor buildEditor();
		
		
		// **************** Exposed *******************************************
		
		XpathTreeNode getParent() {
			return this.parent;
		}
		
		Iterator children() {
			return this.getChildren().iterator();
		}
		
		int childrenSize() {
			return this.getChildren().size();
		}
		
		XpathTreeNode child(int index) {
			return (XpathTreeNode) this.getChildren().get(index);
		}
		
		int childIndex(XpathTreeNode child) {
			return this.getChildren().indexOf(child);
		}
		
		boolean isLeaf() {
			return this.childrenSize() == 0;
		}
		
		String xpath() {
			if (this.parent == null) {
				return this.xpathStepString();
			}
			else {
				String parentXpath = this.parent.xpath();
				
				if ("".equals(parentXpath)) {
					return this.xpathStepString();
				}
				else {
					return parentXpath + "/" + this.xpathStepString();
				}
			}
		}
		
		abstract String xpathStepString();
	}
	
	
	private final static class SchemaContextNode
		extends XpathTreeNode
		implements RenderingNode
	{
		private MWSchemaContextComponent contextComponent;
		private MWXpathSpec xpathSpec;
		
		
		SchemaContextNode(MWSchemaContextComponent contextComponent, MWXpathSpec xpathSpec) {
			super(null);
			this.initialize(contextComponent, xpathSpec);
		}
		
		
		// **************** Internal ******************************************
		
		private void initialize(MWSchemaContextComponent contextComponent, MWXpathSpec xpathSpec) {
			this.contextComponent = contextComponent;
			this.xpathSpec = xpathSpec;
		}
		
		protected Iterator newChildren() {
			return new CompositeIterator(this.newTextChildren(), this.newComponentChildren());
		}
		
		protected Iterator newTextChildren() {
			if (this.xpathSpec.mayUseSimpleData() && this.contextComponent.containsText()) {
				return new SingleElementIterator(this.buildChildTextNode());
			}
			else {
				return NullIterator.instance();
			}
		}
		
		private Object buildChildTextNode() {
			return new TextNode(this);
		}
		
		protected Iterator newComponentChildren() {
			return new TransformationIterator(SchemaContextNode.this.specXpathComponents()) {
				protected Object transform(Object next) {
					return SchemaContextNode.this.buildChildComponentNode((MWXpathableSchemaComponent) next);
				}
			};
		}
		
		private Iterator specXpathComponents() {
			return new FilteringIterator(this.xpathComponents()) {
				protected boolean accept(Object o) {
					if (o instanceof MWAttributeDeclaration) {
						return SchemaContextNode.this.xpathSpec.mayUseSimpleData();
					}
					else {
						return true;
					}
				}
			};
		}
		
		private Iterator xpathComponents() {
			return (this.contextComponent == null) ? NullIterator.instance() : this.contextComponent.xpathComponents();
		}
		
		private Object buildChildComponentNode(MWXpathableSchemaComponent xpathComponent) {
			return XpathableComponentNode.buildNode(this, xpathComponent, this.xpathSpec);
		}
		
		
		// **************** RenderingNode contract ****************************
		
		public Object getCellValue() {
			return this.contextComponent;
		}
		
		public TreeCellRenderer buildRenderer() {
			return new SimpleTreeCellRenderer() {
				protected Icon buildIcon(Object value) {
					return null;
				}
				
				protected String buildText(Object value) {
					return "";
				}
			};
		}
		
		
		// **************** EditingNode contract ******************************
		
		protected TreeCellEditor buildEditor() {
			return NullTreeCellEditor.instance();
		}
		
		
		// **************** Exposed *******************************************
		
		String xpathStepString() {
			return "";
		}
	}
	
	
	private static class TextNode
		extends XpathTreeNode
	{
		// *************** Constructors ***************************************
		
		TextNode(XpathTreeNode parent) {
			super(parent);
		}
		
		
		// **************** Internal ******************************************
		
		protected Iterator newChildren() {
			return NullIterator.instance();
		}
		
		
		// **************** RenderingNode contract ****************************
		
		public Object getCellValue() {
			return null;
		}
		
		protected TreeCellRenderer buildRenderer() {
			return new SimpleTreeCellRenderer() {
				protected Icon buildIcon(Object value) {
					return null;
				}
				
				protected String buildText(Object value) {
					return MWXmlField.TEXT;
				}
			};
		}
		
		
		// **************** EditingNode contract ******************************
		
		protected TreeCellEditor buildEditor() {
			return NullTreeCellEditor.instance();
		}
		
		
		// **************** Exposed *******************************************
		
		String xpathStepString() {
			return MWXmlField.TEXT;
		}
	}
	
	
	private abstract static class XpathableComponentNode
		extends XpathTreeNode
	{
		// ************ Instance variables ************************************
		
		protected MWXpathableSchemaComponent xpathComponent;
		
		
		// **************** Static methods ************************************
		
		static XpathableComponentNode buildNode(XpathTreeNode parent,
												MWXpathableSchemaComponent xpathComponent,
												MWXpathSpec xpathSpec) 
		{
			if (xpathComponent instanceof MWAttributeDeclaration) {
				return new AttributeNode(parent, (MWAttributeDeclaration) xpathComponent);
			}
			else if (xpathComponent instanceof MWElementDeclaration) {
				return new ElementNode(parent, (MWElementDeclaration) xpathComponent, xpathSpec);
			}
			else {
				throw new IllegalArgumentException("Illegal xpath component");
			}
		}
		
		
		// **************** Constructors **************************************
		
		protected XpathableComponentNode(XpathTreeNode parent, MWXpathableSchemaComponent component) {
			super(parent);
			this.xpathComponent = component;
		}
		
		// **************** RenderingNode contract ********************************
		
		public Object getCellValue() {
			// Since the component is displayed by the label, what we're returning here is the "additional" value.
			// On this level, there is no additional value.
			return null;
		}
		
		/** For use in subclasses */
		protected SimpleTreeCellRenderer buildSimpleRenderer() {
			return new SimpleTreeCellRenderer() {
				protected Icon buildIcon(Object value) {
					return null;
				}
				
				protected String buildText(Object value) {
					return XpathableComponentNode.this.displayString();
				}
			};
		}
		
		
		// **************** Internal **********************************************
		
		protected abstract String displayString();
	}
	
	
	private final static class AttributeNode 
		extends XpathableComponentNode
	{
		// **************** Constructors **************************************
		
		AttributeNode(XpathTreeNode parent, MWAttributeDeclaration attribute) {
			super(parent, attribute);
		}
		
		
		// **************** Internal ******************************************
		
		protected Iterator newChildren() {
			return NullIterator.instance();
		}
		
		private String internalXpathStepString() {
			return "@" + this.xpathComponent.qName();
		}
		
		
		// **************** RenderingNode contract ****************************
		
		public TreeCellRenderer buildRenderer() {
			return this.buildSimpleRenderer();
		}
		
		public TreeCellEditor buildEditor() {
			return NullTreeCellEditor.instance();
		}
		
		protected String displayString() {
			return this.internalXpathStepString();
		}
		
		
		// **************** Exposed *******************************************
		
		String xpathStepString() {
			return this.internalXpathStepString();
		}
	}
	
	
	private final static class ElementNode 
		extends XpathableComponentNode
	{
		// ************ Instance variables ****************************************
		
		private MWXpathSpec xpathSpec;
		
		private int position;
		
		private boolean textBuiltIn;
		
		
		// **************** Constructors ******************************************
		
		protected ElementNode(XpathTreeNode parent, MWElementDeclaration element, MWXpathSpec xpathSpec) {
			super(parent, element);
			this.initialize(xpathSpec);
		}
		
		
		// **************** Internal ******************************************
		
		private void initialize(MWXpathSpec xpathSpec) {
			this.xpathSpec = xpathSpec;
			this.position = this.initialPosition();
			this.textBuiltIn = this.calculateTextBuiltIn();
		}
		
		private boolean calculateTextBuiltIn() {
			return 
				(! this.xpathSpec.mayUseComplexData() 
				 && this.xpathSpec.mayUseSimpleData()
				 && this.xpathComponent.containsText()
				 && CollectionTools.size(this.xpathComponent.xpathComponents()) == 0);
		}
		
		protected Iterator newChildren() {
			return new CompositeIterator(this.newTextChildren(), this.newComponentChildren());
		}
		
		protected Iterator newTextChildren() {
			if (! this.textBuiltIn 
				&& this.xpathSpec.mayUseSimpleData() 
				&& this.xpathComponent.containsText()
			) {
				return new SingleElementIterator(this.buildChildTextNode());
			}
			else {
				return NullIterator.instance();
			}
		}
		
		private Object buildChildTextNode() {
			return new TextNode(this);
		}
		
		protected Iterator newComponentChildren() {
			return new TransformationIterator(ElementNode.this.specXpathComponents()) {
				protected Object transform(Object next) {
					return ElementNode.this.buildChildNode((MWXpathableSchemaComponent) next);
				}
			};
		}
		
		private Iterator specXpathComponents() {
			return new FilteringIterator(this.xpathComponents()) {
				protected boolean accept(Object o) {
					if (o instanceof MWAttributeDeclaration) {
						return ElementNode.this.xpathSpec.mayUseSimpleData();
					}
					else {
						return true;
					}
				}
			};
		}
		
		private Iterator xpathComponents() {
			return this.xpathComponent.xpathComponents();
		}
		
		private Object buildChildNode(MWXpathableSchemaComponent xpathComponent) {
			return XpathableComponentNode.buildNode(this, xpathComponent, this.xpathSpec);
		}
		
		
		// **************** RenderingNode contract ********************************
		
		public Object getCellValue() {
			return (this.position == 0) ? "all" : String.valueOf(this.position);
		}
		
		protected TreeCellRenderer buildRenderer() {
			if (this.canSetPosition()) {
				return this.buildSpinnerRenderer();
			}
			else {
				return this.buildSimpleRenderer();
			}
		}
		
		private SpinnerTreeCellRenderer buildSpinnerRenderer() {
			return new SpinnerTreeCellRenderer(this.displayString(), this.buildSpinnerModel()) {
				protected JComponent buildComponent() {
					JSpinner spinner = (JSpinner) super.buildComponent();
					spinner.setPreferredSize(new Dimension(40, spinner.getPreferredSize().height));
					JFormattedTextField textField = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
					textField.setEditable(false);
					textField.setColumns(4);
					return spinner;
				}
			};
		}
		
		private SpinnerModel buildSpinnerModel() {
			return new ElementPositionSpinnerModel(this.position, this.minPosition(), this.maxPosition());
		}
		
		protected TreeCellEditor buildEditor() {
			if (this.canSetPosition()) {
				return this.buildSpinnerEditor();
			}
			else {
				return NullTreeCellEditor.instance();
			}
		}
		
		private TreeCellEditor buildSpinnerEditor() {
			return new TreeCellEditorAdapter(this.buildSpinnerRenderer());
		}
		
		protected String displayString() {
			String displayString = this.xpathComponent.qName();
			
			if (this.textBuiltIn) {
				displayString += ("/" + MWXmlField.TEXT);
			}
			
			return displayString;
		}
		
		
		// **************** Internal **********************************************
		
		private MWElementDeclaration element() {
			return (MWElementDeclaration) this.xpathComponent;
		}
		
		private boolean canSetPosition() {
			return this.maxPosition() > 1;
		}
		
		private int initialPosition() {
			return (this.canSetPosition()) ? this.minPosition() : 0;
				
		}
		
		private int minPosition() {
			return (this.xpathSpec.mayUseCollectionData()) ? 0 : 1; 
		}
		
		private int maxPosition() {
			return this.element().getMaxOccurs();
		}
		
		
		// **************** Exposed ***********************************************
		
		void setPosition(String positionString) {
			int oldValue = this.position;
			if (positionString.equals("all")) {
				this.position = 0;
			} else {
				this.position = Integer.parseInt(positionString);
			}
			if (oldValue != this.position) {
				this.fireStateChanged();
			}
		}
		
		/** Add position to internal xpath step string */
		String xpathStepString() {
			String xpathStepString = this.xpathComponent.qName();
			
			if (this.position != 0) {
				xpathStepString += ("[" + this.position + "]");
			}
			
			if (this.textBuiltIn) {
				xpathStepString += ("/" + MWXmlField.TEXT);
			}
			
			return xpathStepString;
		}
	}
	
	
	private final static class ElementPositionSpinnerModel 
		extends AbstractSpinnerModel 
	{
		private int value;
		private int min;
		private int max;
	
		ElementPositionSpinnerModel(int value, int min, int max) {
			super();
			this.value = value;
			this.min = min;
			this.max = max;
		}
	
		public Object getValue() {
			return (this.value == 0) ? "all" : String.valueOf(this.value);
		}
	
		public void setValue(Object value) {
			if ((value == null) || ! (value instanceof String)) {
				throw new IllegalArgumentException("illegal value: " + value);
			}
			int old = this.value;
			if (value.equals("all")) {
				this.value = 0;
			} else {
				try {
					this.value = Integer.parseInt((String) value);
				} catch (NumberFormatException ex) {
					// just ignore bogus values...
				}
			}
			if (old != this.value) {
				this.fireStateChanged();
			}
		}
	
		public Object getPreviousValue() {
			if (this.value == 0) {
				return null;
			} else if (this.value == this.min) {
				return "all";
			} else {
				return String.valueOf(this.value - 1);
			}
		}
	
		public Object getNextValue() {
			if (this.value == this.max) {
				return null;
			} else {
				return String.valueOf(this.value + 1);
			}
		}
	}
}
