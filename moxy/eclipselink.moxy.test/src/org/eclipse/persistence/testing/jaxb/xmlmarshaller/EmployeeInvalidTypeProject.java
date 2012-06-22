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
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import java.net.URL;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.oxm.schema.XMLSchemaURLReference;
import org.eclipse.persistence.sessions.Project;

public class EmployeeInvalidTypeProject extends Project {

  private NamespaceResolver namespaceResolver;

	public EmployeeInvalidTypeProject() {
	
	 namespaceResolver = new NamespaceResolver();
    namespaceResolver.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");	
	
		addDescriptor(getPhoneDescriptor());
		
	}

	private XMLDescriptor getPhoneDescriptor() {
		XMLDescriptor descriptor = new XMLDescriptor();
		descriptor.setJavaClass(Phone.class);
		
		XMLDirectMapping numberMapping = new XMLDirectMapping();
		numberMapping.setAttributeName("number");
		numberMapping.setGetMethodName("getNumber");
		numberMapping.setSetMethodName("setNumber");
		numberMapping.setXPath("text()");
		descriptor.addMapping(numberMapping);

		URL schemaURL = ClassLoader.getSystemResource("org/eclipse/persistence/testing/oxm/jaxb/Employee.xsd");
		XMLSchemaURLReference schemaRef = new XMLSchemaURLReference(schemaURL);
		schemaRef.setType(4);
		schemaRef.setSchemaContext("/phone-type");
		descriptor.setSchemaReference(schemaRef);

		return descriptor;
	}
}
