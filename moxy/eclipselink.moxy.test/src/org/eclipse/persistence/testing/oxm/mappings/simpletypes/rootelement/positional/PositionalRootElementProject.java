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
package org.eclipse.persistence.testing.oxm.mappings.simpletypes.rootelement.positional;

// TopLink imports
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;

import org.eclipse.persistence.testing.oxm.mappings.simpletypes.rootelement.Employee;

public class PositionalRootElementProject extends Project {
    public PositionalRootElementProject() {
        super();
        this.addDescriptor(getEmployeeDescriptor());
    }

        XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Employee.class);
        xmlDescriptor.setDefaultRootElement("employee");
        xmlDescriptor.setShouldPreserveDocument(true);

        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("name");
        mapping.setXPath("text()");
        xmlDescriptor.addMapping(mapping);

        XMLDirectMapping agemapping = new XMLDirectMapping();
        agemapping.setAttributeName("age");
        agemapping.setXPath("age/text()");
        xmlDescriptor.addMapping(agemapping);

        XMLDirectMapping lastNameMapping = new XMLDirectMapping();
        lastNameMapping.setAttributeName("lastName");
        lastNameMapping.setXPath("text()[2]");
        xmlDescriptor.addMapping(lastNameMapping);

        return xmlDescriptor;
    }

}
