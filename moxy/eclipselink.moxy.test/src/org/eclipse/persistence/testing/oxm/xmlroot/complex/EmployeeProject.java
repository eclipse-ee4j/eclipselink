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
package org.eclipse.persistence.testing.oxm.xmlroot.complex;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.oxm.mappings.*;
import org.eclipse.persistence.sessions.Project;

public class EmployeeProject extends Project {

	public EmployeeProject() {
		super();
		this.addDescriptor(getEmployeeDescriptor());
		this.addDescriptor(getAddressDescriptor());
	}
	
	private XMLDescriptor getEmployeeDescriptor() {
		XMLDescriptor xmlDescriptor = new XMLDescriptor();
		xmlDescriptor.setJavaClass(Employee.class);
		xmlDescriptor.setDefaultRootElement("employee");
		
		NamespaceResolver nsResolver = new NamespaceResolver();
		xmlDescriptor.setNamespaceResolver(nsResolver);

		XMLAnyObjectMapping anyObjectMapping = new XMLAnyObjectMapping();
		// anyObjectMapping.setXPath("g");
		anyObjectMapping.setAttributeName("anyObject");
		anyObjectMapping.setUseXMLRoot(true);
		xmlDescriptor.addMapping(anyObjectMapping);
	
		/*
		XMLAnyCollectionMapping anyCollectionMapping = new XMLAnyCollectionMapping();
		anyCollectionMapping.setAttributeName("anyCollection");
		xmlDescriptor.addMapping(anyCollectionMapping);
		*/
		return xmlDescriptor;
	}

	private XMLDescriptor getAddressDescriptor() {
		XMLDescriptor xmlDescriptor = new XMLDescriptor();
		xmlDescriptor.setJavaClass(Address.class);
		xmlDescriptor.setDefaultRootElement("address");
		
        NamespaceResolver nsResolver = new NamespaceResolver();
        // nsResolver.put("foo", "http://www.example.org/2");
        xmlDescriptor.setNamespaceResolver(nsResolver);
		
		XMLDirectMapping streetMapping = new XMLDirectMapping();
		streetMapping.setAttributeName("street");
		streetMapping.setXPath("street/text()");
		xmlDescriptor.addMapping(streetMapping);

		XMLDirectMapping cityMapping = new XMLDirectMapping();
		cityMapping.setAttributeName("city");
		cityMapping.setXPath("city/text()");
		xmlDescriptor.addMapping(cityMapping);

		return xmlDescriptor;
	}
	
}
