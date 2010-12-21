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
package org.eclipse.persistence.testing.oxm.mappings.simpletypes.rootelement;

// TopLink imports
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

public class EmployeeProject extends Project {
	public EmployeeProject() {
		super();
		this.addDescriptor(getEmployeeDescriptor());
	}

	XMLDescriptor getEmployeeDescriptor() {
		XMLDescriptor xmlDescriptor = new XMLDescriptor();
		xmlDescriptor.setJavaClass(Employee.class);
		xmlDescriptor.setDefaultRootElement("employee");
		// xmlDescriptor.setShouldPreserveDocument(true);

		XMLDirectMapping mapping = new XMLDirectMapping();
		mapping.setAttributeName("name");
		mapping.setXPath("text()");
		xmlDescriptor.addMapping(mapping);
		
		return xmlDescriptor;
	}

}
