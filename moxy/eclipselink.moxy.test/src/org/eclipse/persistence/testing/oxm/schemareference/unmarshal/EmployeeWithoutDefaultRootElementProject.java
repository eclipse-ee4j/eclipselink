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
package org.eclipse.persistence.testing.oxm.schemareference.unmarshal;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.sessions.Project;

public class EmployeeWithoutDefaultRootElementProject extends Project {
  
  public EmployeeWithoutDefaultRootElementProject() {
    super();
    this.addDescriptor(getEmployeeDescriptor());
    
  }
  
  private XMLDescriptor getEmployeeDescriptor() {
    XMLDescriptor xmlDescriptor = new XMLDescriptor();
    xmlDescriptor.setJavaClass(Employee.class);

    XMLSchemaClassPathReference schemaReference = new XMLSchemaClassPathReference();
    schemaReference.setSchemaContext("/ns:employee-type");
    xmlDescriptor.setSchemaReference(schemaReference);   

    NamespaceResolver namespaceResolver = new NamespaceResolver();
    namespaceResolver.put("ns", "urn:test");
    xmlDescriptor.setNamespaceResolver(namespaceResolver);
    
    XMLDirectMapping nameMapping = new XMLDirectMapping();
    nameMapping.setAttributeName("name");
    nameMapping.setXPath("ns:name/text()");
    xmlDescriptor.addMapping(nameMapping);
    
    return xmlDescriptor;
  }
  
}
