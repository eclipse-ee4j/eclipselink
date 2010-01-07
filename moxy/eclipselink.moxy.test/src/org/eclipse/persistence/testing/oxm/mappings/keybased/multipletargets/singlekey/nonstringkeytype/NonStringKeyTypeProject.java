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
package org.eclipse.persistence.testing.oxm.mappings.keybased.multipletargets.singlekey.nonstringkeytype;

import java.util.ArrayList;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.*;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.keybased.Root;
import org.eclipse.persistence.testing.oxm.mappings.keybased.singletarget.singlekey.nonstringkeytype.Address;

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
        XMLCollectionReferenceMapping addressMapping = new XMLCollectionReferenceMapping();
        addressMapping.useCollectionClass(ArrayList.class);
        addressMapping.setAttributeName("addresses");
        addressMapping.setReferenceClass(Address.class);
        addressMapping.addSourceToTargetKeyFieldAssociation("@address-ids", "@aid");
        addressMapping.setUsesSingleNode(true);
        descriptor.addMapping(addressMapping);
        return descriptor;
    }

    private XMLDescriptor getAddressDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Address.class);
        descriptor.addPrimaryKeyFieldName("@aid");
        descriptor.setDefaultRootElement("address");
        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("@aid");
        descriptor.addMapping(idMapping);
        XMLDirectMapping streetMapping = new XMLDirectMapping();
        streetMapping.setAttributeName("street");
        streetMapping.setXPath("street/text()");
        descriptor.addMapping(streetMapping);
        XMLDirectMapping cityMapping = new XMLDirectMapping();
        cityMapping.setAttributeName("city");
        cityMapping.setXPath("city/text()");
        descriptor.addMapping(cityMapping);
        XMLDirectMapping countryMapping = new XMLDirectMapping();
        countryMapping.setAttributeName("country");
        countryMapping.setXPath("country/text()");
        descriptor.addMapping(countryMapping);
        XMLDirectMapping zipMapping = new XMLDirectMapping();
        zipMapping.setAttributeName("zip");
        zipMapping.setXPath("zip/text()");
        descriptor.addMapping(zipMapping);
        return descriptor;
    }
}
