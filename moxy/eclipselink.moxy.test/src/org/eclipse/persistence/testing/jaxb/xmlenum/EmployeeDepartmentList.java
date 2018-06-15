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
package org.eclipse.persistence.testing.jaxb.xmlenum;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="employee")
public class EmployeeDepartmentList {
    public String name;

    @XmlElement(name="department-number")
    public java.util.ArrayList<Department> deps;

    public boolean equals(Object o) {
        if(!(o instanceof EmployeeDepartmentList) || o == null) {
            return false;
        } else {
            return ((EmployeeDepartmentList)o).deps.equals(this.deps);
        }
    }

    public String toString() {
        return "EMPLOYEE(" + deps + ")";
    }
}
