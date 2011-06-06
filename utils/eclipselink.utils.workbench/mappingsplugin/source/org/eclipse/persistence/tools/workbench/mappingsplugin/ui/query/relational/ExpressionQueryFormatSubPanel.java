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
package org.eclipse.persistence.tools.workbench.mappingsplugin.ui.query.relational;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.eclipse.persistence.tools.workbench.framework.context.WorkbenchContextHolder;
import org.eclipse.persistence.tools.workbench.framework.ui.view.AbstractSubjectPanel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWExpressionQueryFormat;
import org.eclipse.persistence.tools.workbench.uitools.ComponentEnabler;
import org.eclipse.persistence.tools.workbench.uitools.app.FilteringPropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyAspectAdapter;
import org.eclipse.persistence.tools.workbench.uitools.app.PropertyValueModel;
import org.eclipse.persistence.tools.workbench.uitools.app.ValueModel;



/**
 * This panel appears on the Named Queries->QueryFormatPanel, if the user chooses Expression
 * for the query format.  It contains a panel with a tree for viewing the expression
 * and an Edit button to edit the expression.
 */
final class ExpressionQueryFormatSubPanel extends AbstractSubjectPanel
{
	private DefaultTreeModel expressionTreeModel;
	private TreeSelectionModel treeSelectionModel;
	
	private PropertyValueModel expressionQueryFormatHolder;
	
				
	ExpressionQueryFormatSubPanel(ValueModel queryFormatHolder, WorkbenchContextHolder contextHolder) {
		super(queryFormatHolder, contextHolder);
	}
	
	protected ActionListener buildEditExpressionAction() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				editExpresion();
			}
		};
	}

	private PropertyChangeListener buildQueryPropertyChangeListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				populateTree();
			}
		};
	}
	
	protected void initializeLayout() 
	{
		this.expressionQueryFormatHolder = buildExpressionQueryFormatHolder();
		this.expressionQueryFormatHolder.addPropertyChangeListener(ValueModel.VALUE, buildQueryPropertyChangeListener());
		
		GridBagConstraints constraints = new GridBagConstraints();
		
		// Expression label
		JLabel expressionLabel = buildLabel("EXPRESSION_PANEL_LABEL");
		constraints.gridx      = 0;
		constraints.gridy      = 0;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_START;
		constraints.insets     = new Insets(0, 0, 0, 0);
		
		add(expressionLabel, constraints);

		// Expression tree
		this.expressionTreeModel = new DefaultTreeModel(new DefaultMutableTreeNode("RootNode"), true);
		ExpressionTree expressionTree = new ExpressionTree(this.expressionTreeModel);
		this.treeSelectionModel = expressionTree.getSelectionModel();
		this.treeSelectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		expressionLabel.setLabelFor(expressionTree);
		
		expressionTree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				if (event.getClickCount() == 2) {
					ExpressionQueryFormatSubPanel.this.editExpresion();
				}
			}
		});
				
		JScrollPane expressionTreePane = new JScrollPane(expressionTree);
		expressionTreePane.setPreferredSize(new Dimension(0, 0));
		constraints.gridx      = 0;
		constraints.gridy      = 1;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 1;
		constraints.weighty    = 1;
		constraints.fill       = GridBagConstraints.BOTH;
		constraints.anchor     = GridBagConstraints.CENTER;
		constraints.insets     = new Insets(1, 0, 0, 0);
		add(expressionTreePane, constraints);

		// Edit button
		JButton editExpressionButton = buildButton("EDIT_EXPRESSION_BUTTON");
		editExpressionButton.addActionListener(buildEditExpressionAction());
		new ComponentEnabler(buildQueryBooleanHolder(), Collections.singleton(editExpressionButton));
		constraints.gridx      = 0;
		constraints.gridy      = 2;
		constraints.gridwidth  = 1;
		constraints.gridheight = 1;
		constraints.weightx    = 0;
		constraints.weighty    = 0;
		constraints.fill       = GridBagConstraints.NONE;
		constraints.anchor     = GridBagConstraints.LINE_END;
		constraints.insets     = new Insets(5, 0, 0, 0);
		add(editExpressionButton, constraints);		
	}
	
	private MWExpressionQueryFormat getExpressionQueryFormat()
	{
		return (MWExpressionQueryFormat) this.expressionQueryFormatHolder.getValue();
	}

	private PropertyValueModel buildExpressionQueryFormatHolder() {
		return new FilteringPropertyValueModel((PropertyValueModel)getSubjectHolder()) {
			protected boolean accept(Object value) {
				return value instanceof MWExpressionQueryFormat;
			}
		};
	}
	
	private void populateTree()
	{
		if (getExpressionQueryFormat() == null) {
			this.expressionTreeModel.setRoot(new DefaultMutableTreeNode(""));
		}
		else {
			ExpressionTreeNode node = new CompoundExpressionTreeNode(getExpressionQueryFormat().getExpression());
			this.expressionTreeModel.setRoot(node);
			node.setModel(this.expressionTreeModel);
			node.initializeChildren();
		}
	}
	
	private void editExpresion() 
	{
		int selectionRow = this.treeSelectionModel.getLeadSelectionRow();		
		
		ExpressionBuilderDialog expressionBuilderDialog =
			new ExpressionBuilderDialog(getExpressionQueryFormat().getExpression(), selectionRow, getWorkbenchContext());
		expressionBuilderDialog.show();
		if (expressionBuilderDialog.wasCanceled()) {
			getExpressionQueryFormat().getExpression().restoreChanges();
		}
		else {
			getExpressionQueryFormat().getExpression().clearChanges();
		}
		expressionBuilderDialog = null;
		populateTree();
	}
	
	private ValueModel buildQueryBooleanHolder() {
		return new PropertyAspectAdapter(getSubjectHolder()) {
			public Object buildValue() {
				return this.subject == null ? Boolean.FALSE : Boolean.TRUE;
			}
		};
	}	
}
