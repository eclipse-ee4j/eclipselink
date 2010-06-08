/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import javax.swing.tree.DefaultTreeModel;

import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWBasicExpression;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWCompoundExpression;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWExpression;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.ListChangeListener;


/**
 * A node in the ExpressionTree which represents a compound expression
 * All that will be displayed for the node is the operator type (AND,OR, NAND, NOR)
 * CompoundExpressionTreeNodes can always have children
 */
final class CompoundExpressionTreeNode extends ExpressionTreeNode 
	implements ListChangeListener, PropertyChangeListener
{
	CompoundExpressionTreeNode(MWCompoundExpression compoundExpression) {
		this(compoundExpression, null);
	}
    
	CompoundExpressionTreeNode(MWCompoundExpression compoundExpression, DefaultTreeModel model) {
		super(compoundExpression, true, model); //these nodes can always have children
	}

	
	MWCompoundExpression getCompoundExpression() {
    	return (MWCompoundExpression) getUserObject();
	}
    
    private ExpressionTreeNode addNodeForExpression(MWExpression expression)
    {
    	ExpressionTreeNode node;
    	
    	if (MWBasicExpression.class.isAssignableFrom(expression.getClass()))
			node = new BasicExpressionTreeNode((MWBasicExpression) expression, getModel());
		else
			node = new CompoundExpressionTreeNode((MWCompoundExpression) expression, getModel());
			
		getModel().insertNodeInto(node, this, getChildCount());  	
	
		return node;
    }
    
    public void initializeChildren()
    {
    	Iterator expressions = getCompoundExpression().expressions();
    	while (expressions.hasNext())
		{
			MWExpression expression = (MWExpression) expressions.next();
			ExpressionTreeNode node = addNodeForExpression(expression);
			node.initializeChildren();
		}
    }
 
	protected void engageListeners()
	{
		getCompoundExpression().addListChangeListener(this);
		getCompoundExpression().addPropertyChangeListener(this);
	}
  	
  	public void itemsReplaced(ListChangeEvent event) {
  		//do nothing for now
  	}
  	public void itemsAdded(ListChangeEvent event) {
  		for (Iterator stream = event.items(); stream.hasNext(); ) {
			addNodeForExpression((MWExpression) stream.next());	
  		}
  	}
  	
  	public void itemsRemoved(ListChangeEvent event) {
 		ExpressionTreeNode node = (ExpressionTreeNode) getChildAt(event.getIndex());
		node.disengageListeners();
		getModel().removeNodeFromParent(node);
  	}
  	
  	public void listChanged(ListChangeEvent event) {
  	}
  	
	public void propertyChange(PropertyChangeEvent evt)
	{
		String propertyName = evt.getPropertyName();
		if (propertyName == MWCompoundExpression.OPERATOR_TYPE_PROPERTY)
		{
			getModel().nodeChanged(this);	
		}
	}

	protected void disengageListeners()
	{
		getCompoundExpression().removeListChangeListener(this);
		getCompoundExpression().removePropertyChangeListener(this);
		while(getChildCount() >0)
		{
			ExpressionTreeNode node = (ExpressionTreeNode) getFirstChild();
			node.disengageListeners();
			getModel().removeNodeFromParent(node);
		}
		setUserObject(null);
	}	
}
