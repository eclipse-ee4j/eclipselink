/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - April 21/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.identifiedbyname;

import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.sessions.Project;

public class CompositeObjectIdentifiedByNameTextProject extends Project {

  public CompositeObjectIdentifiedByNameTextProject() {

    addDescriptor(getEmployeeDescriptor());
  }

  private XMLDescriptor getEmployeeDescriptor() {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(EmployeeWithObjects.class);
    descriptor.setDefaultRootElement("employee");

    XMLDirectMapping idMapping = new XMLDirectMapping();
    idMapping.setXPath("id/text()");
    idMapping.setAttributeName("id");
    descriptor.addMapping(idMapping);

    XMLCompositeObjectMapping addressMapping = new XMLCompositeObjectMapping();
    addressMapping.setAttributeName("mailingAddress");
    addressMapping.setXPath("mailing-address");
    addressMapping.setGetMethodName("getMailingAddress");
    addressMapping.setSetMethodName("setMailingAddress");
    descriptor.addMapping(addressMapping);

    XMLCompositeObjectMapping emailMapping = new XMLCompositeObjectMapping();
    emailMapping.setAttributeName("emailAddress");
    emailMapping.setXPath("info/email-address");
    emailMapping.setGetMethodName("getEmailAddress");
    emailMapping.setSetMethodName("setEmailAddress");
    descriptor.addMapping(emailMapping);

    XMLCompositeObjectMapping salaryMapping = new XMLCompositeObjectMapping();
    salaryMapping.setAttributeName("salary");
    salaryMapping.setXPath("salary");
    ((XMLField)salaryMapping.getField()).setIsTypedTextField(true);
    descriptor.addMapping(salaryMapping);

    return descriptor;
  }
}
