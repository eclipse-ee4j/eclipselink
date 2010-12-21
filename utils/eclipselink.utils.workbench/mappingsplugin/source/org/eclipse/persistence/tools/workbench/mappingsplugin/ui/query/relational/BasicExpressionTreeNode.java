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

import javax.swing.tree.DefaultTreeModel;

import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWBasicExpression;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWLiteralArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryParameterArgument;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational.MWQueryableArgument;



/**
 * Node for a BasicExpression in an Expression tree
 * The display in the tree will be   firstArgument operator secondArgument
 * BasicExpressionTreeNodes never have children and their parent is always a CompoundExpressionTreeNode
 */
final class BasicExpressionTreeNode extends ExpressionTreeNode implements PropertyChangeListener
{

    BasicExpressionTreeNode(MWBasicExpression basicExpression)
    {
		this(basicExpression, null);
    }
    
    BasicExpressionTreeNode(MWBasicExpression basicExpression, DefaultTreeModel model)
    {
		super(basicExpression, false, model); //these nodes can never have children
    }
     
    MWBasicExpression getBasicExpression()
    {
    	return (MWBasicExpression) getUserObject();
    }
    
	public void initializeChildren()
	{
		//BasicExpressions do not have children, so do nothing here
	}

	protected void engageListeners()
	{
 		getBasicExpression().addPropertyChangeListener(this);
 		getBasicExpression().getFirstArgument().addPropertyChangeListener(this);
 	
 		if (getBasicExpression().getSecondArgument() != null)
 	 		getBasicExpression().getSecondArgument().addPropertyChangeListener(this);
 	}

	protected void disengageListeners()
	{
		getBasicExpression().removePropertyChangeListener(this);
 		getBasicExpression().getFirstArgument().removePropertyChangeListener(this);
 	
 		if (getBasicExpression().getSecondArgument() != null)
 	 		getBasicExpression().getSecondArgument().removePropertyChangeListener(this);
 	}
 	
	public void propertyChange(PropertyChangeEvent evt)
	{
		String propertyName = evt.getPropertyName();
		if ( propertyName == MWBasicExpression.OPERATOR_TYPE_PROPERTY
			|| propertyName == MWQueryableArgument.QUERYABLE_ARGUMENT_ELEMENT_PROPERTY
			|| propertyName == MWLiteralArgument.VALUE_PROPERTY
			|| propertyName == MWQueryParameterArgument.QUERY_PARAMETER_PROPERTY)
			{
				getModel().nodeChanged(this);
			}
		else if (propertyName == MWBasicExpression.FIRST_ARUGMENT_PROPERTY
			|| propertyName == MWBasicExpression.SECOND_ARGUMENT_PROPERTY)
			{
				MWArgument oldArgument = (MWArgument)evt.getOldValue();
				MWArgument newArgument = (MWArgument)evt.getNewValue();
				if (oldArgument != null)
					oldArgument.removePropertyChangeListener(this);
				if (newArgument != null)
					newArgument.addPropertyChangeListener(this);
					
				getModel().nodeChanged(this);
			}	
	}   	
}
