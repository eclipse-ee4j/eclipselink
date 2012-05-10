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
package org.eclipse.persistence.testing.oxm.mappings.namespaces.identifiedbyname;

import org.eclipse.persistence.testing.oxm.mappings.namespaces.Employee;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class DefaultNamespaceProject extends Project  {

	public DefaultNamespaceProject() {
		super();
		this.addDescriptor(getEmployeeDescriptor());
	}
	
	private XMLDescriptor getEmployeeDescriptor() {
		XMLDescriptor xmlDescriptor = new XMLDescriptor();
		xmlDescriptor.setJavaClass(Employee.class);
		xmlDescriptor.setDefaultRootElement("employee");

		NamespaceResolver nsResolver = new NamespaceResolver();
		nsResolver.setDefaultNamespaceURI("http://www.example.com/EMPLOYEE");
		xmlDescriptor.setNamespaceResolver(nsResolver);

		// Unqualified attributes are in the null namespace
		// rather than the default namespace
		XMLDirectMapping idMapping = new XMLDirectMapping();
		idMapping.setAttributeName("id");
		idMapping.setXPath("@id");
		xmlDescriptor.addMapping(idMapping);

		XMLDirectMapping nameMapping = new XMLDirectMapping();
		nameMapping.setAttributeName("name");
		nameMapping.setXPath("name/text()");
		xmlDescriptor.addMapping(nameMapping);

		return xmlDescriptor;
	}
	
}
