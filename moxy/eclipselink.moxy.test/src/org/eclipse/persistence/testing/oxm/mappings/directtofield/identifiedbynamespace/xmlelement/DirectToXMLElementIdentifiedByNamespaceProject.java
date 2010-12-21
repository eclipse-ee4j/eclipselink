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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbynamespace.xmlelement;

import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.Employee;

public class DirectToXMLElementIdentifiedByNamespaceProject extends Project {

	private final static String FIRST_PREFIX = "first";
	private final static String FIRST_NAMESPACE = "www.example.com/some-dir/first.xsd";
	private final static String LAST_PREFIX = "last";
	private final static String LAST_NAMESPACE = "www.example.com/some-dir/last.xsd";
	private final static String ROOT_PREFIX = "root";
	private final static String ROOT_NAMESPACE = "www.example.com/some-dir/root.xsd";

  public DirectToXMLElementIdentifiedByNamespaceProject() {
    addDescriptor(getEmployeeDescriptor());
  }

  private XMLDescriptor getEmployeeDescriptor() {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(Employee.class);
    descriptor.setDefaultRootElement(ROOT_PREFIX+":employee");

    NamespaceResolver namespaceResolver = new NamespaceResolver();
    namespaceResolver.put(FIRST_PREFIX, FIRST_NAMESPACE);
    namespaceResolver.put(LAST_PREFIX, LAST_NAMESPACE);
		namespaceResolver.put(ROOT_PREFIX, ROOT_NAMESPACE);
    descriptor.setNamespaceResolver(namespaceResolver);

    XMLDirectMapping firstNameMapping = new XMLDirectMapping();
    firstNameMapping.setAttributeName("firstName");
    firstNameMapping.setXPath(FIRST_PREFIX + ":name/text()");
    descriptor.addMapping(firstNameMapping);

    XMLDirectMapping lastNameMapping = new XMLDirectMapping();
    lastNameMapping.setAttributeName("lastName");
    lastNameMapping.setXPath(LAST_PREFIX + ":name/text()");
    descriptor.addMapping(lastNameMapping);

    return descriptor;
  }

}
