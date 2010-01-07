/*******************************************************************************
* Copyright (c) 1998, 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - August 25/2009 - 1.2 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.xmlcontext.byxpath;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class CustomerProject extends Project {

    public CustomerProject() {
        this.addDescriptor(getCustomerDescriptor());
        this.addDescriptor(getAddressDescriptor());
        this.addDescriptor(getPhoneNumberDescriptor());
        this.addDescriptor(getEmergencyContactDescriptor());
    }

     private XMLDescriptor getCustomerDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Customer.class);

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("@id");
        xmlDescriptor.addMapping(idMapping);

        XMLDirectMapping firstNameMapping = new XMLDirectMapping();
        firstNameMapping.setAttributeName("firstName");
        firstNameMapping.setXPath("personal-info/first-name/text()");
        xmlDescriptor.addMapping(firstNameMapping);

        XMLCompositeObjectMapping emergencyContactMapping = new XMLCompositeObjectMapping();
        emergencyContactMapping.setAttributeName("emergencyContact");
        emergencyContactMapping.setXPath(".");
        emergencyContactMapping.setReferenceClass(EmergencyContact.class);
        xmlDescriptor.addMapping(emergencyContactMapping);

        XMLCompositeObjectMapping addressMapping = new XMLCompositeObjectMapping();
        addressMapping.setAttributeName("address");
        addressMapping.setXPath("contact-info/address");
        addressMapping.setReferenceClass(Address.class);
        xmlDescriptor.addMapping(addressMapping);

        XMLCompositeCollectionMapping phoneNumberMapping = new XMLCompositeCollectionMapping();
        phoneNumberMapping.setAttributeName("phoneNumbers");
        phoneNumberMapping.setXPath("contact-info/phone-number");
        phoneNumberMapping.setReferenceClass(PhoneNumber.class);
        xmlDescriptor.addMapping(phoneNumberMapping);

        return xmlDescriptor;
     }

    private XMLDescriptor getAddressDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Address.class);

        XMLDirectMapping street1Mapping = new XMLDirectMapping();
        street1Mapping.setAttributeName("street1");
        street1Mapping.setXPath("street[1]/text()");
        xmlDescriptor.addMapping(street1Mapping);

        XMLDirectMapping street2Mapping = new XMLDirectMapping();
        street2Mapping.setAttributeName("street2");
        street2Mapping.setXPath("street[2]/text()");
        xmlDescriptor.addMapping(street2Mapping);

        XMLDirectMapping cityMapping = new XMLDirectMapping();
        cityMapping.setAttributeName("city");
        cityMapping.setXPath("city/text()");
        xmlDescriptor.addMapping(cityMapping);

        return xmlDescriptor;
    }

    private XMLDescriptor getPhoneNumberDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(PhoneNumber.class);

        XMLDirectMapping valueMapping = new XMLDirectMapping();
        valueMapping.setAttributeName("value");
        valueMapping.setXPath("text()");
        xmlDescriptor.addMapping(valueMapping);

        return xmlDescriptor;
    }

    private XMLDescriptor getEmergencyContactDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(EmergencyContact.class);

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setXPath("emergency-contact/text()");
        xmlDescriptor.addMapping(nameMapping);

        return xmlDescriptor;
    }

}
