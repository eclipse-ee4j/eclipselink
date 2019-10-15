/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Vikram Bhatia
package org.eclipse.persistence.testing.jaxb.xmlenum;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="employee")
public class EmployeeSingleDepartmentChoice {
    public String name;

    @XmlElements ({
        @XmlElement(name="department-number", type=Department.class)
    })
    public Department department;

    public boolean equals(Object o) {
        if(!(o instanceof EmployeeSingleDepartmentChoice) || o == null) {
            return false;
        } else {
            return ((EmployeeSingleDepartmentChoice)o).department == this.department;
        }
    }

    public String toString() {
        return "EMPLOYEE(" + department + ")";
    }
}
