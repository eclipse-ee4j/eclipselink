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
package org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational;

import java.util.List;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;

/**
 * This class holds on to the expression for queries which have expression format chosen
 */
public final class MWExpressionQueryFormat extends MWQueryFormat 
{
	//expression can never be null
	private volatile MWCompoundExpression expression; 
		public final static String EXPRESSION_PROPERTY = "expression";
		
	/**
	 * Default constructor - for TopLink use only.
	 */	
	private MWExpressionQueryFormat() 
	{
		super();
	}
	
	MWExpressionQueryFormat(MWRelationalSpecificQueryOptions parent) 
	{
		super(parent);
	}

	protected void addChildrenTo(List children) 
	{
		super.addChildrenTo(children);
		children.add(expression);
	}

	/**
	 * initialize persistent state
	 */
	protected void initialize(Node modelObject) 
	{
		super.initialize(modelObject);
		expression = new MWCompoundExpression(this);
	}

	String getType() {
		return MWRelationalQuery.EXPRESSION_FORMAT;
	}

	public MWCompoundExpression getExpression() 
	{
		return expression;
	}
	
	public void setExpression(MWCompoundExpression expression) 
	{
		MWCompoundExpression oldExpression = getExpression();
		this.expression = expression;
		firePropertyChanged(EXPRESSION_PROPERTY, oldExpression, getExpression());
	}
	
    public boolean orderingAttributesAllowed() {
        return true;
    }
    
    public boolean batchReadAttributesAllowed() {
        return true;
    }
    
    public boolean reportAttributesAllowed() {
        return true;
    }
    
    public boolean groupingAtributesAllowed() {
        return true;
    }
	public void toString(StringBuffer sb) 
	{
		super.toString(sb);
		sb.append(", expression = ");
		sb.append(getExpression());
	}

	//persistence
	public static XMLDescriptor buildDescriptor() 
	{
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWExpressionQueryFormat.class);
		descriptor.getInheritancePolicy().setParentClass(MWQueryFormat.class);
	
		// 1-1 to the compound expression
		XMLCompositeObjectMapping expressionMapping = new XMLCompositeObjectMapping();
		expressionMapping.setAttributeName("expression");
		expressionMapping.setReferenceClass(MWCompoundExpression.class);
		expressionMapping.setXPath("main-compound-expression");
		descriptor.addMapping(expressionMapping);
		
		return descriptor;
	}

	//Conversion methods
	void convertToRuntime(DatabaseQuery runtimeQuery) 
	{
		runtimeQuery.setSelectionCriteria(getExpression().buildRuntimeExpression(((ObjectLevelReadQuery)runtimeQuery).getExpressionBuilder())); 	
	}
	void convertFromRuntime(DatabaseQuery runtimeQuery)
	{		
		Expression selectionCriteria = runtimeQuery.getSelectionCriteria();
		
		if (selectionCriteria != null)
		{
			setExpression(MWCompoundExpression.convertFromRuntime(this, selectionCriteria));
		}
	}
}

