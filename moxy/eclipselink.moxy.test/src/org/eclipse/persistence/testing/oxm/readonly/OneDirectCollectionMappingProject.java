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

import java.util.Vector;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;


public class OneDirectCollectionMappingProject extends Project 
{
	public OneDirectCollectionMappingProject() 
	{
		super();
		addEmployeeDescriptor();
	}
	
	public void addEmployeeDescriptor() 
	{
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setDefaultRootElement("employee");
		descriptor.setJavaClass(Employee.class);
		
		XMLCompositeDirectCollectionMapping responsibilities = new XMLCompositeDirectCollectionMapping();
		responsibilities.setAttributeName("primaryResponsibilities");
		responsibilities.setXPath("primary-responsibilities/responsibility/text()");
		responsibilities.getContainerPolicy().setContainerClass(Vector.class);
		responsibilities.readOnly();
		descriptor.addMapping(responsibilities);
		
		this.addDescriptor(descriptor);
	}
}

