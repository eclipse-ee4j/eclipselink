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
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class AttributeOnTargetProject extends Project {
    public AttributeOnTargetProject() {
        super();
        addDescriptor(getEmployeeDescriptor());
        addDescriptor(getAddressDescriptor());
    }

    public XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Employee.class);
        xmlDescriptor.setDefaultRootElement("employee");

        XMLCompositeObjectMapping addressMapping = new XMLCompositeObjectMapping();
        addressMapping.setReferenceClass(Address.class);
        addressMapping.setAttributeName("address");
        addressMapping.setXPath(".");
        xmlDescriptor.addMapping(addressMapping);

        return xmlDescriptor;
    }

    public XMLDescriptor getAddressDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Address.class);

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("street");
        idMapping.setXPath("@street");
        xmlDescriptor.addMapping(idMapping);

        return xmlDescriptor;
    }
}
