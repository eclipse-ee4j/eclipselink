/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.readonly;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

import java.util.Vector;

public class TwoCompositeCollectionMappingProject extends Project
{
    public TwoCompositeCollectionMappingProject()
    {
        super();
        addEmployeeDescriptor();
        addAddressDescriptor();
    }

    public void addEmployeeDescriptor()
    {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Employee.class);
        descriptor.setDefaultRootElement("employee");

        XMLCompositeCollectionMapping addr = new XMLCompositeCollectionMapping();
        addr.setAttributeName("otherAddresses");
        addr.setXPath("addresses/address");
        addr.setReferenceClass(Address.class);
        addr.getContainerPolicy().setContainerClass(Vector.class);
        addr.readOnly();
        descriptor.addMapping(addr);

        XMLCompositeCollectionMapping addr2 = new XMLCompositeCollectionMapping();
        addr2.setAttributeName("otherAddresses2");
        addr2.setXPath("addresses/address");
        addr2.setReferenceClass(Address.class);
        addr2.getContainerPolicy().setContainerClass(Vector.class);
        descriptor.addMapping(addr2);

        this.addDescriptor(descriptor);
    }
    public void addAddressDescriptor()
    {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Address.class);

        XMLDirectMapping street = new XMLDirectMapping();
        street.setAttributeName("street");
        street.setXPath("street/text()");
        descriptor.addMapping(street);

        this.addDescriptor(descriptor);
    }
}
