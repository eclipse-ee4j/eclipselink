/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryParameter;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;

/**
 * Used by MWQuery if the format EJBQL is chosen
 */
public final class MWEJBQLQueryFormat 
	extends MWStringQueryFormat
{
	// **************** Constructors ******************************************
	
	/** Default constructor - for TopLink use only. */		
	private MWEJBQLQueryFormat()
	{
		super();
	}

	MWEJBQLQueryFormat(MWRelationalSpecificQueryOptions parent)
	{
		super(parent);
	}

	MWEJBQLQueryFormat(MWRelationalSpecificQueryOptions parent, String queryString)
	{
		super(parent, queryString);
	}
	
	String getType() {
		return MWRelationalQuery.EJBQL_FORMAT;
	}
    
    public boolean batchReadAttributesAllowed() {
        return true;
    }
    
	// **************** Conversion ********************************************
	
	void convertToRuntime(ObjectLevelReadQuery runtimeQuery) 
	{
		runtimeQuery.setEJBQLString(getQueryString());
	}	

	void convertFromRuntime(ObjectLevelReadQuery runtimeQuery)
	{
		if (runtimeQuery.getEJBQLString() != null) {
			this.setQueryString(runtimeQuery.getEJBQLString());					
		} 
	}		
		
	String getRuntimeParameterName(MWQueryParameter parameter)
	{
		return String.valueOf(getQuery().getParameterIndex(parameter) + 1);
	}
	
	// **************** Persistence *******************************************
	
	public static XMLDescriptor buildDescriptor() 
	{
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWEJBQLQueryFormat.class);

		descriptor.getInheritancePolicy().setParentClass(MWStringQueryFormat.class);
	
		return descriptor;
	}
	public static ClassDescriptor legacy50BuildDescriptor() 
	{
		ClassDescriptor descriptor = MWModel.legacy50BuildStandardDescriptor();
		descriptor.descriptorIsAggregate();
		descriptor.setJavaClass(MWEJBQLQueryFormat.class);

		descriptor.getInheritancePolicy().setParentClass(MWStringQueryFormat.class);
	
		return descriptor;
	}
}
