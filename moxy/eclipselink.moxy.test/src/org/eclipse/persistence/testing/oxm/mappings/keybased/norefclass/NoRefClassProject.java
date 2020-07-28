/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     bdoughan - April 9/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.keybased.norefclass;

import java.util.ArrayList;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCollectionReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLObjectReferenceMapping;
import org.eclipse.persistence.sessions.Project;

public class NoRefClassProject extends Project {

    public NoRefClassProject() {
        this.addDescriptor(getRootDescriptor());
        this.addDescriptor(getCustomerDescriptor());
        this.addDescriptor(getAddressDescriptor());
        this.addDescriptor(getPhoneNumberDescriptor());
    }

    private XMLDescriptor getRootDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Root.class);
        xmlDescriptor.setDefaultRootElement("root");

        XMLCompositeCollectionMapping customersMapping = new XMLCompositeCollectionMapping();
        customersMapping.setAttributeName("customers");
        customersMapping.setXPath("customer");
        customersMapping.setReferenceClass(Customer.class);
        xmlDescriptor.addMapping(customersMapping);

        XMLCompositeCollectionMapping addressesMapping = new XMLCompositeCollectionMapping();
        addressesMapping.setAttributeName("addresses");
        addressesMapping.setXPath("address");
        addressesMapping.setReferenceClass(Address.class);
        xmlDescriptor.addMapping(addressesMapping);

        XMLCompositeCollectionMapping phoneNumbersMapping = new XMLCompositeCollectionMapping();
        phoneNumbersMapping.setAttributeName("phoneNumbers");
        phoneNumbersMapping.setXPath("phone-number");
        phoneNumbersMapping.setReferenceClass(PhoneNumber.class);
        xmlDescriptor.addMapping(phoneNumbersMapping);

        return xmlDescriptor;
    }

    private XMLDescriptor getCustomerDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Customer.class);

        XMLObjectReferenceMapping addressMapping = new XMLObjectReferenceMapping();
        addressMapping.setAttributeName("address");
        addressMapping.addSourceToTargetKeyFieldAssociation("address-id/text()", null);
        xmlDescriptor.addMapping(addressMapping);

        XMLCollectionReferenceMapping phoneNumbersMapping = new XMLCollectionReferenceMapping();
        phoneNumbersMapping.setAttributeName("phoneNumbers");
        phoneNumbersMapping.addSourceToTargetKeyFieldAssociation("phone-number-id/text()", null);
        phoneNumbersMapping.getContainerPolicy().setContainerClass(ArrayList.class);
        xmlDescriptor.addMapping(phoneNumbersMapping);

        return xmlDescriptor;
    }

    private XMLDescriptor getAddressDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Address.class);
        xmlDescriptor.setPrimaryKeyFieldName("@id");

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("@id");
        xmlDescriptor.addMapping(idMapping);

        return xmlDescriptor;
    }

    private XMLDescriptor getPhoneNumberDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(PhoneNumber.class);
        xmlDescriptor.setPrimaryKeyFieldName("@id");

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("@id");
        xmlDescriptor.addMapping(idMapping);

        return xmlDescriptor;
    }

}
