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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWBasicExpression;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWCompoundExpression;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWExpression;
import org.eclipse.persistence.tools.workbench.uitools.app.AbstractReadOnlyCollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.CollectionValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.FilteringPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.SimplePropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.swing.ComboBoxModelAdapter;



/**
 * The main panel for the ExpressionBuilderDialog.
 * Contains the ExpressionTree, buttons for manipulating the CompoundExpressions,
 * and a panel to edit the basic expressions
 */
final class ExpressionTreePanel extends AbstractPanel
{
	
	private MWCompoundExpression expression;
	
	private PropertyValueModel selectedExpressionHolder;
	private PropertyValueModel selectedCompoundExpressionHolder;
	
	
	//Models for the ExpressionTree
	private ExpressionTree expressionTree;
	private DefaultTreeModel expressionTreeModel;
	private TreeSelectionModel expressionTreeSelectionModel;

	//Logical Operators
	public final static String AND = "AND";
	public final static String OR = "OR";
	public final static String NAND = "NAND";
	public final static String NOR = "NOR";

	
	private JButton addButton;
	private JButton addNestedButton;
	private JButton removeButton;
	
	private JComboBox logicalOperatorComboBox;

	private BasicExpressionPanel basicExpressionPanel;
	
	private String baseTopicId;
		
		
	protected class ExpressionTreeSelectionListener implements TreeSelectionListener {
		public void valueChanged(TreeSelectionEvent e) {
			if (expressionTreeSelectionModel.getSelectionPath() == null || expressionTreeSelectionModel.getSelectionCount() >1) {
				selectedExpressionHolder.setValue(null);
			}
			else {
				ExpressionTreeNode selectedNode = (ExpressionTreeNode) expressionTreeSelectionModel.getSelectionPath().getLastPathComponent();
				selectedExpressionHolder.setValue(selectedNode.getUserObject());
			}
				
			updateButtons();
		}
	}		
	
	ExpressionTreePanel(WorkbenchContextHolder contextHolder, String baseTopicId) {
		super(contextHolder);
		this.baseTopicId = baseTopicId;
		initialize();
		
	}
	
	private void addBasicExpresion()
	{
		ExpressionTreeNode node = getSelectedCompoundExpressionNode();
	
		//if nothing is selected we will add to the main compound expression
		MWCompoundExpression compoundExpression;
		if (node == null)
		{
			compoundExpression = this.expression;
			node = (ExpressionTreeNode)this.expressionTreeModel.getRoot();
		}
		else 
		{
			compoundExpression = (MWCompoundExpression)node.getUserObject();
		}
					
		MWBasicExpression basicExpression = compoundExpression.addBasicExpression();

		Enumeration children = node.children();
		while (children.hasMoreElements()) {
			ExpressionTreeNode treeNode = (ExpressionTreeNode) children.nextElement();
				if ( treeNode.getUserObject() == basicExpression) {
					expressionTreeSelectionModel.setSelectionPath(new TreePath(treeNode.getPath()));
				}
		}
	}

	private void addNestedExpresion()
	{
		ExpressionTreeNode node = getSelectedCompoundExpressionNode();
		MWCompoundExpression compoundExpression = getSelectedCompoundExpression();
	
		//if nothing is selected we will add to the main compound expression
		if (compoundExpression == null) {
			compoundExpression = this.expression;
			node = (ExpressionTreeNode)this.expressionTreeModel.getRoot();
		}
			
		MWCompoundExpression addedExpression = compoundExpression.addSubCompoundExpression();	

		Enumeration children = node.children();
		while (children.hasMoreElements()) {
			ExpressionTreeNode treeNode = (ExpressionTreeNode) children.nextElement();
				if ( treeNode.getUserObject() == addedExpression) {
					this.expressionTreeSelectionModel.setSelectionPath(new TreePath(((ExpressionTreeNode)treeNode.getChildAt(0)).getPath()));
				}
		}
	}
	
	private ActionListener buildAddBasicExpresionAction()
	 {
		return new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				addBasicExpresion();
			}
		};
	}
	
	private ActionListener buildAddNestedExpressionAction()
	 {
		return new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				addNestedExpresion();
			}
		};
	}

	private ActionListener buildRemoveExpressionAction()
	 {
		return new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				removeSelectedExpressions();
			}
		};
	}	
	
	private Iterator allowableOperatorTypes()
	{
		Collection operatorTypes = new ArrayList();
		operatorTypes.add(AND);
 		operatorTypes.add(OR);
 		operatorTypes.add(NAND);
 		operatorTypes.add(NOR);
 		
		return operatorTypes.iterator();	
	}

	private MWCompoundExpression getSelectedCompoundExpression()
	{
		MWExpression selectedExpression = getSelectedExpression();
		if (selectedExpression == null)
			return null;

    	//might want to look at this one again once I get to refactoring the expressionTree
    	//isAssignableFrom is just a replacement for instanceof.			
		if(MWCompoundExpression.class.isAssignableFrom(selectedExpression.getClass()))
			return (MWCompoundExpression) selectedExpression;
		else
			return ((MWBasicExpression)selectedExpression).getParentCompoundExpression();	
	}
	
	private ExpressionTreeNode getSelectedCompoundExpressionNode() {
		TreePath selectionPath = expressionTreeSelectionModel.getSelectionPath();
		if (selectionPath == null)
			return null;
			
		while (selectionPath.getPathCount() > 0) {
			ExpressionTreeNode lastNode = (ExpressionTreeNode)selectionPath.getLastPathComponent();
			
	    	//might want to look at this one again once I get to refactoring the expressionTree
	    	//isAssignableFrom is just a replacement for instanceof.
			if (MWCompoundExpression.class.isAssignableFrom(lastNode.getUserObject().getClass()))
				return lastNode;
			selectionPath = selectionPath.getParentPath();
		}	
		return null;	
	}
	
	private ExpressionTreeNode getSelectedExpressionNode() {
		if (this.expressionTreeSelectionModel.getSelectionPath() == null || this.expressionTreeSelectionModel.getSelectionCount() >1)
			return null;
			
		return (ExpressionTreeNode)this.expressionTreeSelectionModel.getSelectionPath().getLastPathComponent();
		
	}
	
	private MWExpression getSelectedExpression() {
		ExpressionTreeNode selectedNode = getSelectedExpressionNode();
		if (selectedNode == null)
			return null;
			
		return (MWExpression) selectedNode.getUserObject();
	}
	
	private PropertyValueModel buildSelectedCompoundExpressionHolder(PropertyValueModel selectedExpressionHolder) {
		return new FilteringPropertyValueModel(selectedExpressionHolder) {
			protected boolean accept(Object value) {
				return value instanceof MWCompoundExpression;
			}
		};		
	}
	
	private void initialize() {	
		this.selectedExpressionHolder = new SimplePropertyValueModel();
		this.selectedCompoundExpressionHolder = buildSelectedCompoundExpressionHolder(selectedExpressionHolder);

		setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		
		this.expressionTreeModel = new DefaultTreeModel( new DefaultMutableTreeNode("RootNode"), true); 

		expressionTree = new ExpressionTree(this.expressionTreeModel);
		addHelpTopicId(expressionTree, baseTopicId);
		this.expressionTreeSelectionModel = expressionTree.getSelectionModel();
		this.expressionTreeSelectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		JScrollPane expressionTreePane = new JScrollPane(expressionTree);
		expressionTreePane.setPreferredSize(new Dimension(10,10));
		expressionTree.addTreeSelectionListener(new ExpressionTreeSelectionListener());

		constraints.gridx		= 0;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 1;
		constraints.weighty		= 1;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.CENTER;
		constraints.insets		= new Insets(5, 5, 5, 5);
		add(expressionTreePane, constraints);	
		
		
		JPanel buttonPanel = new JPanel(new GridBagLayout());
			
			this.addButton = new JButton(resourceRepository().getString("ADD_EXPRESSION_BUTTON"));
			this.addButton.setMnemonic(resourceRepository().getMnemonic("ADD_EXPRESSION_BUTTON"));
			this.addButton.addActionListener(buildAddBasicExpresionAction());
			constraints.gridx		= 0;
			constraints.gridy		= 0;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 1;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.NORTHWEST;
			constraints.insets		= new Insets(5, 5, 5, 5);
			buttonPanel.add(this.addButton, constraints);	
			
			this.addNestedButton = new JButton(resourceRepository().getString("ADD_NESTED_EXPRESSION_BUTTON"));
			this.addNestedButton.addActionListener(buildAddNestedExpressionAction());
			this.addNestedButton.setMnemonic(resourceRepository().getMnemonic("ADD_NESTED_EXPRESSION_BUTTON"));
			constraints.gridx		= 0;
			constraints.gridy		= 1;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 1;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.NORTHWEST;
			constraints.insets		= new Insets(0, 5, 5, 5);
			buttonPanel.add(this.addNestedButton, constraints);	
			
			this.removeButton = new JButton(resourceRepository().getString("REMOVE_EXPRESSION_BUTTON"));
			this.removeButton.setMnemonic(resourceRepository().getMnemonic("REMOVE_EXPRESSION_BUTTON"));
			this.removeButton.addActionListener(buildRemoveExpressionAction());
			constraints.gridx		= 0;
			constraints.gridy		= 2;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 1;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.NORTHWEST;
			constraints.insets		= new Insets(0, 5, 5, 5);
			buttonPanel.add(this.removeButton, constraints);	
	
			JLabel operatorLabel = new JLabel(resourceRepository().getString("OPERATOR_TYPE_COMBO_BOX_LABEL"));
			operatorLabel.setDisplayedMnemonic(resourceRepository().getMnemonic("OPERATOR_TYPE_COMBO_BOX_LABEL"));
			constraints.gridx		= 0;
			constraints.gridy		= 3;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 0;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.NORTHWEST;
			constraints.insets		= new Insets(0, 5, 5, 5);
			buttonPanel.add(operatorLabel, constraints);	
		
			this.logicalOperatorComboBox = new JComboBox();
			this.logicalOperatorComboBox.setModel(buildLogicalOperatorComboBoxModel());
			operatorLabel.setLabelFor(logicalOperatorComboBox);
			constraints.gridx		= 0;
			constraints.gridy		= 4;
			constraints.gridwidth	= 1;
			constraints.gridheight	= 1;
			constraints.weightx		= 1;
			constraints.weighty		= 0;
			constraints.fill		= GridBagConstraints.HORIZONTAL;
			constraints.anchor		= GridBagConstraints.NORTHWEST;
			constraints.insets		= new Insets(0, 5, 5, 5);
			buttonPanel.add(this.logicalOperatorComboBox, constraints);	


		constraints.gridx		= 1;
		constraints.gridy		= 0;
		constraints.gridwidth	= 1;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.NONE;
		constraints.anchor		= GridBagConstraints.NORTHEAST;
		constraints.insets		= new Insets(5, 5, 5, 5);
		add(buttonPanel, constraints);	

		this.basicExpressionPanel = new BasicExpressionPanel(this.selectedExpressionHolder, getWorkbenchContextHolder());
		constraints.gridx		= 0;
		constraints.gridy		= 1;
		constraints.gridwidth	= 2;
		constraints.gridheight	= 1;
		constraints.weightx		= 0;
		constraints.weighty		= 0;
		constraints.fill		= GridBagConstraints.BOTH;
		constraints.anchor		= GridBagConstraints.SOUTH;
		constraints.insets		= new Insets(5, 5, 5, 5);
		add(this.basicExpressionPanel, constraints);
		
		updateButtons();
	}
	
	
	// *********** logical operator ************
	
	private ComboBoxModel buildLogicalOperatorComboBoxModel() {
		return new ComboBoxModelAdapter(buildOperatorTypeCollectionHolder(), buildOperatorAdapter());
	}
	
	private CollectionValueModel buildOperatorTypeCollectionHolder() {
		return new AbstractReadOnlyCollectionValueModel() {
			public Object getValue() {
				return allowableOperatorTypes();
			}
		};
	}
		
	private PropertyValueModel buildOperatorAdapter() {
		return new PropertyAspectAdapter(selectedCompoundExpressionHolder, MWCompoundExpression.OPERATOR_TYPE_PROPERTY) {
		
			protected Object getValueFromSubject() {
				if (((MWCompoundExpression) subject).getOperatorType() == MWCompoundExpression.AND)
					return AND;
				else if (((MWCompoundExpression) subject).getOperatorType() == MWCompoundExpression.OR)
					return OR;
				else if (((MWCompoundExpression) subject).getOperatorType() == MWCompoundExpression.NAND)
					return NAND;
				else 
					return NOR; 
			}

			protected void setValueOnSubject(Object value) {
		
				if (value == AND)
					getSelectedCompoundExpression().setOperatorType(MWCompoundExpression.AND);
				else if (value == OR)
					getSelectedCompoundExpression().setOperatorType(MWCompoundExpression.OR);
				else if (value == NAND)
					getSelectedCompoundExpression().setOperatorType(MWCompoundExpression.NAND);
				else if (value == NOR)
					getSelectedCompoundExpression().setOperatorType(MWCompoundExpression.NOR);
			}
		};
		
	}

	protected void disengageListeners() {
		CompoundExpressionTreeNode rootNode = (CompoundExpressionTreeNode)expressionTreeModel.getRoot();		
		rootNode.disengageListeners();
		this.expression = null;
	}
	
	private void removeSelectedExpressions() {
		TreePath[] selectionPaths = this.expressionTreeSelectionModel.getSelectionPaths();
		
		for (int i = 0; i < selectionPaths.length; i++) 
		{
			TreePath path = selectionPaths[i];
			ExpressionTreeNode chosenNode = (ExpressionTreeNode) path.getLastPathComponent();
			MWExpression chosenExpression = (MWExpression) chosenNode.getUserObject();
			
			MWCompoundExpression parentExpression = chosenExpression.getParentCompoundExpression();
			if (parentExpression != null)  
			{
				parentExpression.removeExpression(chosenExpression);				
			}	
			else //if parentExpression is null, the root expression has been chosen
				 //it's parent is a BldrQueryFormat
			{
				((MWCompoundExpression)chosenExpression).clearExpressions();
			}
		}
	}

	void setExpression(MWCompoundExpression expression, int selectionRow)
	{
		this.expression = expression;
		
		ExpressionTreeNode node = new CompoundExpressionTreeNode(expression);
		this.expressionTreeModel.setRoot(node);
		node.setModel(expressionTreeModel);
		node.initializeChildren();
		
		for (int i = 0; i < selectionRow; i++)
			expressionTree.expandRow(i);
		expressionTree.setSelectionRow(selectionRow);
		expressionTree.scrollRowToVisible(selectionRow);
		
	}
	
	//tree selection event method will call this
	private void updateButtons()
	{
		//if nothing is selected, we will add it directly to the main expression
		this.addButton.setEnabled(this.expressionTreeSelectionModel.getSelectionCount() <= 1);
		
		//if nothing is selected, we will add it directly to the main expression
		this.addNestedButton.setEnabled(this.expressionTreeSelectionModel.getSelectionCount() <= 1);
		
		this.removeButton.setEnabled(this.expressionTreeSelectionModel.getSelectionCount() >= 1);
		
		this.logicalOperatorComboBox.setEnabled((this.expressionTreeSelectionModel.getSelectionCount() >= 1) && (getSelectedExpression() != null && MWCompoundExpression.class.isAssignableFrom(getSelectedExpression().getClass())));
	}

}
