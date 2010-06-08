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
package org.eclipse.persistence.testing.oxm.classloader;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class EmployeeProject extends Project {

	public EmployeeProject() {
		super();
		this.addDescriptor(getEmployeeDescriptor());
	}
	
	private XMLDescriptor getEmployeeDescriptor() {
		try {
			ClassLoader employeeClassLoader = new JARClassLoader("org/eclipse/persistence/testing/oxm/classloader/Employee.jar");
			Class employeeClass = employeeClassLoader.loadClass("org.eclipse.persistence.testing.oxm.classloader.Employee");
		
			XMLDescriptor xmlDescriptor = new XMLDescriptor();
			xmlDescriptor.setJavaClass(employeeClass);
			xmlDescriptor.setDefaultRootElement("employee");
			
			XMLDirectMapping nameMapping = new XMLDirectMapping();
			nameMapping.setAttributeName("name");
			nameMapping.setXPath("text()");
			xmlDescriptor.addMapping(nameMapping);
			
			return xmlDescriptor;
		} catch(ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
}
