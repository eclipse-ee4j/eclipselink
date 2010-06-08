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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.map;

import java.util.TreeMap;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.sessions.Project;

public class CompositeCollectionMapProject extends Project {
    public CompositeCollectionMapProject() {
        addDescriptor(getEmployeeDescriptor());
        addDescriptor(getMailingAddressDescriptor());
        addDescriptor(getAddressInfoDescriptor());
    }

    private XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Employee.class);
        descriptor.setDefaultRootElement("employee");

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("@id");
        descriptor.addMapping(idMapping);

        XMLCompositeCollectionMapping mailingMapping = new XMLCompositeCollectionMapping();
        mailingMapping.setAttributeName("addresses");
        mailingMapping.setReferenceClass(MailingAddress.class);
        mailingMapping.useMapClass(TreeMap.class, "getAddressType");

        mailingMapping.setXPath("mailing-address");
        descriptor.addMapping(mailingMapping);

        return descriptor;
    }

    private XMLDescriptor getMailingAddressDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(MailingAddress.class);

        XMLDirectMapping typeMapping = new XMLDirectMapping();
        typeMapping.setAttributeName("addressType");
        typeMapping.setXPath("@type");
        descriptor.addMapping(typeMapping);

        XMLDirectMapping testMapping = new XMLDirectMapping();
        testMapping.setAttributeName("test");
        testMapping.setXPath("@test");
        descriptor.addMapping(testMapping);

        XMLCompositeObjectMapping infoMapping = new XMLCompositeObjectMapping();
        infoMapping.setAttributeName("addressInfo");
        infoMapping.setReferenceClass(AddressInfo.class);
        infoMapping.setXPath("info");
        descriptor.addMapping(infoMapping);

        return descriptor;
    }

    private XMLDescriptor getAddressInfoDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(AddressInfo.class);

        XMLDirectMapping streetMapping = new XMLDirectMapping();
        streetMapping.setAttributeName("street");
        streetMapping.setXPath("street/text()");
        descriptor.addMapping(streetMapping);

        XMLDirectMapping cityMapping = new XMLDirectMapping();
        cityMapping.setAttributeName("city");
        cityMapping.setXPath("city/text()");
        descriptor.addMapping(cityMapping);

        XMLDirectMapping provinceMapping = new XMLDirectMapping();
        provinceMapping.setAttributeName("province");
        provinceMapping.setXPath("province/text()");
        descriptor.addMapping(provinceMapping);

        XMLDirectMapping postalCodeMapping = new XMLDirectMapping();
        postalCodeMapping.setAttributeName("postalCode");
        postalCodeMapping.setXPath("postal-code/text()");
        descriptor.addMapping(postalCodeMapping);

        return descriptor;
    }
}
