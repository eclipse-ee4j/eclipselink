/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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