/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     rbarkhouse - 2009-10-09 14:17:31 - initial implementation
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.reuse;

import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sessions.Project;

import org.eclipse.persistence.testing.oxm.mappings.compositecollection.EmailAddress;
import org.eclipse.persistence.testing.oxm.mappings.compositecollection.reuse.Employee;

public class CompositeCollectionReuseProject extends Project {

    public CompositeCollectionReuseProject() {
        addDescriptor(getEmployeeDescriptor());
        addDescriptor(getEmailAddressDescriptor());
    }

    private XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Employee.class);
        descriptor.setDefaultRootElement("employee");

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("@id");
        descriptor.addMapping(idMapping);

        XMLCompositeCollectionMapping emailMapping = new XMLCompositeCollectionMapping();
        emailMapping.setAttributeName("emailAddresses");
        emailMapping.setReferenceClass(EmailAddress.class);
        emailMapping.setXPath("email-addresses/email-address");
        emailMapping.setReuseContainer(true);
        descriptor.addMapping(emailMapping);

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

}
