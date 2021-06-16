/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.schemareference.unmarshal;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.sessions.Project;

public class EmployeeWithDefaultRootElementProject extends Project {

  public EmployeeWithDefaultRootElementProject() {
    super();
    this.addDescriptor(getEmployeeDescriptor());

  }

  private XMLDescriptor getEmployeeDescriptor() {
    XMLDescriptor xmlDescriptor = new XMLDescriptor();
    xmlDescriptor.setJavaClass(Employee.class);
    xmlDescriptor.setDefaultRootElement("ns:DEFAULT-ROOT");

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
