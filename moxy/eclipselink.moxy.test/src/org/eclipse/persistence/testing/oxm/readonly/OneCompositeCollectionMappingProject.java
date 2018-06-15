/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.readonly;

import java.util.Vector;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;

public class OneCompositeCollectionMappingProject extends Project
{
    public OneCompositeCollectionMappingProject()
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

