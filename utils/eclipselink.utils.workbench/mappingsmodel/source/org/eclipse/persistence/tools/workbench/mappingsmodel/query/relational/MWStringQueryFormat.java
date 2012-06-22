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
import org.eclipse.persistence.tools.workbench.utility.node.Node;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

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

	//legacy toplink use only
	protected void legacySetQueryStringForTopLink(String newQueryString) {
		this.queryString = newQueryString;
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
	
		((XMLDirectMapping)descriptor.addDirectMapping("queryString", "query-string/text()")).setNullValue("");

		return descriptor;
	}	

}
