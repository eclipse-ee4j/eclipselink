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
//     Oracle - August 26, 2009 initial test case
package org.eclipse.persistence.testing.jaxb.xmlenum;

import javax.xml.bind.annotation.*;

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
