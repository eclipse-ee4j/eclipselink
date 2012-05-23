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
package org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational;

import org.eclipse.persistence.tools.workbench.mappingsmodel.MWModel;
import org.eclipse.persistence.tools.workbench.mappingsmodel.meta.MWClass;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWAbstractQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWReadAllQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWReadObjectQuery;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;

public final class MWRelationalReadObjectQuery 
	extends MWAbstractRelationalReadQuery
	implements MWReadObjectQuery
{
	
	// **************** static methods ****************	
	
	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWRelationalReadObjectQuery.class);	
		descriptor.getInheritancePolicy().setParentClass(MWAbstractRelationalReadQuery.class);

		return descriptor;
	}
			
	/** Default constructor - for TopLink use only. */			
	private MWRelationalReadObjectQuery() {
		super();
	}

	MWRelationalReadObjectQuery(MWRelationalQueryManager parent, String name) {
		super(parent, name);
	}
	
	
	// ******************* Morphing *******************
	
	public String queryType() {
		return READ_OBJECT_QUERY;
	}
	
	
	public MWReadAllQuery asReadAllQuery() {
		getQueryManager().removeQuery(this);
		MWReadAllQuery newQuery = getQueryManager().addReadAllQuery(getName());
		((MWAbstractQuery) newQuery).initializeFrom((MWReadObjectQuery) this);		
		return newQuery;
	}

	public MWReadObjectQuery asReadObjectQuery() {
		return this;
	}
		
	public MWReportQuery asReportQuery() {
		getQueryManager().removeQuery(this);
		MWReportQuery newQuery = ((MWRelationalQueryManager) getQueryManager()).addReportQuery(getName());
		newQuery.initializeFrom((MWReadObjectQuery) this);		
		return newQuery;
	}

	public MWClass findMethodReturnType(MWClass createMethodReturnType) {
		return createMethodReturnType;
	}
    
    public void formatSetToEjbql() {
        //do nothing
    }
    
    public void formatSetToSql() {
        //do nothing        
    }
    
	// **************** Runtime conversion ****************	
	
	protected ObjectLevelReadQuery buildRuntimeQuery() {
		return new ReadObjectQuery();
	}

}
