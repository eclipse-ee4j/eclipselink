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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.xmlmarshaller.twoprojects;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class EmployeeProject extends Project {
    public EmployeeProject() {
        super();
        this.addDescriptor(getAddressDescriptor());
        this.addDescriptor(getEmployeeDescriptor());
        this.addDescriptor(getPhoneNumberDescriptor());
    }

    private XMLDescriptor getAddressDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Address.class);
        xmlDescriptor.setDefaultRootElement("customer-address");

        XMLDirectMapping streetMapping = new XMLDirectMapping();
        streetMapping.setAttributeName("street");
        streetMapping.setXPath("employee-street/text()");
        xmlDescriptor.addMapping(streetMapping);

        XMLDirectMapping cityMapping = new XMLDirectMapping();
        cityMapping.setAttributeName("city");
        cityMapping.setXPath("employee-city/text()");
        xmlDescriptor.addMapping(cityMapping);

        return xmlDescriptor;
    }

    private XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Employee.class);
        xmlDescriptor.setDefaultRootElement("employee");

        XMLCompositeObjectMapping addressMapping = new XMLCompositeObjectMapping();
        addressMapping.setReferenceClass(Address.class);
        addressMapping.setAttributeName("address");
        addressMapping.setXPath("employee-address");
        xmlDescriptor.addMapping(addressMapping);

        XMLCompositeCollectionMapping addressesMapping = new XMLCompositeCollectionMapping();
        addressesMapping.setReferenceClass(Address.class);
        addressesMapping.setAttributeName("addresses");
        addressesMapping.setXPath("employee-addresses/employee-address");
        xmlDescriptor.addMapping(addressesMapping);

        return xmlDescriptor;
    }

    private XMLDescriptor getPhoneNumberDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(PhoneNumber.class);
        xmlDescriptor.setDefaultRootElement("employee-phone-number");

        XMLDirectMapping valueMapping = new XMLDirectMapping();
        valueMapping.setAttributeName("value");
        valueMapping.setXPath("text()");
        xmlDescriptor.addMapping(valueMapping);

       return xmlDescriptor;
    }
}
