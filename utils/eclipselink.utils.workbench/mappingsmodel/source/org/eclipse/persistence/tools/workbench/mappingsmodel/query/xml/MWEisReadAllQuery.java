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
package org.eclipse.persistence.tools.workbench.mappingsmodel.query.xml;

import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWReadAllQuery;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;

public final class MWEisReadAllQuery 
	extends MWAbstractEisReadQuery 
	implements MWReadAllQuery
{

	// **************** Static methods ****************	

	public static XMLDescriptor buildDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(MWEisReadAllQuery.class);	
		descriptor.getInheritancePolicy().setParentClass(MWAbstractEisReadQuery.class);
		
		return descriptor;
	}
	
	
	// **************** Constructors ****************	
	
	/** Default constructor - for TopLink use only. */			
	private MWEisReadAllQuery() {
		super();
	}

	MWEisReadAllQuery(MWEisQueryManager parent, String name) {
		super(parent, name);
	}	
	
	
	// ******************* Morphing *******************
		
	public String queryType() {
		return READ_ALL_QUERY;
	}
	
	public MWReadAllQuery asReadAllQuery() {
		return this;
	}
	
	// **************** Runtime Conversion ****************	
	
	protected ObjectLevelReadQuery buildRuntimeQuery() {
		return new ReadAllQuery();
	}

}
