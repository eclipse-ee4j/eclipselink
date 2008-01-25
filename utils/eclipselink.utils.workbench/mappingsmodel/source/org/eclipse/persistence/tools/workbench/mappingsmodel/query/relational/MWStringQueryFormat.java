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
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.XMLDescriptor;

/**
 * This abstract class is used MWQuery if the format SQL or EJBQL is chosen
 */
public abstract class MWStringQueryFormat extends MWQueryFormat 
{
	private volatile String queryString;
		public final static String QUERY_STRING_PROPERTY = "queryString";

	/**
	 * Default constructor - for TopLink use only.
	 */			
	protected MWStringQueryFormat() {
		super();
	}

	MWStringQueryFormat(MWRelationalSpecificQueryOptions parent) 
	{
		super(parent);
	}

	MWStringQueryFormat(MWRelationalSpecificQueryOptions parent, String queryString) 
	{
		this(parent);
		this.queryString = queryString;
	}

	protected void initialize(Node parent)
	{
		super.initialize(parent);
		this.queryString = "";
	}

	public String getQueryString()
	{
		return this.queryString;
	}

	public void setQueryString(String newQueryString) 
	{
		String oldQueryString = this.queryString;
		this.queryString = newQueryString;
		firePropertyChanged(QUERY_STRING_PROPERTY, oldQueryString, newQueryString);
	}
	
	public void toString(StringBuffer sb) 
	{
		super.toString(sb);
		sb.append(", queryString = ");
		sb.append(getQueryString());
	}

	//Persistence
	public static XMLDescriptor buildDescriptor() 
	{
		XMLDescriptor descriptor = new XMLDescriptor();

		descriptor.setJavaClass(MWStringQueryFormat.class);
		descriptor.getInheritancePolicy().setParentClass(MWQueryFormat.class);
	
		descriptor.addDirectMapping("queryString", "query-string/text()");

		return descriptor;
	}	
	public static ClassDescriptor legacy50BuildDescriptor() 
	{
		ClassDescriptor descriptor = MWModel.legacy50BuildStandardDescriptor();
		descriptor.descriptorIsAggregate();
		descriptor.setJavaClass(MWStringQueryFormat.class);
		descriptor.getInheritancePolicy().setParentClass(MWQueryFormat.class);
	
		descriptor.addDirectMapping("queryString", "query-string");

		return descriptor;
	}	
	public static ClassDescriptor legacy45BuildDescriptor() 
	{
		ClassDescriptor descriptor = MWModel.legacy45BuildStandardDescriptor();
		descriptor.descriptorIsAggregate();
		descriptor.setJavaClass(MWStringQueryFormat.class);
		descriptor.setTableName("StringQueryFormat");
		descriptor.getInheritancePolicy().setParentClass(MWQueryFormat.class);
			
		// dtf queryString 
		descriptor.addDirectMapping("queryString", "queryString");
			
		return descriptor;
	}
}