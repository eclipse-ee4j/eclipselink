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
package org.eclipse.persistence.testing.oxm.classloader;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class EmployeeProject extends Project {

    public EmployeeProject() {
        super();
        this.addDescriptor(getEmployeeDescriptor());
    }

    private XMLDescriptor getEmployeeDescriptor() {
        try {
            ClassLoader employeeClassLoader = new JARClassLoader("org/eclipse/persistence/testing/oxm/classloader/Employee.jar");
            Class employeeClass = employeeClassLoader.loadClass("org.eclipse.persistence.testing.oxm.classloader.Employee");

            XMLDescriptor xmlDescriptor = new XMLDescriptor();
            xmlDescriptor.setJavaClass(employeeClass);
            xmlDescriptor.setDefaultRootElement("employee");

            XMLDirectMapping nameMapping = new XMLDirectMapping();
            nameMapping.setAttributeName("name");
            nameMapping.setXPath("text()");
            xmlDescriptor.addMapping(nameMapping);

            return xmlDescriptor;
        } catch(ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
