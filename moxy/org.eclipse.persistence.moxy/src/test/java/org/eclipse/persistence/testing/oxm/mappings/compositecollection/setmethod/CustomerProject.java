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
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.setmethod;

import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

public class CustomerProject extends Project {
    public CustomerProject() {
        super();
        this.addDescriptor(getCustomerDescriptor());
        this.addDescriptor(getPhoneNumberDescriptor());
    }

    public XMLDescriptor getCustomerDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Customer.class);
        xmlDescriptor.setDefaultRootElement("customer");

        XMLCompositeCollectionMapping phoneNumbersMapping = new XMLCompositeCollectionMapping();
        phoneNumbersMapping.setAttributeName("phoneNumbers");
        phoneNumbersMapping.setGetMethodName("getPhoneNumbers");
        phoneNumbersMapping.setSetMethodName("setPhoneNumbers");
        phoneNumbersMapping.setXPath("phone-number");
        phoneNumbersMapping.setReferenceClass(PhoneNumber.class);
        xmlDescriptor.addMapping(phoneNumbersMapping);

        return xmlDescriptor;
    }

    public XMLDescriptor getPhoneNumberDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(PhoneNumber.class);

        XMLDirectMapping valueMapping = new XMLDirectMapping();
        valueMapping.setAttributeName("value");
        valueMapping.setXPath("text()");
        xmlDescriptor.addMapping(valueMapping);

        return xmlDescriptor;
    }
}
