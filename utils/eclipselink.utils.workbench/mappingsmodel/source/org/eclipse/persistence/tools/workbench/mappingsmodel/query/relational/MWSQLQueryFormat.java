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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.queries.DatabaseQuery;
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
	
	//Conversion to Runtime
	void convertToRuntime(DatabaseQuery runtimeQuery) 
	{
			runtimeQuery.setSQLString(getQueryString());
	}

	void convertFromRuntime(DatabaseQuery runtimeQuery)
	{
		if (runtimeQuery.getSQLString() != null) {
			this.setQueryString(runtimeQuery.getSQLString());
		}
	}
	
}
