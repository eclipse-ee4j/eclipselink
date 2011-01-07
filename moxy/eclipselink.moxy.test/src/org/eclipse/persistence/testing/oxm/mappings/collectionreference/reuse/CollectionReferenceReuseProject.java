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
 *     rbarkhouse - 2009-10-07 13:24:58 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.collectionreference.reuse;

import java.util.ArrayList;
import java.util.LinkedList;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCollectionReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class CollectionReferenceReuseProject extends Project {

    public CollectionReferenceReuseProject() {
        addDescriptor(getEmployeeDescriptor());
        addDescriptor(getAddressDescriptor());
        addDescriptor(getRootDescriptor());
    }

    private XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Employee.class);
        descriptor.setDefaultRootElement("employee");

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("@id");
        descriptor.addMapping(idMapping);

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setXPath("name/text()");
        descriptor.addMapping(nameMapping);

        XMLCollectionReferenceMapping addressesMapping = new XMLCollectionReferenceMapping();
        addressesMapping.setAttributeName("addresses");
        addressesMapping.setReferenceClass(Address.class);
        addressesMapping.addSourceToTargetKeyFieldAssociation("address-id/text()", "@id");
        addressesMapping.setReuseContainer(true);
        descriptor.addMapping(addressesMapping);

        return descriptor;
    }

    private XMLDescriptor getAddressDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Address.class);
        descriptor.addPrimaryKeyFieldName("@id");
        descriptor.setDefaultRootElement("address");

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("@id");
        descriptor.addMapping(idMapping);

        XMLDirectMapping infoMapping = new XMLDirectMapping();
        infoMapping.setAttributeName("info");
        infoMapping.setXPath("info/text()");
        descriptor.addMapping(infoMapping);

        XMLDirectMapping typeMapping = new XMLDirectMapping();
        typeMapping.setAttributeName("type");
        typeMapping.setXPath("type/text()");
        descriptor.addMapping(typeMapping);

        return descriptor;
    }

    private XMLDescriptor getRootDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Root.class);
        descriptor.setDefaultRootElement("root");

        XMLCompositeObjectMapping empMapping = new XMLCompositeObjectMapping();
        empMapping.setAttributeName("employee");
        empMapping.setXPath("employee");
        empMapping.setReferenceClass(Employee.class);
        descriptor.addMapping(empMapping);

        XMLCompositeCollectionMapping addMapping = new XMLCompositeCollectionMapping();
        addMapping.setAttributeName("addresses");
        addMapping.setXPath("address");
        addMapping.setReferenceClass(Address.class);
        descriptor.addMapping(addMapping);

        return descriptor;
    }

}