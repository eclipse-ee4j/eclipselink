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
package org.eclipse.persistence.testing.oxm.readonly;

import java.util.Vector;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;


public class TwoDirectCollectionMappingProject extends Project 
{
	public TwoDirectCollectionMappingProject() 
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
		
		XMLCompositeDirectCollectionMapping responsibilities2 = new XMLCompositeDirectCollectionMapping();
		responsibilities2.setAttributeName("primaryResponsibilities2");
		responsibilities2.setXPath("primary-responsibilities/responsibility/text()");
		responsibilities2.getContainerPolicy().setContainerClass(Vector.class);
		descriptor.addMapping(responsibilities2);		

		this.addDescriptor(descriptor);
	}
}
