/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname.xmlattribute;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname.Employee;

public class DirectToXMLAttributeIdentifiedByNameProject extends Project {

  public DirectToXMLAttributeIdentifiedByNameProject() {
    addDescriptor(getEmployeeDescriptor());
  }

  private XMLDescriptor getEmployeeDescriptor() {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(Employee.class);
    descriptor.setDefaultRootElement("employee");

    XMLDirectMapping idMapping = new XMLDirectMapping();
    idMapping.setAttributeName("id");
    idMapping.setXPath("@id");
    descriptor.addMapping(idMapping);

    XMLDirectMapping firstNameMapping = new XMLDirectMapping();
    firstNameMapping.setAttributeName("firstName");
    firstNameMapping.setXPath("@first-name");
    descriptor.addMapping(firstNameMapping);

    XMLDirectMapping lastNameMapping = new XMLDirectMapping();
    lastNameMapping.setAttributeName("lastName");
    lastNameMapping.setXPath("@last-name");
    descriptor.addMapping(lastNameMapping);

    return descriptor;
  }

}
