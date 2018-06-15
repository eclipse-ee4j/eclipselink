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
// dsmith  - Dec 17/2008 - 1.1 - Initial implementation
// dmccann - Dec 31/2008 - 1.1 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.directtofield.leafelement;

import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class DirectWithLeafElementProject extends Project {
    public DirectWithLeafElementProject() {
        addDescriptor(getEmployeeDescriptor());
    }

    private XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Employee.class);
        descriptor.setDefaultRootElement("employee");

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("id/text()");
        descriptor.addMapping(idMapping);

        XMLDirectMapping birthDateMapping = new XMLDirectMapping();
        birthDateMapping.setAttributeName("birthdate");
        XMLField xmlField = new XMLField("birthdate/text()");
        xmlField.setLeafElementType(XMLConstants.DATE_TIME_QNAME);
        birthDateMapping.setField(xmlField);
        //birthDateMapping.setXPath("birthdate/text()");
        descriptor.addMapping(birthDateMapping);

        XMLDirectMapping firstNameMapping = new XMLDirectMapping();
        firstNameMapping.setAttributeName("firstName");
        firstNameMapping.setXPath("firstname/text()");
        descriptor.addMapping(firstNameMapping);

        XMLDirectMapping lastNameMapping = new XMLDirectMapping();
        lastNameMapping.setAttributeName("lastName");
        lastNameMapping.setXPath("lastname/text()");
        descriptor.addMapping(lastNameMapping);

        return descriptor;
    }

}
