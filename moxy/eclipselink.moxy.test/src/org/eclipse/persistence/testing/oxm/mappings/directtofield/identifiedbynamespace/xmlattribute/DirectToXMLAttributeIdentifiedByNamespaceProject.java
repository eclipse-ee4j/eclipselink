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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbynamespace.xmlattribute;

import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.Employee;

public class DirectToXMLAttributeIdentifiedByNamespaceProject extends Project {

	private final static String FIRST_PREFIX = "first";
	private final static String FIRST_NAMESPACE = "www.example.com/some-dir/first.xsd";
	private final static String LAST_PREFIX = "last";
	private final static String LAST_NAMESPACE = "www.example.com/some-dir/last.xsd";

  public DirectToXMLAttributeIdentifiedByNamespaceProject() {
    addDescriptor(getEmployeeDescriptor());
  }

  private XMLDescriptor getEmployeeDescriptor() {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(Employee.class);
    descriptor.setDefaultRootElement("employee");

    NamespaceResolver namespaceResolver = new NamespaceResolver();
    namespaceResolver.put(FIRST_PREFIX, FIRST_NAMESPACE);
    namespaceResolver.put(LAST_PREFIX, LAST_NAMESPACE);
    descriptor.setNamespaceResolver(namespaceResolver);

    XMLDirectMapping firstNameMapping = new XMLDirectMapping();
    firstNameMapping.setAttributeName("firstName");
    firstNameMapping.setXPath("@" + FIRST_PREFIX + ":name");
    descriptor.addMapping(firstNameMapping);

    XMLDirectMapping lastNameMapping = new XMLDirectMapping();
    lastNameMapping.setAttributeName("lastName");
    lastNameMapping.setXPath("@" + LAST_PREFIX + ":name");
    descriptor.addMapping(lastNameMapping);

    return descriptor;
  }

}
