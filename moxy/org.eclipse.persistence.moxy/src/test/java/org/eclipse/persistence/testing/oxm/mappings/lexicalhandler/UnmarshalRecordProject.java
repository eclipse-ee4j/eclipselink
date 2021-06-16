/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     bdoughan - December 4/2009 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.lexicalhandler;

import java.util.ArrayList;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class UnmarshalRecordProject extends Project {

    public UnmarshalRecordProject() {
        addDescriptor(getEmployeeDescriptor());
        addDescriptor(getAddressDescriptor());
        addDescriptor(getPhoneNumberDescriptor());
    }

    private XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Employee.class);
        descriptor.setDefaultRootElement("employee");

        XMLDirectMapping firstProperty = new XMLDirectMapping();
        firstProperty.setAttributeName("firstName");
        firstProperty.setXPath("firstName/text()");
        firstProperty.setIsCDATA(true);
        descriptor.addMapping(firstProperty);

        XMLCompositeObjectMapping secondProperty = new XMLCompositeObjectMapping();
        secondProperty.setAttributeName("address");
        secondProperty.setXPath("address");
        secondProperty.setReferenceClass(Address.class);
        descriptor.addMapping(secondProperty);

        XMLCompositeCollectionMapping thirdProperty = new XMLCompositeCollectionMapping();
        thirdProperty.setAttributeName("phoneNumbers");
        thirdProperty.setXPath("phone-number");
        thirdProperty.setReferenceClass(PhoneNumber.class);
        thirdProperty.useCollectionClass(ArrayList.class);
        descriptor.addMapping(thirdProperty);

        XMLDirectMapping fourthProperty = new XMLDirectMapping();
        fourthProperty.setAttributeName("lastName");
        fourthProperty.setXPath("lastName/text()");
        fourthProperty.setIsCDATA(true);
        descriptor.addMapping(fourthProperty);

        return descriptor;
    }

    private XMLDescriptor getAddressDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Address.class);

        XMLDirectMapping streetMapping = new XMLDirectMapping();
        streetMapping.setAttributeName("street");
        streetMapping.setXPath("street/text()");
        streetMapping.setIsCDATA(true);
        descriptor.addMapping(streetMapping);

        return descriptor;
    }

    private XMLDescriptor getPhoneNumberDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(PhoneNumber.class);

        XMLDirectMapping valueMapping = new XMLDirectMapping();
        valueMapping.setAttributeName("value");
        valueMapping.setXPath("text()");
        valueMapping.setIsCDATA(true);
        descriptor.addMapping(valueMapping);

        return descriptor;
    }

}
