/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.xmlmarshaller;

import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sessions.Project;

public class XMLMarshallerCarProject extends Project
{
	public XMLMarshallerCarProject()
	{
		addDescriptor(getCarDescriptor());
	}

	private XMLDescriptor getCarDescriptor() {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(Car.class);
    descriptor.setDefaultRootElement("Car");
        XMLSchemaClassPathReference ref = new XMLSchemaClassPathReference("org/eclipse/persistence/testing/oxm/xmlmarshaller/Car.xsd");
        descriptor.setSchemaReference(ref);
		
    XMLDirectMapping licenseMapping = new XMLDirectMapping();
    licenseMapping.setAttributeName("license");
    licenseMapping.setXPath("license-number/text()");
    descriptor.addMapping(licenseMapping);   

    return descriptor;
  }  

}
