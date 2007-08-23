/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.readonly;

import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;


public class OneDirectMappingProject extends Project 
{
	public OneDirectMappingProject() 
	{
		super();
		addEmployeeDescriptor();
	}
	
	public void addEmployeeDescriptor() 
	{
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setDefaultRootElement("employee");
		descriptor.setJavaClass(Employee.class);
		
		XMLDirectMapping firstNameMapping = new XMLDirectMapping();
		firstNameMapping.setAttributeName("firstName");
		firstNameMapping.setXPath("first-name/text()");
		firstNameMapping.readOnly();
		descriptor.addMapping(firstNameMapping);
		
		this.addDescriptor(descriptor);
	}
}