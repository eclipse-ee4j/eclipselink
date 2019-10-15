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
package org.eclipse.persistence.testing.oxm.converter.jaxbenum;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.jaxb.JAXBEnumTypeConverter;
import org.eclipse.persistence.jaxb.JAXBTypesafeEnumConverter;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.converter.typesafeenum.Employee;
import org.eclipse.persistence.testing.oxm.converter.typesafeenum.MyTypeSafeEnumClass;
public class JAXBEnumTypeConverterProject extends Project {
    public JAXBEnumTypeConverterProject() {
        super();
        addDescriptor(getEmployeeDescriptor());
    }

    public ClassDescriptor getEmployeeDescriptor() {
         XMLDescriptor descriptor = new XMLDescriptor();
         descriptor.setDefaultRootElement("employee");
         descriptor.setJavaClass(Employee.class);

         XMLDirectMapping firstNameMapping = new XMLDirectMapping();
         firstNameMapping.setAttributeName("firstName");
         firstNameMapping.setXPath("first-name/text()");

         JAXBEnumTypeConverter converter = new JAXBEnumTypeConverter(firstNameMapping,"theClassName", false);
         ((XMLDirectMapping)firstNameMapping).setConverter(converter);

         descriptor.addMapping(firstNameMapping);

         return descriptor;
    }
}
