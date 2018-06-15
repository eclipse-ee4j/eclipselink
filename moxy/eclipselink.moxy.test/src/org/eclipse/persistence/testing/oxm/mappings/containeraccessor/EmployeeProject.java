/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.oxm.mappings.containeraccessor;

import java.util.ArrayList;

import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.*;

public class EmployeeProject extends Project {
    public EmployeeProject(boolean methodAccess) {
        addEmployeeDescriptor(methodAccess);
        addAddressDescriptor(methodAccess);
        addPhoneNumberDescriptor(methodAccess);
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
        descriptor.addMapping(addressMapping);

        XMLCompositeCollectionMapping phoneMapping = new XMLCompositeCollectionMapping();
        phoneMapping.setAttributeName("phoneNumbers");
        phoneMapping.setReferenceClass(PhoneNumber.class);
        phoneMapping.setXPath("phone-numbers/number");
        phoneMapping.setContainerPolicy(ContainerPolicy.buildPolicyFor(ArrayList.class));
        descriptor.addMapping(phoneMapping);
        this.addDescriptor(descriptor);
    }

    public void addAddressDescriptor(boolean methodAccess) {
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

        XMLInverseReferenceMapping owningEmployee = new XMLInverseReferenceMapping();
        owningEmployee.setReferenceClass(Employee.class);
        owningEmployee.setMappedBy("address");
        owningEmployee.setAttributeName("owningEmployee");
        if (methodAccess) {
            owningEmployee.setSetMethodName("setOwningEmployee");
            owningEmployee.setGetMethodName("getOwningEmployee");
        }
        descriptor.addMapping(owningEmployee);

        this.addDescriptor(descriptor);
    }

    public void addPhoneNumberDescriptor(boolean methodAccess) {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(PhoneNumber.class);

        XMLDirectMapping numberMapping = new XMLDirectMapping();
        numberMapping.setAttributeName("number");
        numberMapping.setXPath("text()");
        descriptor.addMapping(numberMapping);

        XMLInverseReferenceMapping owningEmployee = new XMLInverseReferenceMapping();
        owningEmployee.setReferenceClass(Employee.class);
        owningEmployee.setMappedBy("phoneNumbers");
        owningEmployee.setAttributeName("owningEmployee");
        if (methodAccess) {
            owningEmployee.setSetMethodName("setOwningEmployee");
            owningEmployee.setGetMethodName("getOwningEmployee");
        }
        descriptor.addMapping(owningEmployee);

        this.addDescriptor(descriptor);
    }
}
