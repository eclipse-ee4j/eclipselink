/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.unmapped;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class EmployeeProject extends Project {
    XMLDescriptor employeeDescriptor;

    public EmployeeProject() {
        this.addDescriptor(getEmployeeDescriptor());
    }

    public XMLDescriptor getEmployeeDescriptor() {
        if (null == employeeDescriptor) {
            employeeDescriptor = new XMLDescriptor();
            employeeDescriptor.setJavaClass(Employee.class);
            employeeDescriptor.setDefaultRootElement("employee");

            XMLDirectMapping fnMapping = new XMLDirectMapping();
            fnMapping.setAttributeName("firstName");
            fnMapping.setXPath("first-name/text()");
            employeeDescriptor.addMapping(fnMapping);

            XMLDirectMapping lnMapping = new XMLDirectMapping();
            lnMapping.setAttributeName("lastName");
            lnMapping.setXPath("last-name/text()");
            employeeDescriptor.addMapping(lnMapping);
        }
        return employeeDescriptor;
    }
}
