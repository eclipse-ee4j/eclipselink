/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
