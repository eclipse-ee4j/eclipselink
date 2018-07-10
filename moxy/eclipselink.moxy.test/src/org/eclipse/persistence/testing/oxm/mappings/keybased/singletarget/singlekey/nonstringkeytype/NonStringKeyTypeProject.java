/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey.nonstringkeytype;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLObjectReferenceMapping;
import org.eclipse.persistence.sessions.Project;

import org.eclipse.persistence.testing.oxm.mappings.keybased.Root;

public class NonStringKeyTypeProject extends Project {
    public NonStringKeyTypeProject() {
        addDescriptor(getRootDescriptor());
        addDescriptor(getEmployeeDescriptor());
        addDescriptor(getAddressDescriptor());
    }

    private XMLDescriptor getRootDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Root.class);
        descriptor.setDefaultRootElement("root");
        // create employee mapping
        XMLCompositeObjectMapping empMapping = new XMLCompositeObjectMapping();
        empMapping.setAttributeName("employee");
        empMapping.setXPath("employee");
        empMapping.setReferenceClass(Employee.class);
        descriptor.addMapping(empMapping);
        // create address mapping
        XMLCompositeCollectionMapping addMapping = new XMLCompositeCollectionMapping();
        addMapping.setAttributeName("addresses");
        addMapping.setXPath("address");
        addMapping.setReferenceClass(Address.class);
        descriptor.addMapping(addMapping);
        return descriptor;
    }

    private XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Employee.class);
        descriptor.setDefaultRootElement("employee");
        // create id mapping
        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("@id");
        descriptor.addMapping(idMapping);
        // create name mapping
        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setXPath("name/text()");
        descriptor.addMapping(nameMapping);
        // create address mapping
        XMLObjectReferenceMapping addressMapping = new XMLObjectReferenceMapping();
        addressMapping.setAttributeName("address");
        addressMapping.setReferenceClass(Address.class);
        addressMapping.addSourceToTargetKeyFieldAssociation("@address-id", "@aid");
        descriptor.addMapping(addressMapping);
        return descriptor;
    }

    private XMLDescriptor getAddressDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Address.class);
        descriptor.addPrimaryKeyFieldName("@aid");
        descriptor.setDefaultRootElement("address");
        // create id mapping
        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("@aid");
        descriptor.addMapping(idMapping);
        // create street mapping
        XMLDirectMapping streetMapping = new XMLDirectMapping();
        streetMapping.setAttributeName("street");
        streetMapping.setXPath("street/text()");
        descriptor.addMapping(streetMapping);
        // create city mapping
        XMLDirectMapping cityMapping = new XMLDirectMapping();
        cityMapping.setAttributeName("city");
        cityMapping.setXPath("city/text()");
        descriptor.addMapping(cityMapping);
        // create country mapping
        XMLDirectMapping countryMapping = new XMLDirectMapping();
        countryMapping.setAttributeName("country");
        countryMapping.setXPath("country/text()");
        descriptor.addMapping(countryMapping);
        // create zip mapping
        XMLDirectMapping zipMapping = new XMLDirectMapping();
        zipMapping.setAttributeName("zip");
        zipMapping.setXPath("zip/text()");
        descriptor.addMapping(zipMapping);
        return descriptor;
    }
}
