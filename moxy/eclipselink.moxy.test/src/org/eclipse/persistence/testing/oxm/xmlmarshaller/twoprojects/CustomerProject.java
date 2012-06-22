/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.xmlmarshaller.twoprojects;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLAnyObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLAnyCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class CustomerProject extends Project {
    public CustomerProject() {
        super();
        this.addDescriptor(getAddressDescriptor());
        this.addDescriptor(getCustomerDescriptor());
    }

    private XMLDescriptor getAddressDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Address.class);
        xmlDescriptor.setDefaultRootElement("customer-address");

        XMLDirectMapping streetMapping = new XMLDirectMapping();
        streetMapping.setAttributeName("street");
        streetMapping.setXPath("customer-street/text()");
        xmlDescriptor.addMapping(streetMapping);

        XMLDirectMapping cityMapping = new XMLDirectMapping();
        cityMapping.setAttributeName("city");
        cityMapping.setXPath("customer-city/text()");
        xmlDescriptor.addMapping(cityMapping);

        return xmlDescriptor;
    }

    private XMLDescriptor getCustomerDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Customer.class);
        xmlDescriptor.setDefaultRootElement("customer");

        XMLCompositeObjectMapping addressMapping = new XMLCompositeObjectMapping();
        addressMapping.setReferenceClass(Address.class);
        addressMapping.setAttributeName("address");
        addressMapping.setXPath("customer-address");
        xmlDescriptor.addMapping(addressMapping);

        XMLCompositeCollectionMapping addressesMapping = new XMLCompositeCollectionMapping();
        addressesMapping.setReferenceClass(Address.class);
        addressesMapping.setAttributeName("addresses");
        addressesMapping.setXPath("customer-addresses/customer-address");
        xmlDescriptor.addMapping(addressesMapping);
        
        XMLAnyObjectMapping anyMapping = new XMLAnyObjectMapping();
        anyMapping.setAttributeName("any");
        anyMapping.setXPath("any-object");
        xmlDescriptor.addMapping(anyMapping);

        XMLAnyCollectionMapping anyCollectionMapping = new XMLAnyCollectionMapping();
        anyCollectionMapping.setAttributeName("anyCollection");
        anyCollectionMapping.setXPath("any-collection");
        xmlDescriptor.addMapping(anyCollectionMapping);

        return xmlDescriptor;
    }
}
