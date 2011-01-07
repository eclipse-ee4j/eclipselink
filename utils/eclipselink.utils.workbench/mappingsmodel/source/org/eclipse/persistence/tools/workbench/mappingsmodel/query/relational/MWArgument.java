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

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQuery;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.oxm.XMLDescriptor;

/**
 *  An argument is one side of a MWBasicExpression
 */
public abstract class MWArgument extends MWModel implements Undoable {
	
	public final static String LITERAL_TYPE = "literal";
	public final static String QUERY_KEY_TYPE = "queryKey";
	public final static String PARAMETER_TYPE = "parameter";

	/**
	 * Default constructor - for TopLink use only.
	 */	
	protected MWArgument() {
		super();
	}
	
	
	MWArgument(MWQueryableArgumentParent parent) {
		super(parent);
	}	

  
    protected MWQueryableArgumentParent getQueryableArgumentParent() {
        return (MWQueryableArgumentParent) this.getParent();
    }
    
    public MWQuery getParentQuery() {
        return getQueryableArgumentParent().getParentQuery();
    }
    
	public abstract String getType();
	
	
	void recalculateQueryables()
	{
	}

	public void operatorTypeChanged()
	{
		//Do nothing, this can be overridden if an argument type needs to do 
		//something based on the operator changing.
		//MWLiteralArgument overrides this
	}
	
	//Persistence
	public static XMLDescriptor buildDescriptor() 
	{
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWArgument.class);
	
		//inheritance policy
		org.eclipse.persistence.descriptors.InheritancePolicy ip = (org.eclipse.persistence.descriptors.InheritancePolicy)descriptor.getInheritancePolicy();
		ip.setClassIndicatorFieldName("@type");
		ip.readSubclassesOnQueries();
		ip.addClassIndicator(MWLiteralArgument.class, "literal");
		ip.addClassIndicator(MWQueryParameterArgument.class, "parm");
		ip.addClassIndicator(MWQueryableArgument.class, "queryable");
		ip.addClassIndicator(MWNullArgument.class, "null");
		
		return descriptor;
	}

	//Conversion to Runtime
	abstract Expression runtimeExpression(ExpressionBuilder builder);

}
