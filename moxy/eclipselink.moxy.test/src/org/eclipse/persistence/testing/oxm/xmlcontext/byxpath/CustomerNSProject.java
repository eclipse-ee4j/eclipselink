/*******************************************************************************
* Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class CustomerNSProject extends Project {

    public CustomerNSProject() {
        this.addDescriptor(getCustomerDescriptor());
        this.addDescriptor(getAddressDescriptor());
        this.addDescriptor(getPhoneNumberDescriptor());
        this.addDescriptor(getEmergencyContactDescriptor());
    }

     private XMLDescriptor getCustomerDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Customer.class);

        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("ns", "urn:customer");
        xmlDescriptor.setNamespaceResolver(namespaceResolver);

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("@ns:id");
        xmlDescriptor.addMapping(idMapping);

        XMLDirectMapping firstNameMapping = new XMLDirectMapping();
        firstNameMapping.setAttributeName("firstName");
        firstNameMapping.setXPath("ns:personal-info/ns:first-name/text()");
        xmlDescriptor.addMapping(firstNameMapping);

        XMLCompositeObjectMapping emergencyContactMapping = new XMLCompositeObjectMapping();
        emergencyContactMapping.setAttributeName("emergencyContact");
        emergencyContactMapping.setXPath(".");
        emergencyContactMapping.setReferenceClass(EmergencyContact.class);
        xmlDescriptor.addMapping(emergencyContactMapping);

        XMLCompositeObjectMapping addressMapping = new XMLCompositeObjectMapping();
        addressMapping.setAttributeName("address");
        addressMapping.setXPath("ns:contact-info/ns:address");
        addressMapping.setReferenceClass(Address.class);
        xmlDescriptor.addMapping(addressMapping);

        XMLCompositeCollectionMapping phoneNumberMapping = new XMLCompositeCollectionMapping();
        phoneNumberMapping.setAttributeName("phoneNumbers");
        phoneNumberMapping.setXPath("ns:contact-info/ns:phone-number");
        phoneNumberMapping.setReferenceClass(PhoneNumber.class);
        xmlDescriptor.addMapping(phoneNumberMapping);

        return xmlDescriptor;
     }

    private XMLDescriptor getAddressDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Address.class);

        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("ns", "urn:customer");
        xmlDescriptor.setNamespaceResolver(namespaceResolver);

        XMLDirectMapping street1Mapping = new XMLDirectMapping();
        street1Mapping.setAttributeName("street1");
        street1Mapping.setXPath("ns:street[1]/text()");
        xmlDescriptor.addMapping(street1Mapping);

        XMLDirectMapping street2Mapping = new XMLDirectMapping();
        street2Mapping.setAttributeName("street2");
        street2Mapping.setXPath("ns:street[2]/text()");
        xmlDescriptor.addMapping(street2Mapping);

        XMLDirectMapping cityMapping = new XMLDirectMapping();
        cityMapping.setAttributeName("city");
        cityMapping.setXPath("ns:city/text()");
        xmlDescriptor.addMapping(cityMapping);

        return xmlDescriptor;
    }

    private XMLDescriptor getPhoneNumberDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(PhoneNumber.class);

        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("ns", "urn:customer");
        xmlDescriptor.setNamespaceResolver(namespaceResolver);

        XMLDirectMapping valueMapping = new XMLDirectMapping();
        valueMapping.setAttributeName("value");
        valueMapping.setXPath("text()");
        xmlDescriptor.addMapping(valueMapping);

        return xmlDescriptor;
    }

    private XMLDescriptor getEmergencyContactDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(EmergencyContact.class);

        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("ns", "urn:customer");
        xmlDescriptor.setNamespaceResolver(namespaceResolver);

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setXPath("ns:emergency-contact/text()");
        xmlDescriptor.addMapping(nameMapping);

        return xmlDescriptor;
    }

}
