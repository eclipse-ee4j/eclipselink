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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;

/**
 * Used by MWQuery if the format SQL is chosen
 */
public final class MWSQLQueryFormat extends MWStringQueryFormat
{
	/**
	 * Default constructor - for TopLink use only.
	 */			
	private MWSQLQueryFormat() {
		super();
	}
	
	MWSQLQueryFormat(MWRelationalSpecificQueryOptions parent) 
	{
		super(parent);
	}

	MWSQLQueryFormat(MWRelationalSpecificQueryOptions parent, String queryString) 
	{
		super(parent, queryString);
	}
	
	String getType() {
		return MWRelationalQuery.SQL_FORMAT;
	}

    public boolean reportAttributesAllowed() {
        return true;
    }
    
	//Persistence
	public static XMLDescriptor buildDescriptor() 
	{
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWSQLQueryFormat.class);

		descriptor.getInheritancePolicy().setParentClass(MWStringQueryFormat.class);
	
		return descriptor;
	}
	public static ClassDescriptor legacy50BuildDescriptor() 
	{
		ClassDescriptor descriptor = MWModel.legacy50BuildStandardDescriptor();
		descriptor.descriptorIsAggregate();
		descriptor.setJavaClass(MWSQLQueryFormat.class);

		descriptor.getInheritancePolicy().setParentClass(MWStringQueryFormat.class);
	
		return descriptor;
	}
	
	//Conversion to Runtime
	void convertToRuntime(ObjectLevelReadQuery runtimeQuery) 
	{
			runtimeQuery.setSQLString(getQueryString());
	}

	void convertFromRuntime(ObjectLevelReadQuery runtimeQuery)
	{
		if (runtimeQuery.getSQLString() != null) {
			this.setQueryString(runtimeQuery.getSQLString());
		}
	}
	
	

}