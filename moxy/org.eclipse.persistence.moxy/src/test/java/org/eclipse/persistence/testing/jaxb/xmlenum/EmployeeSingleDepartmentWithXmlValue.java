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
//     Oracle - August 26, 2009 initial test case
package org.eclipse.persistence.testing.jaxb.xmlenum;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name="employee")
public class EmployeeSingleDepartmentWithXmlValue {
    @XmlTransient
    public String name;

    @XmlValue
    public Department department;

    public boolean equals(Object o) {
        if(!(o instanceof EmployeeSingleDepartmentWithXmlValue) || o == null) {
            return false;
        } else {
            return ((EmployeeSingleDepartmentWithXmlValue)o).department == this.department;
        }
    }

    public String toString() {
        return "EMPLOYEE(" + department + ")";
    }
}
