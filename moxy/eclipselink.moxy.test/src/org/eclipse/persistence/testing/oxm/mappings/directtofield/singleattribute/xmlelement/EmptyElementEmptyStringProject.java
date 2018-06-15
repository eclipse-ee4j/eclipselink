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
//     rbarkhouse - 2009-06-19 14:33:00 - initial implementation
package org.eclipse.persistence.testing.oxm.mappings.directtofield.singleattribute.xmlelement;

import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.sessions.Project;

public class EmptyElementEmptyStringProject extends Project {

    public EmptyElementEmptyStringProject() {
        addDescriptor(getEmployeeDescriptor());
    }

    private XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(EmptyElementEmptyStringEmployee.class);
        descriptor.setDefaultRootElement("employee");

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("id/text()");
        idMapping.getNullPolicy().setNullRepresentedByEmptyNode(false);
        descriptor.addMapping(idMapping);

        return descriptor;
    }

}
