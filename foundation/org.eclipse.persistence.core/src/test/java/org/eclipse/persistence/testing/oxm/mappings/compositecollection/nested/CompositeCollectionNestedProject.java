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
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.nested;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.EmailAddress;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.Employee;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.MailingAddress;

public class CompositeCollectionNestedProject extends Project {

  public CompositeCollectionNestedProject() {
    addDescriptor(getEmployeeDescriptor());
    addDescriptor(getEmailAddressDescriptor());
    addDescriptor(getMailingAddressDescriptor());
    addDescriptor(getProjectDescriptor());
  }

  private XMLDescriptor getEmployeeDescriptor() {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(Employee.class);

    XMLDirectMapping idMapping = new XMLDirectMapping();
    idMapping.setAttributeName("id");
    idMapping.setXPath("@id");
    descriptor.addMapping(idMapping);

    XMLCompositeCollectionMapping emailMapping = new XMLCompositeCollectionMapping();
    emailMapping.setAttributeName("emailAddresses");
    emailMapping.useCollectionClass(java.util.Vector.class);
    emailMapping.setReferenceClass(EmailAddress.class);
    emailMapping.setXPath("email-addresses/email-address");
    descriptor.addMapping(emailMapping);

    XMLCompositeCollectionMapping mailingMapping = new XMLCompositeCollectionMapping();
    mailingMapping.setAttributeName("mailingAddresses");
    mailingMapping.useCollectionClass(java.util.Vector.class);
    mailingMapping.setReferenceClass(MailingAddress.class);
    mailingMapping.setXPath("mailing-addresses/mailing-address");
    descriptor.addMapping(mailingMapping);

    return descriptor;
  }

    private XMLDescriptor getProjectDescriptor() {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(org.eclipse.persistence.testing.oxm.mappings.compositecollection.nested.Project.class);
    descriptor.setDefaultRootElement("project");

    XMLDirectMapping nameMapping = new XMLDirectMapping();
    nameMapping.setAttributeName("name");
    nameMapping.setXPath("name/text()");
    descriptor.addMapping(nameMapping);

    XMLCompositeCollectionMapping employeesMapping = new XMLCompositeCollectionMapping();
    employeesMapping.setAttributeName("employees");
    employeesMapping.useCollectionClass(java.util.Vector.class);
    employeesMapping.setXPath("employees/employee");
    employeesMapping.setReferenceClass(Employee.class);
    descriptor.addMapping(employeesMapping);

        return descriptor;
    }

   private XMLDescriptor getEmailAddressDescriptor() {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(EmailAddress.class);

    XMLDirectMapping userIDMapping = new XMLDirectMapping();
    userIDMapping.setAttributeName("userID");
    userIDMapping.setXPath("user-id/text()");
    descriptor.addMapping(userIDMapping);

    XMLDirectMapping domainMapping = new XMLDirectMapping();
    domainMapping.setAttributeName("domain");
    domainMapping.setXPath("domain/text()");
    descriptor.addMapping(domainMapping);

    return descriptor;
  }

  private XMLDescriptor getMailingAddressDescriptor() {
    XMLDescriptor descriptor = new XMLDescriptor();
    descriptor.setJavaClass(MailingAddress.class);

    XMLDirectMapping streetMapping = new XMLDirectMapping();
    streetMapping.setAttributeName("street");
    streetMapping.setXPath("street/text()");
    descriptor.addMapping(streetMapping);

    XMLDirectMapping cityMapping = new XMLDirectMapping();
    cityMapping.setAttributeName("city");
    cityMapping.setXPath("city/text()");
    descriptor.addMapping(cityMapping);

    XMLDirectMapping provinceMapping = new XMLDirectMapping();
    provinceMapping.setAttributeName("province");
    provinceMapping.setXPath("province/text()");
    descriptor.addMapping(provinceMapping);

    XMLDirectMapping postalCodeMapping = new XMLDirectMapping();
    postalCodeMapping.setAttributeName("postalCode");
    postalCodeMapping.setXPath("postal-code/text()");
    descriptor.addMapping(postalCodeMapping);

    return descriptor;
  }
}
