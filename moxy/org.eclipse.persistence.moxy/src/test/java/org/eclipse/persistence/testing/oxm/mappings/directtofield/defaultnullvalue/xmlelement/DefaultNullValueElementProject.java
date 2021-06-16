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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.defaultnullvalue.xmlelement;

import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.defaultnullvalue.Employee;

public class DefaultNullValueElementProject extends Project {
    public final static int CONTROL_ID = -1;
    public final static String CONTROL_FIRSTNAME = "";

    public DefaultNullValueElementProject() {
        super();
        this.addDescriptor(getEmployeeDescriptor());
    }

    private XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Employee.class);
        xmlDescriptor.setDefaultRootElement("employee");

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("id/text()");
        idMapping.setNullValue(new Integer(CONTROL_ID));
        xmlDescriptor.addMapping(idMapping);

        XMLDirectMapping numericNoNullValueMapping = new XMLDirectMapping();
        numericNoNullValueMapping.setAttributeName("numericNoNullValue");
        numericNoNullValueMapping.setXPath("numeric-no-null-value/text()");
        //numericNoNullValueMapping.setNullValue(new Integer(CONTROL_ID));
        xmlDescriptor.addMapping(numericNoNullValueMapping);

        XMLDirectMapping firstNameMapping = new XMLDirectMapping();
        firstNameMapping.setAttributeName("firstName");
        firstNameMapping.setXPath("first-name/text()");
        firstNameMapping.setNullValue(CONTROL_FIRSTNAME);
        xmlDescriptor.addMapping(firstNameMapping);

        return xmlDescriptor;
    }
}
