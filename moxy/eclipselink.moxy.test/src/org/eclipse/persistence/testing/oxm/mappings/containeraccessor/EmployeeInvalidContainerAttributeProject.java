/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.oxm.mappings.containeraccessor;

import java.util.ArrayList;

import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.*;

public class EmployeeInvalidContainerAttributeProject extends Project {
    public EmployeeInvalidContainerAttributeProject(boolean methodAccess) {
        addEmployeeDescriptor(methodAccess);
        addAddressDescriptor();
        addPhoneNumberDescriptor();
    }

    public void addEmployeeDescriptor(boolean methodAccess) {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Employee.class);
        descriptor.setDefaultRootElement("employee");

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("@id");
        descriptor.addMapping(idMapping);

        XMLDirectMapping firstNameMapping = new XMLDirectMapping();
        firstNameMapping.setAttributeName("firstName");
        firstNameMapping.setXPath("first-name/text()");
        descriptor.addMapping(firstNameMapping);

        XMLDirectMapping lastNameMapping = new XMLDirectMapping();
        lastNameMapping.setAttributeName("lastName");
        lastNameMapping.setXPath("last-name/text()");
        descriptor.addMapping(lastNameMapping);

        XMLCompositeObjectMapping addressMapping = new XMLCompositeObjectMapping();
        addressMapping.setAttributeName("address");
        addressMapping.setReferenceClass(Address.class);
        addressMapping.setXPath("address");
        addressMapping.setContainerAttributeName("invalidAttribute");
        if(methodAccess) {
            addressMapping.setContainerGetMethodName("invalidAttributeGet");
            addressMapping.setContainerSetMethodName("invalidAttributeSet");
        }
        descriptor.addMapping(addressMapping);

        XMLCompositeCollectionMapping phoneMapping = new XMLCompositeCollectionMapping();
        phoneMapping.setAttributeName("phoneNumbers");
        phoneMapping.setReferenceClass(PhoneNumber.class);
        phoneMapping.setXPath("phone-numbers/number");
        phoneMapping.setContainerPolicy(ContainerPolicy.buildPolicyFor(ArrayList.class));
        phoneMapping.setContainerAttributeName("owningEmployee");
        if(methodAccess) {
            phoneMapping.setContainerGetMethodName("getOwningEmployee");
            phoneMapping.setContainerSetMethodName("setOwningEmployee");
        }
        descriptor.addMapping(phoneMapping);
        this.addDescriptor(descriptor);
    }

    public void addAddressDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Address.class);

        XMLDirectMapping streetMapping = new XMLDirectMapping();
        streetMapping.setAttributeName("street");
        streetMapping.setXPath("street/text()");
        descriptor.addMapping(streetMapping);

        XMLDirectMapping cityMapping = new XMLDirectMapping();
        cityMapping.setAttributeName("city");
        cityMapping.setXPath("city/text()");
        descriptor.addMapping(cityMapping);

        XMLDirectMapping stateMapping = new XMLDirectMapping();
        stateMapping.setAttributeName("state");
        stateMapping.setXPath("state/text()");
        descriptor.addMapping(stateMapping);

        XMLDirectMapping countryMapping = new XMLDirectMapping();
        countryMapping.setAttributeName("country");
        countryMapping.setXPath("country/text()");
        descriptor.addMapping(countryMapping);

        this.addDescriptor(descriptor);
    }

    public void addPhoneNumberDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(PhoneNumber.class);

        XMLDirectMapping numberMapping = new XMLDirectMapping();
        numberMapping.setAttributeName("number");
        numberMapping.setXPath("text()");
        descriptor.addMapping(numberMapping);

        this.addDescriptor(descriptor);
    }
}
