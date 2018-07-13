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
//     bdoughan - August 6/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.choice.converter;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLChoiceObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.choice.Address;
import org.eclipse.persistence.testing.oxm.mappings.choice.Employee;

public class EmployeeProject extends Project {

    public EmployeeProject() {
        addDescriptor(getEmployeeDescriptor());
        addDescriptor(getAddressDescriptor());
    }

    private XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Employee.class);
        descriptor.setDefaultRootElement("employee");

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setXPath("name/text()");
        descriptor.addMapping(nameMapping);

        XMLChoiceObjectMapping choiceMapping = new XMLChoiceObjectMapping();
        choiceMapping.setAttributeName("choice");
        choiceMapping.addChoiceElement("street/text()", String.class);
        choiceMapping.addChoiceElement("address", Address.class);
        choiceMapping.addChoiceElement("integer/text()", Integer.class);
        choiceMapping.setConverter(new WrapperConverter());
        descriptor.addMapping(choiceMapping);

        XMLDirectMapping phoneMapping = new XMLDirectMapping();
        phoneMapping.setAttributeName("phone");
        phoneMapping.setXPath("phone/text()");
        descriptor.addMapping(phoneMapping);

        return descriptor;
    }

    private XMLDescriptor getAddressDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Address.class);

        XMLDirectMapping streetMapping = new XMLDirectMapping();
        streetMapping.setAttributeName("street");
        streetMapping.setXPath("street/text()");
        descriptor.addMapping(streetMapping);

        XMLDirectMapping cityMapping = new XMLDirectMapping();
        cityMapping.setAttributeName("city");
        cityMapping.setXPath("city/text()");
        descriptor.addMapping(cityMapping);

        return descriptor;
    }

}
