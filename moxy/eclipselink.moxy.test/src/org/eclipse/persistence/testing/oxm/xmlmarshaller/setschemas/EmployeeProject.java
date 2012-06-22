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
package org.eclipse.persistence.testing.oxm.xmlmarshaller.setschemas;

import java.net.URL;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaURLReference;
import org.eclipse.persistence.sessions.Project;

public class EmployeeProject extends Project {
	
	private static final String XML_SCHEMA_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlmarshaller/setschemas/schema.xsd";
	
	public EmployeeProject() {
		super();
		this.addDescriptor(getEmployeeDescriptor());
	}

	private XMLDescriptor getEmployeeDescriptor() {
		XMLDescriptor xmlDescriptor = new XMLDescriptor();
    xmlDescriptor.setJavaClass(org.eclipse.persistence.testing.oxm.mappings.transformation.Employee.class);
    xmlDescriptor.setDefaultRootElement("ns:root");

		XMLSchemaURLReference schemaReference = new XMLSchemaURLReference();
		URL url = ClassLoader.getSystemResource(XML_SCHEMA_RESOURCE);
		schemaReference.setURL(url);
		xmlDescriptor.setSchemaReference(schemaReference);
		
		NamespaceResolver nsResolver = new NamespaceResolver();
		nsResolver.put("ns", "ElemDecl/maxOccurs");
		nsResolver.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		xmlDescriptor.setNamespaceResolver(nsResolver);

		XMLDirectMapping nameMapping = new XMLDirectMapping();
    nameMapping.setAttributeName("name");
    nameMapping.setXPath("ns:Local");
    xmlDescriptor.addMapping(nameMapping); 
		
		return xmlDescriptor;
	}
	
}
