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
package org.eclipse.persistence.tools.workbench.test.models.employee;

import java.io.Serializable;
import java.math.BigDecimal;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;

/**
 * <b>Purpose</b>: 		Abstract superclass for Large & Small projects in Employee Demo
 *	<p><b>Description</b>: 	Project is an example of an abstract superclass. It demonstrates how class inheritance can be mapped to database tables.
 *								It's subclasses are concrete and may or may not add columns through additional tables. The PROJ_TYPE field in the
 *								database table indicates which subclass to instantiate. Projects are involved in a M:M relationship with employees. 
 *								The Employee classs maintains the definition of the relation table.
 *	@see LargeProject
 *	@see SmallProject
 */

public abstract class Project implements Serializable, ProjectInterface {
	public BigDecimal id;
	public String name;
	public String description;
	public ValueHolderInterface teamLeader;
	public String version;
	
	public Project()
	{
		this.name = "";
		this.description = "";
		this.teamLeader = new ValueHolder();
	}
	public String getDescription()
	{
		return description;
	}
	
	/**
	 * Return the persistent identifier of the receiver.
	 */
	public BigDecimal getId()
	{
		return id;
	}
	public String getName()
	{
		return name;
	}
	public EmployeeInterface getTeamLeader()
	{
		return (EmployeeInterface) teamLeader.getValue();
	}
	public void postRefresh(DescriptorEvent event)
	{
	}
	protected void postUpdate(DescriptorEvent event)
	{
	}
	public void postInsert(DescriptorEvent event)
	{
	}
	protected void postWrite(DescriptorEvent event)
	{
	}
	public void preDelete(DescriptorEvent event)
	{
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	/**
	 * Set the persistent identifier of the receiver.
	 */	
	public void setId(BigDecimal id)
	{
		this.id = id;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public void setTeamLeader(EmployeeInterface teamLeader)
	{
		this.teamLeader.setValue(teamLeader);
	}
}
