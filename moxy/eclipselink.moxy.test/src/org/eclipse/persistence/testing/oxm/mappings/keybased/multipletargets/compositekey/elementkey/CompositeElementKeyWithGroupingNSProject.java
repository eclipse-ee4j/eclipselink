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
*     bdoughan - Oct 22/2009 - 2.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.compositekey.elementkey;

import java.util.ArrayList;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCollectionReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.keybased.Address;
import org.eclipse.persistence.testing.oxm.mappings.keybased.Root;
import org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.Employee;

public class CompositeElementKeyWithGroupingNSProject extends Project {

    public CompositeElementKeyWithGroupingNSProject() {
        addDescriptor(getRootDescriptor());
        addDescriptor(getEmployeeDescriptor());
        addDescriptor(getAddressDescriptor());
    }

    private XMLDescriptor getRootDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Root.class);
        descriptor.setDefaultRootElement("ns:root");
        // create namespace resolver
        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("ns", "urn:example");
        descriptor.setNamespaceResolver(nsResolver);
        // create employee mapping
        XMLCompositeObjectMapping empMapping = new XMLCompositeObjectMapping();
        empMapping.setAttributeName("employee");
        empMapping.setXPath("ns:employee");
        empMapping.setReferenceClass(Employee.class);
        descriptor.addMapping(empMapping);
        // create address mapping
        XMLCompositeCollectionMapping addMapping = new XMLCompositeCollectionMapping();
        addMapping.setAttributeName("addresses");
        addMapping.setXPath("ns:address");
        addMapping.setReferenceClass(Address.class);
        descriptor.addMapping(addMapping);
        return descriptor;
    }

    private XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Employee.class);
        descriptor.setDefaultRootElement("employee");
        // create namespace resolver
        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("ns", "urn:example");
        descriptor.setNamespaceResolver(nsResolver);
        // create id mapping
        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("@id");
        descriptor.addMapping(idMapping);
        // create name mapping
        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setXPath("ns:name/text()");
        descriptor.addMapping(nameMapping);
        // create address mapping
        XMLCollectionReferenceMapping addressMapping = new XMLCollectionReferenceMapping();
        addressMapping.useCollectionClass(ArrayList.class);
        addressMapping.setAttributeName("addresses");
        addressMapping.setXPath("ns:grouping-element");
        addressMapping.setReferenceClass(Address.class);
        addressMapping.addSourceToTargetKeyFieldAssociation("ns:address/ns:id-city/text()", "ns:city/text()");
        addressMapping.addSourceToTargetKeyFieldAssociation("ns:address/ns:id-zip/text()", "ns:zip/text()");
        descriptor.addMapping(addressMapping);
        return descriptor;
    }

    private XMLDescriptor getAddressDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Address.class);
        descriptor.addPrimaryKeyFieldName("ns:city/text()");
        descriptor.addPrimaryKeyFieldName("ns:zip/text()");
        descriptor.setDefaultRootElement("address");
        // create namespace resolver
        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.put("ns", "urn:example");
        descriptor.setNamespaceResolver(nsResolver);
        // create id mapping
        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("@aid");
        descriptor.addMapping(idMapping);
        // create street mapping
        XMLDirectMapping streetMapping = new XMLDirectMapping();
        streetMapping.setAttributeName("street");
        streetMapping.setXPath("ns:street/text()");
        descriptor.addMapping(streetMapping);
        // create city mapping
        XMLDirectMapping cityMapping = new XMLDirectMapping();
        cityMapping.setAttributeName("city");
        cityMapping.setXPath("ns:city/text()");
        descriptor.addMapping(cityMapping);
        // create country mapping
        XMLDirectMapping countryMapping = new XMLDirectMapping();
        countryMapping.setAttributeName("country");
        countryMapping.setXPath("ns:country/text()");
        descriptor.addMapping(countryMapping);
        // create zip mapping
        XMLDirectMapping zipMapping = new XMLDirectMapping();
        zipMapping.setAttributeName("zip");
        zipMapping.setXPath("ns:zip/text()");
        descriptor.addMapping(zipMapping);
        return descriptor;
    }

}