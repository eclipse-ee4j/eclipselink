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

import java.util.List;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="employee")
public class EmployeeMultipleDepartmentChoice {
    public String name;

    @XmlElements ({
        @XmlElement(name="department-number", type=Department.class)
    })
    public List<Object> departments;

    public boolean equals(Object o) {
        if(!(o instanceof EmployeeMultipleDepartmentChoice) || o == null) {
            return false;
        } else {
            return ((EmployeeMultipleDepartmentChoice)o).departments.equals(this.departments);
        }
    }

    public String toString() {
        return "EMPLOYEE(" + departments + ")";
    }
}
